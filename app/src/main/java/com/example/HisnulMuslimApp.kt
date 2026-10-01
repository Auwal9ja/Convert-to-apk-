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

        // Pre-create WebView and Chromium cache directories to prevent simple_file_enumerator ENOENT errors
        try {
            val codeCacheJs = File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/js")
            val codeCacheWasm = File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/wasm")
            if (!codeCacheJs.exists()) {
                codeCacheJs.mkdirs()
            }
            if (!codeCacheWasm.exists()) {
                codeCacheWasm.mkdirs()
            }
        } catch (e: Exception) {
            Log.w("HisnulMuslimApp", "Could not pre-create WebView cache directories: ${e.message}")
        }

        // Initialize OneSignal Push Notification SDK
        OneSignalHelper.initialize(this)
    }
}
