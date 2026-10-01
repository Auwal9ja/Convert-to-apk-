package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.data.local.DuaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class LocalizedDuaDetails(
    val title: String,
    val translation: String,
    val reference: String
)

object GeminiTranslationService {
    private const val TAG = "GeminiTranslation"
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isEmpty() || key == "MY_GEMINI_API_KEY") "" else key
        } catch (e: Exception) {
            ""
        }
    }

    fun hasApiKey(): Boolean {
        return getApiKey().isNotEmpty()
    }

    /**
     * Translates Dua content into targetLanguage.
     * Uses Gemini AI when API key is present; falls back to free translation engine.
     */
    suspend fun translateDuaFull(
        title: String,
        translation: String,
        reference: String,
        targetLanguage: String
    ): LocalizedDuaDetails = withContext(Dispatchers.IO) {
        // 1. Try Gemini API first if configured
        if (hasApiKey()) {
            val geminiResult = translateWithGemini(title, translation, reference, targetLanguage)
            if (geminiResult != null) return@withContext geminiResult
        }

        // 2. High-speed free translation fallback
        val transTitle = translateTextFree(title, targetLanguage)
        val transTranslation = translateTextFree(translation, targetLanguage)
        val transRef = translateTextFree(reference, targetLanguage)

        LocalizedDuaDetails(
            title = transTitle.ifBlank { title },
            translation = transTranslation.ifBlank { translation },
            reference = transRef.ifBlank { reference }
        )
    }

    /**
     * Backward-compatible helper for existing callers.
     */
    suspend fun translateDua(dua: DuaEntity, targetLanguage: String): Pair<String, String>? {
        val result = translateDuaFull(
            title = dua.title,
            translation = dua.translation.ifBlank { dua.translationHausa },
            reference = dua.reference,
            targetLanguage = targetLanguage
        )
        return Pair(result.translation, result.reference)
    }

    private suspend fun translateWithGemini(
        title: String,
        translation: String,
        reference: String,
        targetLanguage: String
    ): LocalizedDuaDetails? = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) return@withContext null

        val prompt = """
            You are an expert Islamic supplication translator. Translate the following supplication details into $targetLanguage.
            
            Original Title: "$title"
            Original Translation/Meaning: "$translation"
            Original Reference & Virtue: "$reference"
            
            Provide your response as a valid JSON object with exactly these keys:
            {
              "title": "The translated title in $targetLanguage",
              "translation": "The translated supplication meaning in $targetLanguage",
              "reference": "The translated reference and virtue in $targetLanguage"
            }
            
            Only return the raw JSON object, without markdown formatting or code blocks.
        """.trimIndent()

        // Follow Gemini API skill rules: use gemini-3.5-flash or gemini-2.5-flash
        val modelsToTry = listOf("gemini-3.5-flash", "gemini-2.5-flash")

        for (modelName in modelsToTry) {
            try {
                Log.d(TAG, "Attempting translation with Gemini model: $modelName")
                val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"

                val requestJson = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", prompt)
                                })
                            })
                        })
                    })
                    put("generationConfig", JSONObject().apply {
                        put("responseMimeType", "application/json")
                    })
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = requestJson.toString().toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                val responseDetails = client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e(TAG, "Gemini $modelName returned HTTP ${response.code}")
                        return@use null
                    }
                    val responseBody = response.body?.string() ?: return@use null
                    val root = JSONObject(responseBody)
                    val candidate = root.optJSONArray("candidates")?.optJSONObject(0) ?: return@use null
                    val part = candidate.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0) ?: return@use null
                    val rawText = part.optString("text", "")

                    val start = rawText.indexOf('{')
                    val end = rawText.lastIndexOf('}')
                    val jsonString = if (start != -1 && end != -1 && end > start) {
                        rawText.substring(start, end + 1)
                    } else {
                        rawText.trim()
                    }

                    val obj = JSONObject(jsonString)
                    val tTitle = obj.optString("title", "").trim()
                    val tTranslation = obj.optString("translation", "").trim()
                    val tReference = obj.optString("reference", "").trim()

                    if (tTranslation.isNotBlank()) {
                        LocalizedDuaDetails(
                            title = tTitle.ifBlank { title },
                            translation = tTranslation,
                            reference = tReference.ifBlank { reference }
                        )
                    } else {
                        null
                    }
                }

                if (responseDetails != null) return@withContext responseDetails
            } catch (e: Exception) {
                Log.w(TAG, "Error with Gemini model $modelName: ${e.message}")
            }
        }
        null
    }

    /**
     * Translates single text string via free translation API (MyMemory).
     */
    suspend fun translateTextFree(text: String, targetLanguage: String): String = withContext(Dispatchers.IO) {
        val trimmed = text.trim()
        if (trimmed.isBlank()) return@withContext ""

        val targetCode = when (targetLanguage.lowercase()) {
            "hausa" -> "ha"
            "english" -> "en"
            "yoruba" -> "yo"
            "igbo" -> "ig"
            "arabic" -> "ar"
            "french" -> "fr"
            "spanish" -> "es"
            "urdu" -> "ur"
            "chinese" -> "zh-CN"
            else -> "en"
        }

        val isHausa = trimmed.contains("wanda", ignoreCase = true) ||
                      (trimmed.contains("Allah", ignoreCase = true) && trimmed.contains("mai", ignoreCase = true)) ||
                      trimmed.contains("addu", ignoreCase = true) ||
                      trimmed.contains("kuma", ignoreCase = true) ||
                      trimmed.contains("babu", ignoreCase = true) ||
                      trimmed.contains("farkar", ignoreCase = true) ||
                      trimmed.contains("dare", ignoreCase = true) ||
                      trimmed.contains("cikin", ignoreCase = true)

        val sourceCode = if (isHausa) "ha" else "en"
        if (sourceCode == targetCode) {
            return@withContext trimmed
        }

        try {
            // Translate up to 500 characters
            val toTranslate = if (trimmed.length > 500) trimmed.take(500) else trimmed
            val encoded = java.net.URLEncoder.encode(toTranslate, "UTF-8")
            val url = "https://api.mymemory.translated.net/get?q=$encoded&langpair=$sourceCode|$targetCode"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "ZakiruMuslim-Android/1.0")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@use trimmed
                val body = response.body?.string() ?: return@use trimmed
                val json = JSONObject(body)
                val responseData = json.optJSONObject("responseData") ?: return@use trimmed
                val translated = responseData.optString("translatedText", "").trim()
                if (translated.isNotBlank() && !translated.contains("MYMEMORY WARNING")) {
                    translated
                } else {
                    trimmed
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "translateTextFree failed: ${e.message}")
            trimmed
        }
    }
}
