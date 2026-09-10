package com.example.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

/**
 * Universal helper for soft, pleasant tactile vibration feedback across all Android versions.
 */
object VibrationHelper {

    private const val TAG = "VibrationHelper"

    /**
     * Triggers two gentle, soft vibrations with a short pause between them.
     * Pattern: [delay: 0ms, vibrate: 150ms, pause: 180ms, vibrate: 150ms]
     * Uses reduced amplitude on Android 8+ for a soft, premium feel.
     */
    fun triggerTwoSoftVibrations(context: Context) {
        try {
            val timings = longArrayOf(0, 150, 180, 150)
            val amplitudes = intArrayOf(0, 110, 0, 110) // Soft amplitude (out of 255)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                if (vibrator != null && vibrator.hasVibrator()) {
                    if (vibrator.hasAmplitudeControl()) {
                        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                    } else {
                        vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (vibrator != null && vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (vibrator.hasAmplitudeControl()) {
                            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                        } else {
                            vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(timings, -1)
                    }
                }
            }
            Log.d(TAG, "Two soft vibrations triggered successfully.")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to trigger two soft vibrations: ${e.message}")
        }
    }
}
