package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.data.local.DuaEntity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiTranslationService {
    private const val TAG = "GeminiTranslation"
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
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

    suspend fun translateDua(dua: DuaEntity, targetLanguage: String): Pair<String, String>? {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            Log.w(TAG, "Gemini API key is empty or placeholder, falling back to original.")
            return null
        }

        val prompt = """
            You are an expert Islamic translator. Translate the following supplication details into $targetLanguage.
            
            Original Title: "${dua.title}"
            Original Translation (English): "${dua.translation}"
            Original Reference (English): "${dua.reference}"
            
            Provide your response as a valid JSON object with exactly these keys:
            - "translation": The translation of the main supplication text in $targetLanguage.
            - "reference": The translated reference and virtue details in $targetLanguage.
            
            Only return the raw JSON object, no markdown, no ```json formatting blocks. Ensure the JSON is completely valid.
        """.trimIndent()

        val modelsToTry = listOf("gemini-2.5-flash", "gemini-1.5-flash", "gemini-3.5-flash")

        for (modelName in modelsToTry) {
            try {
                Log.d(TAG, "Attempting translation with model: $modelName")
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

                val result = client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        val errBody = response.body?.string() ?: ""
                        Log.e(TAG, "Model $modelName failed with code ${response.code}: $errBody")
                        return@use null
                    }

                    val responseBody = response.body?.string() ?: return@use null
                    val responseJson = JSONObject(responseBody)
                    val candidates = responseJson.optJSONArray("candidates") ?: return@use null
                    if (candidates.length() == 0) return@use null
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content") ?: return@use null
                    val parts = content.optJSONArray("parts") ?: return@use null
                    if (parts.length() == 0) return@use null
                    val firstPart = parts.getJSONObject(0)
                    val text = firstPart.optString("text") ?: return@use null

                    // Extract only the JSON portion from the response
                    val start = text.indexOf('{')
                    val end = text.lastIndexOf('}')
                    val jsonString = if (start != -1 && end != -1 && end > start) {
                        text.substring(start, end + 1)
                    } else {
                        text.trim()
                    }

                    val translatedObj = JSONObject(jsonString)
                    val translation = translatedObj.optString("translation", "").trim()
                    val reference = translatedObj.optString("reference", "").trim()

                    if (translation.isNotEmpty()) {
                        Log.d(TAG, "Successfully translated using model: $modelName")
                        Pair(translation, reference)
                    } else {
                        null
                    }
                }

                if (result != null) {
                    return result
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error with model $modelName: ${e.message}", e)
            }
        }

        Log.e(TAG, "All translation models failed.")
        return null
    }
}
