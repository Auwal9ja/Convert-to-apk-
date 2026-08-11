package com.example.data.repository

import com.example.data.local.DuaDao
import com.example.data.local.DuaEntity
import com.example.data.local.DuaDatabaseSeeder
import com.example.data.local.DuaTranslationEntity
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

        // Try cache
        val cached = duaDao.getTranslation(dua.id, language)
        if (cached != null) {
            // If the cached reference is untranslated (still matches English) or empty,
            // and we now have a valid API key, try upgrading to a full Gemini translation.
            if ((cached.reference.isEmpty() || cached.reference == dua.reference) && GeminiTranslationService.hasApiKey()) {
                val result = GeminiTranslationService.translateDua(dua, language)
                if (result != null) {
                    val entity = DuaTranslationEntity(dua.id, language, result.first, result.second)
                    duaDao.insertTranslation(entity)
                    return@withContext result
                }
            }
            return@withContext Pair(cached.translation, cached.reference)
        }

        // Fetch from Gemini API
        val result = GeminiTranslationService.translateDua(dua, language)
        if (result != null) {
            val entity = DuaTranslationEntity(dua.id, language, result.first, result.second)
            duaDao.insertTranslation(entity)
            return@withContext result
        }

        // Fallback
        val fallbackTranslation = when (language) {
            "Hausa" -> if (dua.translationHausa.isNotEmpty()) dua.translationHausa else dua.translation
            "Yoruba" -> if (dua.translationYoruba.isNotEmpty()) dua.translationYoruba else dua.translation
            "Igbo" -> if (dua.translationIgbo.isNotEmpty()) dua.translationIgbo else dua.translation
            else -> dua.translation
        }
        val fallbackReference = com.example.data.local.DuaReferenceLocalization.getLocalizedReference(dua.id, language) ?: dua.reference
        
        // Cache the offline fallback in the database so that it's read from the multilingual database next time
        try {
            val entity = DuaTranslationEntity(dua.id, language, fallbackTranslation, fallbackReference)
            duaDao.insertTranslation(entity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        Pair(fallbackTranslation, fallbackReference)
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
