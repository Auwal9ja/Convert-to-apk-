package com.example

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
import com.example.ui.DuaViewModel
import com.example.ui.DuaViewModelFactory
import com.example.ui.components.InterstitialAdHelper
import com.example.ui.components.RewardedAdHelper
import com.example.ui.screens.MainScreen
import com.example.ui.theme.MyApplicationTheme
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize Google Mobile Ads SDK
    MobileAds.initialize(this) {}
    InterstitialAdHelper.loadAd(this)
    RewardedAdHelper.loadAd(this)

    // Ensure notification channels & exact alarms are scheduled if enabled
    com.example.receiver.MandatoryAdhkarManager.createNotificationChannel(this)
    com.example.receiver.MandatoryAdhkarManager.scheduleAlarms(this)
    com.example.receiver.ReminderReceiver.rescheduleAllIfEnabled(this)

    // Initialize database, repository, and ViewModel using constructor injection
    val database = DuaDatabase.getDatabase(this)
    val repository = DuaRepository(database.duaDao(), lifecycleScope)
    val sharedPrefs = getSharedPreferences("hisnul_muslim_prefs", MODE_PRIVATE)
    val factory = DuaViewModelFactory(repository, sharedPrefs)
    val viewModel = ViewModelProvider(this, factory)[DuaViewModel::class.java]

    setContent {
      val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

      MyApplicationTheme(darkTheme = isDarkTheme) {
        MainScreen(
          viewModel = viewModel,
          isDarkTheme = isDarkTheme,
          onToggleTheme = { viewModel.toggleDarkTheme(it) }
        )
      }
    }
  }
}

