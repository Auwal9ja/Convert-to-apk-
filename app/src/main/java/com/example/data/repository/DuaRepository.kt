package com.example.data.repository

import com.example.data.local.DuaDao
import com.example.data.local.DuaEntity
import com.example.data.local.DuaDatabaseSeeder
import com.example.data.local.DuaTranslationEntity
import com.example.data.local.DuaTranslationLocalization
import com.example.data.local.DuaReferenceLocalization
import com.example.data.local.AppLocalizer
import android.util.Log
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

    suspend fun getLocalizedDuaDetails(dua: DuaEntity, language: String): LocalizedDuaDetails = withContext(Dispatchers.IO) {
        // 1. Title Resolution from built-in static table
        val staticTitle = AppLocalizer.getDuaTitle(dua.id, dua.title, language)
        val hasStaticTitle = (dua.id in 1..359) && (staticTitle != dua.title || language == "English")

        // 2. Translation Resolution from built-in static table
        val builtInTranslation = DuaTranslationLocalization.getLocalizedTranslation(
            dua.id, language, dua.translation,
            dua.translationHausa, dua.translationYoruba, dua.translationIgbo
        )
        val hasSpecializedTranslation = builtInTranslation.isNotEmpty() && builtInTranslation != dua.translation

        // 3. Reference Resolution from built-in static table
        val builtInReference = if (language == "English") {
            dua.reference
        } else {
            DuaReferenceLocalization.getLocalizedReference(dua.id, language)
        }

        // If built-in localization covers all 3 items completely (standard built-in Duas):
        if (hasStaticTitle && hasSpecializedTranslation && builtInReference != null) {
            return@withContext LocalizedDuaDetails(staticTitle, builtInTranslation, builtInReference)
        }

        // 4. Try local Room DB cache
        val cached = duaDao.getTranslation(dua.id, language)
        if (cached != null && cached.translation.isNotBlank() && cached.translation != dua.translation) {
            val title = if (cached.title.isNotBlank()) cached.title else staticTitle
            val reference = if (cached.reference.isNotBlank()) cached.reference else (builtInReference ?: dua.reference)
            return@withContext LocalizedDuaDetails(title, cached.translation, reference)
        }

        // 5. Dynamic translation via Gemini AI or free translation service
        try {
            val baseTitle = if (hasStaticTitle) staticTitle else dua.title
            val baseTranslation = if (hasSpecializedTranslation) {
                builtInTranslation
            } else {
                dua.translationHausa.ifBlank { dua.translation }
            }
            val baseReference = builtInReference ?: dua.reference

            val dynamicResult = GeminiTranslationService.translateDuaFull(
                title = baseTitle,
                translation = baseTranslation,
                reference = baseReference,
                targetLanguage = language
            )

            // Cache in Room DB for instant offline loading next time
            val entity = DuaTranslationEntity(
                duaId = dua.id,
                language = language,
                translation = dynamicResult.translation,
                reference = dynamicResult.reference,
                title = dynamicResult.title
            )
            duaDao.insertTranslation(entity)

            return@withContext dynamicResult
        } catch (e: Exception) {
            Log.e("DuaRepository", "Failed dynamic translation: ${e.message}")
        }

        // Fallback to best available
        val fallbackTranslation = when (language) {
            "Hausa" -> dua.translationHausa.ifBlank { dua.translation }
            "Yoruba" -> dua.translationYoruba.ifBlank { dua.translation }
            "Igbo" -> dua.translationIgbo.ifBlank { dua.translation }
            else -> dua.translation
        }
        val fallbackRef = builtInReference ?: dua.reference
        LocalizedDuaDetails(staticTitle, fallbackTranslation, fallbackRef)
    }

    suspend fun getTranslationAndReference(dua: DuaEntity, language: String): Pair<String, String> = withContext(Dispatchers.IO) {
        val details = getLocalizedDuaDetails(dua, language)
        Pair(details.translation, details.reference)
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

