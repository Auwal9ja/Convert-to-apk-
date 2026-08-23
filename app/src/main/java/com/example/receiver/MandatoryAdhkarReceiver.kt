package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MandatoryAdhkarReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: "UNKNOWN_ACTION"
        val scheduleId = intent.getStringExtra("SCHEDULE_ID")
            ?: if (intent.getStringExtra("ADHKAR_TYPE") == "EVENING") MandatoryAdhkarManager.SCHEDULE_ID_EVENING else MandatoryAdhkarManager.SCHEDULE_ID_MORNING
        val scheduleTitle = intent.getStringExtra("SCHEDULE_TITLE")
            ?: if (scheduleId == MandatoryAdhkarManager.SCHEDULE_ID_EVENING) "Evening Zikir" else "Morning Zikir"
        val category = intent.getStringExtra("CATEGORY") ?: "Morning & Evening"
        val durationMinutes = intent.getIntExtra("DURATION_MINUTES", intent.getIntExtra("READING_DURATION", 3))
        val isTest = intent.getBooleanExtra("IS_TEST", false)

        Log.d(MandatoryAdhkarManager.TAG, "receiver execution: Action received: $action for schedule=$scheduleId ($scheduleTitle), duration=${durationMinutes}m, isTest=$isTest")

        try {
            MandatoryAdhkarManager.onAlarmTriggered(
                context = context,
                scheduleId = scheduleId,
                scheduleTitle = scheduleTitle,
                category = category,
                durationMinutes = durationMinutes,
                isTest = isTest
            )
        } catch (e: Exception) {
            Log.e(MandatoryAdhkarManager.TAG, "errors: Exception during onAlarmTriggered for $scheduleId: ${e.message}", e)
        }
    }
}
