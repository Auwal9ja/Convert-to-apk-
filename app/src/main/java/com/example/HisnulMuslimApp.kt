package com.example

import android.app.Application
import android.util.Log
import com.example.receiver.OneSignalHelper

class HisnulMuslimApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("HisnulMuslimApp", "Application starting, initializing services...")

        // Initialize OneSignal Push Notification SDK
        OneSignalHelper.initialize(this)
    }
}
