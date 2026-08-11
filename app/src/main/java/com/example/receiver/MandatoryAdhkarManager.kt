package com.example.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.ui.screens.MandatoryAdhkarActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object MandatoryAdhkarManager {

    private const val PREFS_NAME = "hisnul_muslim_prefs"
    const val KEY_MORNING_ENABLED = "mandatory_morning_enabled"
    const val KEY_EVENING_ENABLED = "mandatory_evening_enabled"
    const val KEY_MORNING_HOUR = "mandatory_morning_hour"
    const val KEY_MORNING_MINUTE = "mandatory_morning_minute"
    const val KEY_EVENING_HOUR = "mandatory_evening_hour"
    const val KEY_EVENING_MINUTE = "mandatory_evening_minute"
    const val KEY_READING_DURATION = "mandatory_reading_duration_mins"
    const val KEY_FULLSCREEN_ENABLED = "mandatory_fullscreen_enabled"
    const val KEY_MORNING_COMPLETED_DATE = "mandatory_morning_completed_date"
    const val KEY_EVENING_COMPLETED_DATE = "mandatory_evening_completed_date"

    const val CHANNEL_ID_MANDATORY = "mandatory_adhkar_channel"
    const val NOTIF_ID_MORNING = 2001
    const val NOTIF_ID_EVENING = 2002

    fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    fun isMorningCompletedToday(context: Context): Boolean {
        val prefs = getPrefs(context)
        return prefs.getString(KEY_MORNING_COMPLETED_DATE, "") == getTodayDateString()
    }

    fun isEveningCompletedToday(context: Context): Boolean {
        val prefs = getPrefs(context)
        return prefs.getString(KEY_EVENING_COMPLETED_DATE, "") == getTodayDateString()
    }

    fun setMorningCompletedToday(context: Context, completed: Boolean) {
        val prefs = getPrefs(context)
        val value = if (completed) getTodayDateString() else ""
        prefs.edit().putString(KEY_MORNING_COMPLETED_DATE, value).apply()
        if (completed) {
            cancelPersistentNotification(context, "MORNING")
        }
    }

    fun setEveningCompletedToday(context: Context, completed: Boolean) {
        val prefs = getPrefs(context)
        val value = if (completed) getTodayDateString() else ""
        prefs.edit().putString(KEY_EVENING_COMPLETED_DATE, value).apply()
        if (completed) {
            cancelPersistentNotification(context, "EVENING")
        }
    }

    fun scheduleAlarms(context: Context) {
        val prefs = getPrefs(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val morningEnabled = prefs.getBoolean(KEY_MORNING_ENABLED, false)
        val eveningEnabled = prefs.getBoolean(KEY_EVENING_ENABLED, false)

        val morningHour = prefs.getInt(KEY_MORNING_HOUR, 6)
        val morningMin = prefs.getInt(KEY_MORNING_MINUTE, 0)

        val eveningHour = prefs.getInt(KEY_EVENING_HOUR, 18)
        val eveningMin = prefs.getInt(KEY_EVENING_MINUTE, 0)

        // Morning alarm
        val morningIntent = Intent(context, MandatoryAdhkarReceiver::class.java).apply {
            action = "ACTION_MANDATORY_MORNING_ALARM"
            putExtra("ADHKAR_TYPE", "MORNING")
        }
        val morningPendingIntent = PendingIntent.getBroadcast(
            context,
            201,
            morningIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (morningEnabled) {
            scheduleDailyAlarm(alarmManager, morningPendingIntent, morningHour, morningMin)
        } else {
            alarmManager.cancel(morningPendingIntent)
            cancelPersistentNotification(context, "MORNING")
        }

        // Evening alarm
        val eveningIntent = Intent(context, MandatoryAdhkarReceiver::class.java).apply {
            action = "ACTION_MANDATORY_EVENING_ALARM"
            putExtra("ADHKAR_TYPE", "EVENING")
        }
        val eveningPendingIntent = PendingIntent.getBroadcast(
            context,
            202,
            eveningIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (eveningEnabled) {
            scheduleDailyAlarm(alarmManager, eveningPendingIntent, eveningHour, eveningMin)
        } else {
            alarmManager.cancel(eveningPendingIntent)
            cancelPersistentNotification(context, "EVENING")
        }
    }

    private fun scheduleDailyAlarm(
        alarmManager: AlarmManager,
        pendingIntent: PendingIntent,
        hour: Int,
        minute: Int
    ) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            val now = System.currentTimeMillis()
            if (timeInMillis <= now) {
                // If set to the current minute (e.g. 14:05 and current time is 14:05:15),
                // trigger immediately in 1 second instead of pushing to tomorrow!
                if (now - timeInMillis < 60000) {
                    timeInMillis = now + 1000
                } else {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
        }

        try {
            val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
        } catch (_: SecurityException) {
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
        }
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID_MANDATORY,
                "Mandatory Adhkar Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Mandatory Morning and Evening Adhkar daily alerts"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showPersistentNotification(context: Context, type: String) {
        createNotificationChannel(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val isMorning = type == "MORNING"
        val notifId = if (isMorning) NOTIF_ID_MORNING else NOTIF_ID_EVENING

        val intent = Intent(context, MandatoryAdhkarActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            putExtra("ADHKAR_TYPE", type)
            putExtra("READING_DURATION", getPrefs(context).getInt(KEY_READING_DURATION, 3))
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            if (isMorning) 301 else 302,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isMorning) "☀️ Mandatory Morning Adhkar" else "🌙 Mandatory Evening Adhkar"
        val body = if (isMorning) {
            "Time for your Mandatory Morning Supplications. Tap to start reading."
        } else {
            "Time for your Mandatory Evening Supplications. Tap to start reading."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_MANDATORY)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setFullScreenIntent(pendingIntent, true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .build()

        notificationManager.notify(notifId, notification)
    }

    fun cancelPersistentNotification(context: Context, type: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifId = if (type == "MORNING") NOTIF_ID_MORNING else NOTIF_ID_EVENING
        notificationManager.cancel(notifId)
    }
}
