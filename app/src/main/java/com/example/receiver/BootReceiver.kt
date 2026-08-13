package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Reschedule all alarms on Boot, Time Change, or App Update
        MandatoryAdhkarManager.scheduleAlarms(context)
        ReminderReceiver.rescheduleAllIfEnabled(context)
    }
}
