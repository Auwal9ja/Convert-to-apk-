package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: "UNKNOWN_ACTION"
        Log.d(MandatoryAdhkarManager.TAG, "reboot recovery: Broadcast received: $action. Restoring saved Mandatory schedules & reminders.")

        try {
            // Restore notification channel
            MandatoryAdhkarManager.createNotificationChannel(context)

            // Reschedule and recover all active schedules and detect missed occurrences
            MandatoryAdhkarManager.recoverAndRescheduleAll(context, reason = action)
            ReminderReceiver.rescheduleAllIfEnabled(context)
            com.example.util.PrayerTimeManager.reschedulePrayerAlarms(context)
            com.example.receiver.PrayerWidgetProvider.updateAllWidgets(context)

            Log.d(MandatoryAdhkarManager.TAG, "reboot recovery: Successfully restored all schedules after $action")
        } catch (e: Exception) {
            Log.e(MandatoryAdhkarManager.TAG, "errors: Error restoring schedules on boot/system event: ${e.message}", e)
        }
    }
}
