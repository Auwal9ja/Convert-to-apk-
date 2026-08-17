package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
  private var viewModel: DuaViewModel? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize Google Mobile Ads SDK
    MobileAds.initialize(this) {}
    InterstitialAdHelper.loadAd(this)
    RewardedAdHelper.loadAd(this)

    // Ensure notification channels & exact alarms are scheduled if enabled
    com.example.receiver.MandatoryAdhkarManager.createNotificationChannel(this)
    com.example.receiver.MandatoryAdhkarManager.recoverAndRescheduleAll(this, "APP_STARTUP")
    com.example.receiver.ReminderReceiver.rescheduleAllIfEnabled(this)

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

      MyApplicationTheme(darkTheme = isDarkTheme) {
        MainScreen(
          viewModel = vm,
          isDarkTheme = isDarkTheme,
          onToggleTheme = { vm.toggleDarkTheme(it) }
        )
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    viewModel?.let { handleIntentExtras(intent, it) }
  }

  private fun handleIntentExtras(intent: Intent?, vm: DuaViewModel) {
    if (intent == null) return
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

