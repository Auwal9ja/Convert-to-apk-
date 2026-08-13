package com.example.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "hisnul_muslim_reminders"
        const val CHANNEL_NAME = "Hisnul Muslim Daily Reminders"
        const val NOTIFICATION_ID_MORNING = 1001
        const val NOTIFICATION_ID_EVENING = 1002

        fun scheduleDailyReminder(context: Context, type: String, hour: Int, minute: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("REMINDER_TYPE", type)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                if (type == "MORNING") 101 else 102,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val now = System.currentTimeMillis()
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= now) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            try {
                val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent)
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            } catch (_: Throwable) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    } else {
                        alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        )
                    }
                } catch (_: Throwable) {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            }
        }

        fun cancelReminder(context: Context, type: String) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("REMINDER_TYPE", type)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                if (type == "MORNING") 101 else 102,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }

        fun rescheduleAllIfEnabled(context: Context) {
            val sharedPref = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            val morningEnabled = sharedPref.getBoolean("morning_enabled", false)
            val eveningEnabled = sharedPref.getBoolean("evening_enabled", false)

            if (morningEnabled) {
                scheduleDailyReminder(context, "MORNING", 7, 0)
            }
            if (eveningEnabled) {
                scheduleDailyReminder(context, "EVENING", 17, 30)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("REMINDER_TYPE") ?: "MORNING"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Channel for Android O (API 26) +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily morning and evening remembrance alerts"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            if (type == "MORNING") 1 else 2,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (type == "MORNING") {
            "☀️ Hisnul Muslim: Morning Adhkar"
        } else {
            "🌙 Hisnul Muslim: Evening Adhkar"
        }

        val contentText = if (type == "MORNING") {
            "Begin your day with blessings. Tap to read the Morning Supplications."
        } else {
            "Seek peace and protection. Tap to read the Evening Supplications."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(if (type == "MORNING") NOTIFICATION_ID_MORNING else NOTIFICATION_ID_EVENING, notification)

        // Reschedule for next day if still enabled in settings
        val sharedPref = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val isStillEnabled = if (type == "MORNING") {
            sharedPref.getBoolean("morning_enabled", false)
        } else {
            sharedPref.getBoolean("evening_enabled", false)
        }

        if (isStillEnabled) {
            val hour = if (type == "MORNING") 7 else 17
            val min = if (type == "MORNING") 0 else 30
            scheduleDailyReminder(context, type, hour, min)
        }
    }
}
