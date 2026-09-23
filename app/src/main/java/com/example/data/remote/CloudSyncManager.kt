package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.data.local.DuaDatabase
import com.example.data.local.DuaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * CloudSyncManager connects your Android app directly to Firebase Firestore REST API
 * (or any custom JSON endpoint) to sync all Du'a and Azkar in real-time.
 */
object CloudSyncManager {
    private const val TAG = "CloudSyncManager"
    private const val PREFS_NAME = "cloud_sync_prefs"
    private const val KEY_ENDPOINT = "custom_sync_endpoint"
    private const val KEY_LAST_SYNC_TIME = "last_sync_timestamp"
    private const val KEY_AUTO_SYNC_ENABLED = "auto_sync_enabled"

    // Direct Firebase Firestore REST Endpoint for your live project zakiru-45523
    const val DEFAULT_FIRESTORE_PROJECT = "zakiru-45523"
    const val DEFAULT_CLOUD_URL = "https://firestore.googleapis.com/v1/projects/zakiru-45523/databases/(default)/documents/duas"

    fun getEndpoint(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ENDPOINT, DEFAULT_CLOUD_URL) ?: DEFAULT_CLOUD_URL
    }

    fun setEndpoint(context: Context, url: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_ENDPOINT, url.trim()).apply()
    }

    fun getLastSyncTime(context: Context): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(KEY_LAST_SYNC_TIME, 0L)
    }

    fun isAutoSyncEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_AUTO_SYNC_ENABLED, true)
    }

    fun setAutoSyncEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_AUTO_SYNC_ENABLED, enabled).apply()
    }

    /**
     * Performs a background sync with the Firebase Firestore / Web Admin server.
     * Fetches all remote Du'as and updates local Room Database.
     * Supports full pagination across all pages using nextPageToken.
     */
    suspend fun syncWithRemote(context: Context): SyncResult = withContext(Dispatchers.IO) {
        val endpoint = getEndpoint(context)
        if (endpoint.isBlank()) {
            return@withContext SyncResult(false, "Saitin URL bai dace ba / Invalid Cloud Endpoint URL")
        }

        try {
            val isFirestoreEndpoint = endpoint.contains("firestore.googleapis.com")
            // Ensure pageSize=300 for Firestore endpoints to retrieve maximum documents per page
            val baseEndpoint = if (isFirestoreEndpoint) {
                if (endpoint.contains("pageSize=")) {
                    endpoint
                } else if (endpoint.contains("?")) {
                    "$endpoint&pageSize=300"
                } else {
                    "$endpoint?pageSize=300"
                }
            } else {
                endpoint
            }

            val newEntities = mutableListOf<DuaEntity>()
            var nextPageToken: String? = null
            var pagesFetched = 0

            do {
                val pageUrlString = if (!nextPageToken.isNullOrBlank()) {
                    val separator = if (baseEndpoint.contains("?")) "&" else "?"
                    "$baseEndpoint${separator}pageToken=${java.net.URLEncoder.encode(nextPageToken, "UTF-8")}"
                } else {
                    baseEndpoint
                }

                val url = URL(pageUrlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 20000
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty("User-Agent", "ZakiruMuslim-Android/1.0")

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
                    break
                }

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    // If firestore collection is empty or not found yet
                    if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                        return@withContext SyncResult(true, "Firebase Firestore collection yana shirye / Ready for content", 0)
                    }
                    return@withContext SyncResult(false, "Firebase bai amsa ba (HTTP $responseCode)")
                }

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val jsonText = reader.use { it.readText() }
                pagesFetched++

                // 1. Check if response is Firestore REST format (contains "documents")
                val rootObj = JSONObject(jsonText)
                if (rootObj.has("documents")) {
                    val docsArray = rootObj.optJSONArray("documents") ?: JSONArray()
                    for (i in 0 until docsArray.length()) {
                        val docObj = docsArray.optJSONObject(i) ?: continue
                        val fields = docObj.optJSONObject("fields") ?: continue

                        fun getString(field: String): String {
                            val f = fields.optJSONObject(field) ?: return ""
                            return f.optString("stringValue", "")
                        }
                        fun getInt(field: String, fallback: Int = 0): Int {
                            val f = fields.optJSONObject(field) ?: return fallback
                            val numStr = f.optString("integerValue", "")
                            if (numStr.isNotBlank()) return numStr.toIntOrNull() ?: fallback
                            val doubleVal = f.optDouble("doubleValue", Double.NaN)
                            if (!doubleVal.isNaN()) return doubleVal.toInt()
                            val strVal = f.optString("stringValue", "")
                            if (strVal.isNotBlank()) return strVal.toIntOrNull() ?: fallback
                            return fallback
                        }

                        var rawId = getInt("id", 0)
                        if (rawId <= 0) {
                            // Extract ID from document name path e.g. .../documents/duas/1001
                            val docName = docObj.optString("name", "")
                            val lastSegment = docName.substringAfterLast("/")
                            rawId = lastSegment.toIntOrNull() ?: (newEntities.size + 1)
                        }

                        val category = getString("category").ifBlank { "General Adhkar" }
                        val title = getString("title")
                        val arabic = getString("arabic")
                        val translation = getString("translation")
                        val transliteration = getString("transliteration")
                        val reference = getString("reference")
                        val translationHausa = getString("translationHausa")
                        val translationYoruba = getString("translationYoruba")
                        val translationIgbo = getString("translationIgbo")

                        // Safety remapping: IDs 244..359 are reserved in Android for built-in Asmaul Husna & Answered Duas.
                        // If a custom admin dua was created in that ID range, remap it to 1000 + id so it is never
                        // overwritten by local seeders.
                        val finalId = if (rawId in 244..359 && category != "Asma'ul Husna" && category != "Addu'o'i na Ijaba") {
                            1000 + rawId
                        } else {
                            rawId
                        }

                        if (title.isNotBlank() && arabic.isNotBlank()) {
                            newEntities.add(
                                DuaEntity(
                                    id = finalId,
                                    category = category,
                                    title = title,
                                    arabic = arabic,
                                    translation = translation.ifBlank { translationHausa },
                                    transliteration = transliteration,
                                    reference = reference,
                                    translationHausa = translationHausa.ifBlank { translation },
                                    translationYoruba = translationYoruba,
                                    translationIgbo = translationIgbo,
                                    isFavorite = false
                                )
                            )
                        }
                    }

                    // Check for next page token
                    val token = rootObj.optString("nextPageToken", "").trim()
                    nextPageToken = if (token.isNotBlank()) token else null
                } else {
                    // 2. Standard JSON Array format (e.g. { "duas": [...] } or [ ... ])
                    val duasArray = rootObj.optJSONArray("duas") ?: rootObj.optJSONArray("data")
                    if (duasArray != null) {
                        for (i in 0 until duasArray.length()) {
                            val item = duasArray.getJSONObject(i)
                            val id = item.optInt("id", 0)
                            val category = item.optString("category", "General Adhkar")
                            val title = item.optString("title", "")
                            val arabic = item.optString("arabic", "")
                            val translation = item.optString("translation", "")
                            val transliteration = item.optString("transliteration", "")
                            val reference = item.optString("reference", "")
                            val translationHausa = item.optString("translationHausa", item.optString("hausa", ""))
                            val translationYoruba = item.optString("translationYoruba", item.optString("yoruba", ""))
                            val translationIgbo = item.optString("translationIgbo", item.optString("igbo", ""))

                            val finalId = if (id in 244..359 && category != "Asma'ul Husna" && category != "Addu'o'i na Ijaba") {
                                1000 + id
                            } else if (id > 0) {
                                id
                            } else {
                                newEntities.size + 1
                            }

                            if (title.isNotBlank() && arabic.isNotBlank()) {
                                newEntities.add(
                                    DuaEntity(
                                        id = finalId,
                                        category = category,
                                        title = title,
                                        arabic = arabic,
                                        translation = translation,
                                        transliteration = transliteration,
                                        reference = reference,
                                        translationHausa = translationHausa,
                                        translationYoruba = translationYoruba,
                                        translationIgbo = translationIgbo,
                                        isFavorite = false
                                    )
                                )
                            }
                        }
                    }
                    nextPageToken = null
                }
            } while (!nextPageToken.isNullOrBlank())

            if (newEntities.isNotEmpty()) {
                val db = DuaDatabase.getDatabase(context)
                val dao = db.duaDao()
                dao.insertDuas(newEntities)
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().putLong(KEY_LAST_SYNC_TIME, System.currentTimeMillis()).apply()
                Log.d(TAG, "Successfully synced ${newEntities.size} items from Firebase across $pagesFetched pages.")
                return@withContext SyncResult(true, "An sabunta addu'o'i ${newEntities.size} daga Firebase ✓", newEntities.size)
            }

            return@withContext SyncResult(true, "Firebase yana daidai / No changes found", 0)
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed: ${e.message}", e)
            return@withContext SyncResult(false, "Kuskure wajen haɗawa: ${e.localizedMessage ?: "Network error"}")
        }
    }
}

data class SyncResult(
    val isSuccess: Boolean,
    val message: String,
    val itemsCount: Int = 0
)
