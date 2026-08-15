package com.example.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import com.example.ui.screens.MandatoryAdhkarActivity

class MandatoryAdhkarReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "NOOR_ZIKIR_MANDATORY"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: "UNKNOWN_ACTION"
        val scheduleId = intent.getStringExtra("SCHEDULE_ID")
            ?: if (intent.getStringExtra("ADHKAR_TYPE") == "EVENING") MandatoryAdhkarManager.SCHEDULE_ID_EVENING else MandatoryAdhkarManager.SCHEDULE_ID_MORNING
        val scheduleTitle = intent.getStringExtra("SCHEDULE_TITLE")
            ?: if (scheduleId == MandatoryAdhkarManager.SCHEDULE_ID_EVENING) "Evening Zikir" else "Morning Zikir"
        val category = intent.getStringExtra("CATEGORY") ?: "Morning & Evening"
        val durationMinutes = intent.getIntExtra("DURATION_MINUTES", intent.getIntExtra("READING_DURATION", 15))
        val isTest = intent.getBooleanExtra("IS_TEST", false)

        Log.d(TAG, "receiver execution: Action received: $action for schedule=$scheduleId ($scheduleTitle), duration=${durationMinutes}m, isTest=$isTest")

        // Check if master switch or schedule is enabled
        val allSchedules = MandatoryAdhkarManager.getAllSchedules(context)
        val schedule = allSchedules.find { it.id == scheduleId }
        val isEnabled = schedule?.enabled ?: true

        if (!isEnabled && !isTest) {
            Log.d(TAG, "receiver execution: Schedule $scheduleId is disabled. Aborting trigger.")
            return
        }

        // Check duplicate protection & today's completion status
        val isCompletedToday = MandatoryAdhkarManager.isScheduleCompletedToday(context, scheduleId)
        val currentState = MandatoryAdhkarManager.getSessionState(context)

        if (currentState == MandatorySessionState.ACTIVE && !isTest) {
            Log.w(TAG, "duplicate detection: A Mandatory Zikir session is already currently ACTIVE. Gracefully handling overlap.")
            return
        }

        if (isCompletedToday && !isTest) {
            Log.d(TAG, "duplicate detection: Schedule $scheduleId already completed for today. Skipping trigger.")
            MandatoryAdhkarManager.scheduleAlarms(context)
            return
        }

        // Update state machine
        MandatoryAdhkarManager.setSessionState(context, MandatorySessionState.TRIGGERED)

        // Acquire WakeLock safely to turn on screen
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        @Suppress("DEPRECATION")
        val wakeLock = powerManager?.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
            "NoorZikir:MandatoryReceiverWakeLock"
        )

        try {
            wakeLock?.acquire(15000L) // 15 seconds wake lock
            Log.d(TAG, "alarm trigger: WakeLock acquired for screen wakeup")
        } catch (e: Exception) {
            Log.w(TAG, "alarm trigger: Failed to acquire WakeLock: ${e.message}")
        }

        // Transition to STARTING
        MandatoryAdhkarManager.setSessionState(context, MandatorySessionState.STARTING)

        // Show persistent high-priority notification with FullScreenIntent
        MandatoryAdhkarManager.showPersistentNotification(
            context = context,
            scheduleId = scheduleId,
            scheduleTitle = scheduleTitle,
            category = category,
            durationMinutes = durationMinutes
        )

        // Attempt direct launch if allowed by Android background activity launch policy
        val fullScreenIntent = Intent(context, MandatoryAdhkarActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            putExtra("SCHEDULE_ID", scheduleId)
            putExtra("SCHEDULE_TITLE", scheduleTitle)
            putExtra("CATEGORY", category)
            putExtra("DURATION_MINUTES", durationMinutes)
        }

        try {
            Log.d(TAG, "full-screen intent decision: Attempting direct activity launch")
            context.startActivity(fullScreenIntent)
            Log.d(TAG, "full-screen intent decision: Direct activity launch initiated successfully")
        } catch (e: Exception) {
            Log.d(TAG, "full-screen intent decision: Direct launch restricted (${e.message}). Relying on High Priority FullScreenIntent notification.")
            try {
                val pi = PendingIntent.getActivity(
                    context,
                    999,
                    fullScreenIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                pi.send()
            } catch (e2: Exception) {
                Log.w(TAG, "full-screen intent decision: PendingIntent send fallback error: ${e2.message}")
            }
        }

        // Reschedule alarm for next occurrence if not a manual test
        if (!isTest) {
            MandatoryAdhkarManager.scheduleAlarms(context)
        }
    }
}
