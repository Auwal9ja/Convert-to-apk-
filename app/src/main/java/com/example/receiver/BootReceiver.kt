package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "NOOR_ZIKIR_MANDATORY"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: "UNKNOWN_ACTION"
        Log.d(TAG, "reboot restoration: Broadcast received: $action. Restoring saved Mandatory schedules & reminders.")

        try {
            // Restore notification channel
            MandatoryAdhkarManager.createNotificationChannel(context)

            // Reschedule all active schedules
            MandatoryAdhkarManager.scheduleAlarms(context)
            ReminderReceiver.rescheduleAllIfEnabled(context)

            Log.d(TAG, "reboot restoration: Successfully restored all schedules after $action")
        } catch (e: Exception) {
            Log.e(TAG, "reboot restoration: Error restoring schedules: ${e.message}", e)
        }
    }
}
