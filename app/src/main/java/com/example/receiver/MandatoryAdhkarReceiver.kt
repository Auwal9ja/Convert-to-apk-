package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.example.ui.screens.MandatoryAdhkarActivity

class MandatoryAdhkarReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("ADHKAR_TYPE") ?: "MORNING"
        val prefs = MandatoryAdhkarManager.getPrefs(context)

        val isMorning = type == "MORNING"
        val enabled = if (isMorning) {
            prefs.getBoolean(MandatoryAdhkarManager.KEY_MORNING_ENABLED, false)
        } else {
            prefs.getBoolean(MandatoryAdhkarManager.KEY_EVENING_ENABLED, false)
        }

        if (!enabled) return

        val completed = if (isMorning) {
            MandatoryAdhkarManager.isMorningCompletedToday(context)
        } else {
            MandatoryAdhkarManager.isEveningCompletedToday(context)
        }

        if (!completed) {
            // Wake device screen if sleeping
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            val wakeLock = powerManager?.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
                "HisnulMuslim:MandatoryAdhkarWakeLock"
            )
            wakeLock?.acquire(5000L) // Hold wake lock for 5 seconds

            // Requirement 7: Show persistent notification (with fullScreenIntent)
            MandatoryAdhkarManager.showPersistentNotification(context, type)

            // Requirement 4: Full-Screen Reminder Activity wake screen if enabled
            val fullscreenEnabled = prefs.getBoolean(MandatoryAdhkarManager.KEY_FULLSCREEN_ENABLED, true)
            if (fullscreenEnabled) {
                val fullScreenIntent = Intent(context, MandatoryAdhkarActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra("ADHKAR_TYPE", type)
                    putExtra(
                        "READING_DURATION",
                        prefs.getInt(MandatoryAdhkarManager.KEY_READING_DURATION, 3)
                    )
                }
                try {
                    context.startActivity(fullScreenIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // Reschedule alarm for next day
        MandatoryAdhkarManager.scheduleAlarms(context)
    }
}
