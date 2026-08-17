package com.example.receiver

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.BuildConfig
import com.example.MainActivity
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object OneSignalHelper {
    private const val TAG = "OneSignalHelper"
    private const val PREFS_NAME = "onesignal_preferences"
    private const val KEY_CUSTOM_APP_ID = "custom_onesignal_app_id"
    private const val KEY_PUSH_ENABLED = "push_notifications_enabled"

    // Default Fallback OneSignal App ID placeholder
    const val DEFAULT_ONESIGNAL_APP_ID = "YOUR_ONESIGNAL_APP_ID"

    @Volatile
    private var isInitialized = false

    private fun isValidAppId(appId: String?): Boolean {
        if (appId.isNullOrBlank()) return false
        val trimmed = appId.trim()
        return trimmed != DEFAULT_ONESIGNAL_APP_ID &&
               trimmed != "MY_ONESIGNAL_APP_ID" &&
               trimmed.length >= 16
    }

    /**
     * Initializes the OneSignal SDK with the configured App ID and event listeners.
     */
    fun initialize(context: Context) {
        if (isInitialized) {
            Log.d(TAG, "OneSignal already initialized.")
            return
        }

        try {
            val appId = getEffectiveAppId(context)

            if (!isValidAppId(appId)) {
                Log.i(TAG, "OneSignal initialization postponed: No valid ONESIGNAL_APP_ID configured yet.")
                cleanStaleInvalidData(context)
                return
            }

            // Enable logs
            OneSignal.Debug.logLevel = LogLevel.WARN

            OneSignal.initWithContext(context.applicationContext, appId)
            isInitialized = true
            Log.i(TAG, "OneSignal successfully initialized with App ID: $appId")

            // Register Push Notification Click / Deep Link Listener
            OneSignal.Notifications.addClickListener(object : INotificationClickListener {
                override fun onClick(event: INotificationClickEvent) {
                    val additionalData = event.notification.additionalData
                    Log.d(TAG, "OneSignal notification clicked. Additional data: $additionalData")

                    val appContext = context.applicationContext
                    val intent = Intent(appContext, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        if (additionalData != null) {
                            if (additionalData.has("dua_id")) {
                                putExtra("target_dua_id", additionalData.optInt("dua_id"))
                            }
                            if (additionalData.has("category")) {
                                putExtra("target_category", additionalData.optString("category"))
                            }
                            if (additionalData.has("schedule_id")) {
                                putExtra("schedule_id", additionalData.optString("schedule_id"))
                            }
                        }
                    }
                    appContext.startActivity(intent)
                }
            })

            // Sync user language preference tag if stored
            val sharedPref = context.getSharedPreferences("hisnul_muslim_prefs", Context.MODE_PRIVATE)
            val currentLang = sharedPref.getString("selected_language", "English") ?: "English"
            setUserLanguageTag(currentLang)

        } catch (e: Exception) {
            Log.e(TAG, "Exception during OneSignal initialization: ${e.message}", e)
        }
    }

    /**
     * Prompts the user for push notification permission (Android 13+).
     */
    fun requestPushPermission(fallbackToSettings: Boolean = true, onResult: ((Boolean) -> Unit)? = null) {
        if (!isInitialized) {
            Log.d(TAG, "OneSignal not initialized yet. Skipping push permission request.")
            onResult?.invoke(false)
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val granted = OneSignal.Notifications.requestPermission(fallbackToSettings)
                Log.d(TAG, "OneSignal notification permission result: $granted")
                onResult?.invoke(granted)
            } catch (e: Exception) {
                Log.e(TAG, "Error requesting OneSignal notification permission", e)
                onResult?.invoke(false)
            }
        }
    }

    /**
     * Tag user with their selected app language for targeted push notifications.
     */
    fun setUserLanguageTag(language: String) {
        if (!isInitialized) return
        try {
            OneSignal.User.addTag("app_language", language)
            Log.d(TAG, "OneSignal user language tag set: $language")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting user language tag", e)
        }
    }

    /**
     * Tag user with category interest or reminder preferences.
     */
    fun setTopicTag(topicKey: String, isSubscribed: Boolean) {
        if (!isInitialized) return
        try {
            OneSignal.User.addTag(topicKey, if (isSubscribed) "true" else "false")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting topic tag $topicKey", e)
        }
    }

    /**
     * Checks whether the user has opted in to push notifications in OneSignal.
     */
    fun isOptedIn(): Boolean {
        if (!isInitialized) return false
        return try {
            OneSignal.User.pushSubscription.optedIn
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Opts in to push notifications.
     */
    fun optInPush(context: Context) {
        if (!isInitialized) return
        try {
            OneSignal.User.pushSubscription.optIn()
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_PUSH_ENABLED, true).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error opting in to OneSignal push", e)
        }
    }

    /**
     * Opts out of push notifications.
     */
    fun optOutPush(context: Context) {
        if (!isInitialized) return
        try {
            OneSignal.User.pushSubscription.optOut()
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_PUSH_ENABLED, false).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error opting out of OneSignal push", e)
        }
    }

    /**
     * Retrieves the active OneSignal App ID from BuildConfig or SharedPreferences.
     */
    fun getEffectiveAppId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val customAppId = prefs.getString(KEY_CUSTOM_APP_ID, null)
        if (!customAppId.isNullOrBlank()) {
            return customAppId.trim()
        }

        return try {
            val key = BuildConfig.ONESIGNAL_APP_ID
            if (!key.isNullOrBlank() && key != "MY_ONESIGNAL_APP_ID") key.trim() else DEFAULT_ONESIGNAL_APP_ID
        } catch (e: Exception) {
            DEFAULT_ONESIGNAL_APP_ID
        }
    }

    /**
     * Saves a user-defined custom OneSignal App ID and re-initializes.
     */
    fun saveCustomAppId(context: Context, newAppId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_CUSTOM_APP_ID, newAppId.trim()).apply()
        isInitialized = false
        initialize(context)
    }

    /**
     * Retrieves OneSignal Subscription ID (Player ID) for diagnostics / testing.
     */
    fun getSubscriptionId(): String? {
        if (!isInitialized) return null
        return try {
            OneSignal.User.pushSubscription.id
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Cleans up stale OneSignal SQLite databases and cached state left by placeholder runs.
     */
    private fun cleanStaleInvalidData(context: Context) {
        try {
            context.deleteDatabase("OneSignal.db")
            context.deleteDatabase("onesignal.db")
            context.deleteDatabase("OneSignal_v2.db")
            val appDir = context.filesDir?.parentFile
            if (appDir != null) {
                val dbDir = java.io.File(appDir, "databases")
                if (dbDir.exists()) {
                    dbDir.listFiles()?.filter { it.name.startsWith("OneSignal", ignoreCase = true) }?.forEach {
                        try { it.delete() } catch (_: Exception) {}
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Cleanup completed: ${e.message}")
        }
    }
}
