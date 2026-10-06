package com.example.ui

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.DuaEntity
import com.example.data.repository.DuaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DuaViewModel(
    private val repository: DuaRepository,
    private val sharedPrefs: SharedPreferences
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _targetDuaId = MutableStateFlow<Int?>(null)
    val targetDuaId: StateFlow<Int?> = _targetDuaId.asStateFlow()

    fun setTargetDuaId(id: Int?) {
        _targetDuaId.value = id
    }

    fun clearTargetDuaId() {
        _targetDuaId.value = null
    }

    private val _selectedLanguage = MutableStateFlow(sharedPrefs.getString("selected_language", "English") ?: "English")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _arabicFontSize = MutableStateFlow(sharedPrefs.getFloat("arabic_font_size", 24f))
    val arabicFontSize: StateFlow<Float> = _arabicFontSize.asStateFlow()

    private val _textFontSize = MutableStateFlow(sharedPrefs.getFloat("text_font_size", 16f))
    val textFontSize: StateFlow<Float> = _textFontSize.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(sharedPrefs.getBoolean("is_dark_theme", false))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleDarkTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
        sharedPrefs.edit().putBoolean("is_dark_theme", isDark).apply()
    }

    private val _isFirstLaunch = MutableStateFlow(sharedPrefs.getBoolean("is_first_launch", true))
    val isFirstLaunch: StateFlow<Boolean> = _isFirstLaunch.asStateFlow()

    // Dynamically query across cached local list based on searching and categorizing states
    val duas: StateFlow<List<DuaEntity>> = combine(
        _searchQuery,
        _selectedCategory,
        _selectedLanguage,
        repository.allDuas
    ) { query, category, lang, all ->
        var list = all
        if (category != null) {
            list = list.filter { 
                it.category.equals(category, ignoreCase = true) ||
                (category == "Marriage & Family" && (it.category == "Family & Marriage" || it.category == "Marriage & Family")) ||
                (category == "Repentance & Seeking Forgiveness" && (it.category == "Repentance & Istighfar" || it.category == "Repentance & Seeking Forgiveness"))
            }
            if (category.equals("Sleeping & Waking Up", ignoreCase = true)) {
                list = list.sortedWith(
                    compareBy { dua ->
                        when {
                            dua.id == 5 || dua.title.contains("Mulk", ignoreCase = true) || dua.arabic.contains("تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ") -> 0
                            dua.id == 6 || dua.title.contains("Falalar Suratul Mulk", ignoreCase = true) || dua.title.contains("Virtue of Surah Al-Mulk", ignoreCase = true) -> 1
                            dua.id == 57 || dua.title.contains("Ladubba", ignoreCase = true) || dua.title.contains("Etiquette", ignoreCase = true) -> 2
                            dua.id == 58 || dua.title.contains("Tasbih", ignoreCase = true) -> 3
                            dua.id == 59 -> 4
                            dua.id == 360 -> 5
                            dua.id == 361 -> 6
                            dua.id == 362 -> 7
                            dua.id == 363 -> 8
                            dua.id == 364 -> 9
                            dua.id == 365 -> 10
                            dua.id == 366 -> 11
                            dua.id == 367 -> 12
                            dua.id == 368 -> 13
                            dua.id == 369 -> 14
                            dua.id == 370 -> 15
                            dua.id == 371 -> 16
                            dua.id == 372 -> 17
                            dua.id == 373 -> 18
                            else -> 100 + dua.id
                        }
                    }
                )
            }
        }
        if (query.isNotEmpty()) {
            val q = query.trim()
            list = list.filter {
                it.title.contains(q, ignoreCase = true) ||
                com.example.data.local.AppLocalizer.getDuaTitle(it.id, it.title, lang).contains(q, ignoreCase = true) ||
                it.translation.contains(q, ignoreCase = true) ||
                it.translationHausa.contains(q, ignoreCase = true) ||
                it.translationYoruba.contains(q, ignoreCase = true) ||
                it.translationIgbo.contains(q, ignoreCase = true) ||
                it.transliteration.contains(q, ignoreCase = true) ||
                it.arabic.contains(q, ignoreCase = true) ||
                it.reference.contains(q, ignoreCase = true) ||
                it.category.contains(q, ignoreCase = true) ||
                com.example.data.local.AppLocalizer.getCategoryName(it.category, "Hausa").contains(q, ignoreCase = true) ||
                com.example.data.local.AppLocalizer.getCategoryName(it.category, "Yoruba").contains(q, ignoreCase = true) ||
                com.example.data.local.AppLocalizer.getCategoryName(it.category, "Igbo").contains(q, ignoreCase = true)
            }
        }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val favoriteDuas: StateFlow<List<DuaEntity>> = repository.favoriteDuas
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allDuas: StateFlow<List<DuaEntity>> = repository.allDuas
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val categories: StateFlow<List<String>> = repository.categories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun setLanguage(language: String) {
        _selectedLanguage.value = language
        sharedPrefs.edit().putString("selected_language", language).apply()
        try {
            val appCtx = com.example.HisnulMuslimApp.instance
            appCtx.getSharedPreferences("app_preferences", android.content.Context.MODE_PRIVATE)
                .edit()
                .putString("selected_language", language)
                .apply()
            appCtx.getSharedPreferences("tasbeeh_prefs", android.content.Context.MODE_PRIVATE)
                .edit()
                .putString("selected_language", language)
                .apply()
            com.example.receiver.OneSignalHelper.setUserLanguageTag(language)
            com.example.receiver.PrayerWidgetProvider.updateAllWidgets(appCtx)
            com.example.receiver.MandatoryAdhkarManager.recoverAndRescheduleAll(appCtx, "LANGUAGE_CHANGE")
            com.example.receiver.ReminderReceiver.rescheduleAllIfEnabled(appCtx)
        } catch (_: Exception) {}
    }

    fun setArabicFontSize(size: Float) {
        _arabicFontSize.value = size
        sharedPrefs.edit().putFloat("arabic_font_size", size).apply()
    }

    fun setTextFontSize(size: Float) {
        _textFontSize.value = size
        sharedPrefs.edit().putFloat("text_font_size", size).apply()
    }

    fun completeFirstLaunch() {
        _isFirstLaunch.value = false
        sharedPrefs.edit().putBoolean("is_first_launch", false).apply()
    }

    private val _completedDuas = MutableStateFlow<Set<Int>>(
        sharedPrefs.getStringSet("completed_duas", emptySet())
            ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
    )
    val completedDuas: StateFlow<Set<Int>> = _completedDuas.asStateFlow()

    fun toggleCompleted(id: Int) {
        val current = _completedDuas.value
        val updated = if (current.contains(id)) current - id else current + id
        _completedDuas.value = updated
        sharedPrefs.edit().putStringSet("completed_duas", updated.map { it.toString() }.toSet()).apply()
    }

    suspend fun getTranslationAndReference(dua: DuaEntity, language: String): Pair<String, String> {
        return repository.getTranslationAndReference(dua, language)
    }

    suspend fun getLocalizedDuaDetails(dua: DuaEntity, language: String): com.example.data.repository.LocalizedDuaDetails {
        return repository.getLocalizedDuaDetails(dua, language)
    }

    fun toggleFavorite(id: Int, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, !isCurrentlyFavorite)
        }
    }
}

class DuaViewModelFactory(
    private val repository: DuaRepository,
    private val sharedPrefs: SharedPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DuaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DuaViewModel(repository, sharedPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
