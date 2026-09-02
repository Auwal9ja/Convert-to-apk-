package com.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.data.local.DuaDatabase
import com.example.data.repository.DuaRepository
import com.example.receiver.OneSignalHelper
import com.example.ui.DuaViewModel
import com.example.ui.DuaViewModelFactory
import com.example.ui.components.InterstitialAdHelper
import com.example.ui.components.RewardedAdHelper
import com.example.ui.screens.MainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.InAppUpdateManager
import com.example.util.UpdateState
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
  private var viewModel: DuaViewModel? = null
  private lateinit var inAppUpdateManager: InAppUpdateManager

  private val inAppUpdateLauncher = registerForActivityResult(
    ActivityResultContracts.StartIntentSenderForResult()
  ) { result ->
    if (result.resultCode != RESULT_OK) {
      Log.w("MainActivity", "In-app update flow failed or was cancelled by user: code=${result.resultCode}")
    } else {
      Log.d("MainActivity", "In-app update flow completed successfully")
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize Google Mobile Ads SDK
    MobileAds.initialize(this) {}
    InterstitialAdHelper.scheduleAppLaunchAd(this)
    RewardedAdHelper.loadAd(this)

    // Ensure notification channels & exact alarms are scheduled if enabled
    com.example.receiver.MandatoryAdhkarManager.createNotificationChannel(this)
    com.example.receiver.MandatoryAdhkarManager.recoverAndRescheduleAll(this, "APP_STARTUP")
    com.example.receiver.ReminderReceiver.rescheduleAllIfEnabled(this)

    // Request push notification permission for OneSignal & daily reminders
    OneSignalHelper.requestPushPermission(fallbackToSettings = false)

    // Initialize In-App Update Manager & check for background updates cleanly
    inAppUpdateManager = InAppUpdateManager(this)
    inAppUpdateManager.checkForUpdates(isManual = false)

    // Initialize database, repository, and ViewModel using constructor injection
    val database = DuaDatabase.getDatabase(this)
    val repository = DuaRepository(database.duaDao(), lifecycleScope)
    val sharedPrefs = getSharedPreferences("hisnul_muslim_prefs", MODE_PRIVATE)
    val factory = DuaViewModelFactory(repository, sharedPrefs)
    val vm = ViewModelProvider(this, factory)[DuaViewModel::class.java]
    this.viewModel = vm

    // Handle deep link / push notification extras
    handleIntentExtras(intent, vm)

    setContent {
      val isDarkTheme by vm.isDarkTheme.collectAsStateWithLifecycle()
      val updateState by inAppUpdateManager.updateState.collectAsStateWithLifecycle()

      MyApplicationTheme(darkTheme = isDarkTheme) {
        MainScreen(
          viewModel = vm,
          isDarkTheme = isDarkTheme,
          onToggleTheme = { vm.toggleDarkTheme(it) },
          updateState = updateState,
          onCheckForUpdates = { inAppUpdateManager.checkForUpdates(isManual = true) },
          onStartUpdate = {
            val state = inAppUpdateManager.updateState.value
            if (state is UpdateState.UpdateAvailable) {
              inAppUpdateManager.startFlexibleUpdate(this@MainActivity, state.appUpdateInfo, inAppUpdateLauncher)
            }
          },
          onCompleteUpdate = {
            inAppUpdateManager.completeUpdate()
          }
        )
      }
    }
  }

  override fun onResume() {
    super.onResume()
    if (::inAppUpdateManager.isInitialized) {
      inAppUpdateManager.onResume(this)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    if (::inAppUpdateManager.isInitialized) {
      inAppUpdateManager.unregisterListener()
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    viewModel?.let { handleIntentExtras(intent, it) }
  }

  private fun handleIntentExtras(intent: Intent?, vm: DuaViewModel) {
    if (intent == null) return

    // Immediately stop and silence Athan audio if app was opened via Prayer Notification or click
    if (intent.getBooleanExtra(com.example.receiver.PrayerAlarmReceiver.EXTRA_STOP_ATHAN, false) ||
        intent.hasExtra(com.example.receiver.PrayerAlarmReceiver.EXTRA_PRAYER_ID) ||
        com.example.audio.AthanPlayer.isPlaying()
    ) {
      com.example.audio.AthanPlayer.stop()
      val notifId = intent.getIntExtra(com.example.receiver.PrayerAlarmReceiver.EXTRA_NOTIFICATION_ID, 0)
      if (notifId != 0) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as? android.app.NotificationManager
        notificationManager?.cancel(notifId)
      }
    }

    val targetDuaId = intent.getIntExtra("target_dua_id", -1).takeIf { it != -1 }
      ?: intent.getIntExtra("dua_id", -1).takeIf { it != -1 }
    val targetCategory = intent.getStringExtra("target_category")
      ?: intent.getStringExtra("category")

    if (targetDuaId != null) {
      vm.setTargetDuaId(targetDuaId)
    }
    if (!targetCategory.isNullOrBlank()) {
      vm.selectCategory(targetCategory)
    }
  }
}

