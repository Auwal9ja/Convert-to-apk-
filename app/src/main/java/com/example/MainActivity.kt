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
import com.example.ui.screens.MainScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

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

