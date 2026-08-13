package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.example.ui.screens.MandatoryAdhkarActivity

class MandatoryAdhkarReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("ADHKAR_TYPE") ?: "MORNING"
        val isTest = intent.getBooleanExtra("IS_TEST", false)
        val prefs = MandatoryAdhkarManager.getPrefs(context)

        val isMorning = type == "MORNING"
        val enabled = if (isMorning) {
            prefs.getBoolean(MandatoryAdhkarManager.KEY_MORNING_ENABLED, false)
        } else {
            prefs.getBoolean(MandatoryAdhkarManager.KEY_EVENING_ENABLED, false)
        }

        if (!enabled && !isTest) return

        val completed = if (isMorning) {
            MandatoryAdhkarManager.isMorningCompletedToday(context)
        } else {
            MandatoryAdhkarManager.isEveningCompletedToday(context)
        }

        if (!completed || isTest) {
            // Wake device screen if sleeping
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            val wakeLock = powerManager?.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
                "NoorZikir:MandatoryAdhkarWakeLock"
            )
            try {
                wakeLock?.acquire(10000L) // Hold wake lock for 10 seconds
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Show persistent high-priority notification (with fullScreenIntent for lockscreen & heads-up)
            MandatoryAdhkarManager.showPersistentNotification(context, type)

            // Full-Screen Reminder Activity wake screen & open over any running app
            val fullscreenEnabled = prefs.getBoolean(MandatoryAdhkarManager.KEY_FULLSCREEN_ENABLED, true)
            if (fullscreenEnabled || isTest) {
                val fullScreenIntent = Intent(context, MandatoryAdhkarActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                            Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    putExtra("ADHKAR_TYPE", type)
                    putExtra(
                        "READING_DURATION",
                        prefs.getInt(MandatoryAdhkarManager.KEY_READING_DURATION, 3)
                    )
                }
                try {
                    context.startActivity(fullScreenIntent)
                } catch (e: Exception) {
                    try {
                        val pi = android.app.PendingIntent.getActivity(
                            context,
                            if (isMorning) 401 else 402,
                            fullScreenIntent,
                            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                        )
                        pi.send()
                    } catch (e2: Exception) {
                        e2.printStackTrace()
                    }
                }
            }
        }

        // Reschedule alarm for next day if not a test
        if (!isTest) {
            MandatoryAdhkarManager.scheduleAlarms(context)
        }
    }
}
