package com.example

import android.app.Application
import android.util.Log
import com.example.receiver.OneSignalHelper

class HisnulMuslimApp : Application() {

    companion object {
        lateinit var instance: HisnulMuslimApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.d("HisnulMuslimApp", "Application starting, initializing services...")

        // Initialize OneSignal Push Notification SDK
        OneSignalHelper.initialize(this)
    }
}
