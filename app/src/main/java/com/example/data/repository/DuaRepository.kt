package com.example.data.repository

import com.example.data.local.DuaDao
import com.example.data.local.DuaEntity
import com.example.data.local.DuaDatabaseSeeder
import com.example.data.local.DuaTranslationEntity
import com.example.data.local.DuaTranslationLocalization
import com.example.data.local.DuaReferenceLocalization
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DuaRepository(private val duaDao: DuaDao, private val externalScope: CoroutineScope) {

    val allDuas: Flow<List<DuaEntity>> = duaDao.getAllDuas()
    val favoriteDuas: Flow<List<DuaEntity>> = duaDao.getFavoriteDuas()
    val categories: Flow<List<String>> = duaDao.getCategories()

    init {
        // Seed database in background to ensure all seed duas are present
        externalScope.launch(Dispatchers.IO) {
            try {
                duaDao.insertDuas(DuaDatabaseSeeder.getSeedDuas())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getTranslationAndReference(dua: DuaEntity, language: String): Pair<String, String> = withContext(Dispatchers.IO) {
        if (language == "English") {
            return@withContext Pair(dua.translation, dua.reference)
        }

        // Get immediate localized translation and reference from our built-in provider
        val builtInTranslation = DuaTranslationLocalization.getLocalizedTranslation(
            dua.id, language, dua.translation,
            dua.translationHausa, dua.translationYoruba, dua.translationIgbo
        )
        val builtInReference = DuaReferenceLocalization.getLocalizedReference(dua.id, language) ?: dua.reference

        // If we already have a specialized translation (not defaulting to English)
        if (builtInTranslation.isNotEmpty() && builtInTranslation != dua.translation) {
            return@withContext Pair(builtInTranslation, builtInReference)
        }

        // Try database cache (only if valid and not a stale English fallback)
        val cached = duaDao.getTranslation(dua.id, language)
        if (cached != null && cached.translation.isNotEmpty() && cached.translation != dua.translation) {
            return@withContext Pair(cached.translation, cached.reference)
        }

        // Fetch from Gemini API if available
        if (GeminiTranslationService.hasApiKey()) {
            val result = GeminiTranslationService.translateDua(dua, language)
            if (result != null) {
                val entity = DuaTranslationEntity(dua.id, language, result.first, result.second)
                duaDao.insertTranslation(entity)
                return@withContext result
            }
        }

        Pair(builtInTranslation, builtInReference)
    }

    fun getDuasByCategory(category: String): Flow<List<DuaEntity>> {
        return duaDao.getDuasByCategory(category)
    }

    fun searchDuas(query: String): Flow<List<DuaEntity>> {
        return duaDao.searchDuas(query)
    }

    suspend fun toggleFavorite(id: Int, isFavorite: Boolean) {
        duaDao.updateFavorite(id, isFavorite)
    }
}

