package com.example

import android.app.Application
import android.util.Log
import com.example.receiver.OneSignalHelper
import java.io.File

class HisnulMuslimApp : Application() {

    companion object {
        lateinit var instance: HisnulMuslimApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.d("HisnulMuslimApp", "Application starting, initializing services...")

        // Ensure clean cache state without un-indexed empty directories
        try {
            val webViewCache = File(cacheDir, "WebView")
            if (webViewCache.exists()) {
                val httpCache = File(webViewCache, "Default/HTTP Cache")
                val realIndex = File(httpCache, "index-dir/the-real-index")
                val codeCache = File(httpCache, "Code Cache")
                // If Code Cache exists without a valid real-index, delete it to prevent index reconstruction failures
                if (codeCache.exists() && !realIndex.exists()) {
                    codeCache.deleteRecursively()
                }
            }
        } catch (_: Exception) {}

        // Initialize OneSignal Push Notification SDK
        OneSignalHelper.initialize(this)
    }
}
