package com.example.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ui.screens.MandatoryAdhkarActivity
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object MandatoryAdhkarManager {

    private const val TAG = "NOOR_ZIKIR_MANDATORY"
    private const val PREFS_NAME = "noor_zikir_mandatory_prefs"

    // Master switch & General settings
    const val KEY_MASTER_ENABLED = "mandatory_master_enabled"
    const val KEY_RELIABLE_REMINDERS = "mandatory_reliable_reminders"
    const val KEY_SOUND_ENABLED = "mandatory_sound_enabled"
    const val KEY_VIBRATION_ENABLED = "mandatory_vibration_enabled"
    const val KEY_FULLSCREEN_ENABLED = "mandatory_fullscreen_enabled"
    const val KEY_AUTOPLAY_AUDIO = "mandatory_autoplay_audio"

    // Predefined Schedules
    const val SCHEDULE_ID_MORNING = "schedule_morning"
    const val SCHEDULE_ID_EVENING = "schedule_evening"
    const val SCHEDULE_ID_ISHA = "schedule_isha"

    // Backwards compatibility keys
    const val KEY_MORNING_ENABLED = "mandatory_morning_enabled"
    const val KEY_EVENING_ENABLED = "mandatory_evening_enabled"
    const val KEY_MORNING_HOUR = "mandatory_morning_hour"
    const val KEY_MORNING_MINUTE = "mandatory_morning_minute"
    const val KEY_EVENING_HOUR = "mandatory_evening_hour"
    const val KEY_EVENING_MINUTE = "mandatory_evening_minute"
    const val KEY_READING_DURATION = "mandatory_reading_duration_mins"
    const val KEY_MORNING_COMPLETED_DATE = "mandatory_morning_completed_date"
    const val KEY_EVENING_COMPLETED_DATE = "mandatory_evening_completed_date"

    // Active session state tracking
    const val KEY_SESSION_STATE = "session_state"
    const val KEY_CURRENT_SESSION_ID = "session_current_id"
    const val KEY_SESSION_START_TIME = "session_start_time"
    const val KEY_SESSION_TARGET_END_TIME = "session_target_end_time"
    const val KEY_SESSION_SCHEDULE_ID = "session_schedule_id"
    const val KEY_SESSION_SCHEDULE_TITLE = "session_schedule_title"
    const val KEY_SESSION_CATEGORY = "session_category"
    const val KEY_SESSION_DURATION_MINS = "session_duration_mins"

    // Custom schedules JSON key
    private const val KEY_CUSTOM_SCHEDULES_JSON = "custom_schedules_json"

    // Streak & History keys
    const val KEY_STREAK_COUNT = "mandatory_streak_count"
    const val KEY_LAST_STREAK_DATE = "mandatory_last_streak_date"
    const val KEY_COMPLETION_HISTORY = "mandatory_completion_history"

    // Notification Channel
    const val CHANNEL_ID_MANDATORY = "noor_zikir_mandatory_session_channel"
    const val NOTIF_ID_ACTIVE_SESSION = 2000
    const val NOTIF_ID_MISSED_SESSION = 2010

    fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    // ==========================================
    // SCHEDULE RETRIEVAL & PERSISTENCE
    // ==========================================

    fun getAllSchedules(context: Context): List<MandatorySchedule> {
        val prefs = getPrefs(context)
        val list = mutableListOf<MandatorySchedule>()

        // 1. Morning Schedule
        val morningHour = prefs.getInt(KEY_MORNING_HOUR, 6)
        val morningMin = prefs.getInt(KEY_MORNING_MINUTE, 0)
        val morningDur = prefs.getInt("duration_$SCHEDULE_ID_MORNING", prefs.getInt(KEY_READING_DURATION, 15))
        val morningEnabled = prefs.getBoolean(KEY_MORNING_ENABLED, true)
        list.add(
            MandatorySchedule(
                id = SCHEDULE_ID_MORNING,
                title = "Morning Zikir",
                category = "Morning & Evening",
                hour = morningHour,
                minute = morningMin,
                durationMinutes = morningDur,
                enabled = morningEnabled
            )
        )

        // 2. Evening Schedule
        val eveningHour = prefs.getInt(KEY_EVENING_HOUR, 18)
        val eveningMin = prefs.getInt(KEY_EVENING_MINUTE, 0)
        val eveningDur = prefs.getInt("duration_$SCHEDULE_ID_EVENING", prefs.getInt(KEY_READING_DURATION, 15))
        val eveningEnabled = prefs.getBoolean(KEY_EVENING_ENABLED, true)
        list.add(
            MandatorySchedule(
                id = SCHEDULE_ID_EVENING,
                title = "Evening Zikir",
                category = "Morning & Evening",
                hour = eveningHour,
                minute = eveningMin,
                durationMinutes = eveningDur,
                enabled = eveningEnabled
            )
        )

        // 3. After Isha Schedule
        val ishaHour = prefs.getInt("hour_$SCHEDULE_ID_ISHA", 21)
        val ishaMin = prefs.getInt("min_$SCHEDULE_ID_ISHA", 30)
        val ishaDur = prefs.getInt("duration_$SCHEDULE_ID_ISHA", 10)
        val ishaEnabled = prefs.getBoolean("enabled_$SCHEDULE_ID_ISHA", false)
        list.add(
            MandatorySchedule(
                id = SCHEDULE_ID_ISHA,
                title = "After Isha Zikir",
                category = "Sleeping & Waking Up",
                hour = ishaHour,
                minute = ishaMin,
                durationMinutes = ishaDur,
                enabled = ishaEnabled
            )
        )

        // 4. Custom Schedules
        val customJson = prefs.getString(KEY_CUSTOM_SCHEDULES_JSON, null)
        if (!customJson.isNullOrBlank()) {
            try {
                val array = JSONArray(customJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        MandatorySchedule(
                            id = obj.getString("id"),
                            title = obj.getString("title"),
                            category = obj.getString("category"),
                            hour = obj.getInt("hour"),
                            minute = obj.getInt("minute"),
                            durationMinutes = obj.getInt("durationMinutes"),
                            enabled = obj.getBoolean("enabled")
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing custom schedules JSON: ${e.message}", e)
            }
        }

        return list
    }

    fun saveSchedule(context: Context, schedule: MandatorySchedule) {
        val prefs = getPrefs(context)
        Log.d(TAG, "saveSchedule: id=${schedule.id}, title=${schedule.title}, time=${schedule.hour}:${schedule.minute}, duration=${schedule.durationMinutes}, enabled=${schedule.enabled}")
        when (schedule.id) {
            SCHEDULE_ID_MORNING -> {
                prefs.edit()
                    .putBoolean(KEY_MORNING_ENABLED, schedule.enabled)
                    .putInt(KEY_MORNING_HOUR, schedule.hour)
                    .putInt(KEY_MORNING_MINUTE, schedule.minute)
                    .putInt("duration_$SCHEDULE_ID_MORNING", schedule.durationMinutes)
                    .apply()
            }
            SCHEDULE_ID_EVENING -> {
                prefs.edit()
                    .putBoolean(KEY_EVENING_ENABLED, schedule.enabled)
                    .putInt(KEY_EVENING_HOUR, schedule.hour)
                    .putInt(KEY_EVENING_MINUTE, schedule.minute)
                    .putInt("duration_$SCHEDULE_ID_EVENING", schedule.durationMinutes)
                    .apply()
            }
            SCHEDULE_ID_ISHA -> {
                prefs.edit()
                    .putBoolean("enabled_$SCHEDULE_ID_ISHA", schedule.enabled)
                    .putInt("hour_$SCHEDULE_ID_ISHA", schedule.hour)
                    .putInt("min_$SCHEDULE_ID_ISHA", schedule.minute)
                    .putInt("duration_$SCHEDULE_ID_ISHA", schedule.durationMinutes)
                    .apply()
            }
            else -> {
                // Custom schedule update
                val customSchedules = getAllSchedules(context).filter { it.id != SCHEDULE_ID_MORNING && it.id != SCHEDULE_ID_EVENING && it.id != SCHEDULE_ID_ISHA }.toMutableList()
                val idx = customSchedules.indexOfFirst { it.id == schedule.id }
                if (idx >= 0) {
                    customSchedules[idx] = schedule
                } else {
                    customSchedules.add(schedule)
                }
                saveCustomSchedules(context, customSchedules)
            }
        }
        scheduleAlarms(context)
    }

    private fun saveCustomSchedules(context: Context, schedules: List<MandatorySchedule>) {
        val array = JSONArray()
        for (s in schedules) {
            val obj = JSONObject().apply {
                put("id", s.id)
                put("title", s.title)
                put("category", s.category)
                put("hour", s.hour)
                put("minute", s.minute)
                put("durationMinutes", s.durationMinutes)
                put("enabled", s.enabled)
            }
            array.put(obj)
        }
        getPrefs(context).edit().putString(KEY_CUSTOM_SCHEDULES_JSON, array.toString()).apply()
    }

    // ==========================================
    // STATE MACHINE & ACTIVE SESSION PERSISTENCE
    // ==========================================

    fun getSessionState(context: Context): MandatorySessionState {
        val stateName = getPrefs(context).getString(KEY_SESSION_STATE, MandatorySessionState.IDLE.name)
        return try {
            MandatorySessionState.valueOf(stateName ?: MandatorySessionState.IDLE.name)
        } catch (_: Exception) {
            MandatorySessionState.IDLE
        }
    }

    fun setSessionState(context: Context, state: MandatorySessionState) {
        Log.d(TAG, "session state changes -> $state")
        getPrefs(context).edit().putString(KEY_SESSION_STATE, state.name).apply()
    }

    fun startSession(
        context: Context,
        scheduleId: String,
        scheduleTitle: String,
        category: String,
        durationMinutes: Int
    ): String {
        val sessionId = "session_${scheduleId}_${System.currentTimeMillis()}"
        val startTime = System.currentTimeMillis()
        val targetEndTime = startTime + (durationMinutes * 60 * 1000L)

        Log.d(TAG, "session start: sessionId=$sessionId, schedule=$scheduleTitle, duration=${durationMinutes}m, targetEndTime=$targetEndTime")

        getPrefs(context).edit()
            .putString(KEY_SESSION_STATE, MandatorySessionState.ACTIVE.name)
            .putString(KEY_CURRENT_SESSION_ID, sessionId)
            .putLong(KEY_SESSION_START_TIME, startTime)
            .putLong(KEY_SESSION_TARGET_END_TIME, targetEndTime)
            .putString(KEY_SESSION_SCHEDULE_ID, scheduleId)
            .putString(KEY_SESSION_SCHEDULE_TITLE, scheduleTitle)
            .putString(KEY_SESSION_CATEGORY, category)
            .putInt(KEY_SESSION_DURATION_MINS, durationMinutes)
            .apply()

        showPersistentNotification(context, scheduleId, scheduleTitle, category, durationMinutes)
        return sessionId
    }

    fun completeSession(context: Context, scheduleId: String, scheduleTitle: String) {
        Log.d(TAG, "session completion: scheduleId=$scheduleId, title=$scheduleTitle")
        setSessionState(context, MandatorySessionState.COMPLETED)
        cancelPersistentNotification(context)

        // Record completion for today
        val today = getTodayDateString()
        val prefs = getPrefs(context)
        if (scheduleId == SCHEDULE_ID_MORNING) {
            prefs.edit().putString(KEY_MORNING_COMPLETED_DATE, today).apply()
        } else if (scheduleId == SCHEDULE_ID_EVENING) {
            prefs.edit().putString(KEY_EVENING_COMPLETED_DATE, today).apply()
        }
        prefs.edit().putString("completed_${scheduleId}_$today", today).apply()

        // Update streak
        updateStreakOnCompletion(context)

        // Record in history log
        recordHistory(context, scheduleTitle, "COMPLETED")

        // Schedule next occurrence
        scheduleAlarms(context)
    }

    fun cancelSession(context: Context, scheduleId: String, scheduleTitle: String) {
        Log.d(TAG, "session cancelled by user: scheduleId=$scheduleId, title=$scheduleTitle")
        setSessionState(context, MandatorySessionState.CANCELLED)
        cancelPersistentNotification(context)
        recordHistory(context, scheduleTitle, "CANCELLED")
        scheduleAlarms(context)
    }

    private fun updateStreakOnCompletion(context: Context) {
        val prefs = getPrefs(context)
        val today = getTodayDateString()
        val lastDate = prefs.getString(KEY_LAST_STREAK_DATE, "")
        var streak = prefs.getInt(KEY_STREAK_COUNT, 0)

        if (lastDate != today) {
            val yesterdayCal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -1)
            }
            val yesterday = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(yesterdayCal.time)
            streak = if (lastDate == yesterday) streak + 1 else 1
            prefs.edit()
                .putString(KEY_LAST_STREAK_DATE, today)
                .putInt(KEY_STREAK_COUNT, streak)
                .apply()
            Log.d(TAG, "Streak updated to $streak days (lastDate=$today)")
        }
    }

    private fun recordHistory(context: Context, title: String, status: String) {
        val prefs = getPrefs(context)
        val historyStr = prefs.getString(KEY_COMPLETION_HISTORY, "[]") ?: "[]"
        try {
            val array = JSONArray(historyStr)
            val obj = JSONObject().apply {
                put("title", title)
                put("timestamp", System.currentTimeMillis())
                put("date", getTodayDateString())
                put("status", status)
            }
            // Keep last 50 entries
            val updated = JSONArray()
            updated.put(obj)
            for (i in 0 until minOf(array.length(), 49)) {
                updated.put(array.get(i))
            }
            prefs.edit().putString(KEY_COMPLETION_HISTORY, updated.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error recording history: ${e.message}")
        }
    }

    fun isScheduleCompletedToday(context: Context, scheduleId: String): Boolean {
        val prefs = getPrefs(context)
        val today = getTodayDateString()
        return when (scheduleId) {
            SCHEDULE_ID_MORNING -> prefs.getString(KEY_MORNING_COMPLETED_DATE, "") == today
            SCHEDULE_ID_EVENING -> prefs.getString(KEY_EVENING_COMPLETED_DATE, "") == today
            else -> prefs.getString("completed_${scheduleId}_$today", "") == today
        }
    }

    // ==========================================
    // ALARM MANAGER SCHEDULING (EXACT / COMPLIANT)
    // ==========================================

    fun scheduleAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        if (alarmManager == null) {
            Log.e(TAG, "permission problems: AlarmManager is null")
            return
        }

        val schedules = getAllSchedules(context)
        Log.d(TAG, "schedule registration: Total schedules configured = ${schedules.size}")

        // Check exact alarm permissions
        val canExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
        Log.d(TAG, "schedule registration: Exact alarm permitted = $canExact")

        for (schedule in schedules) {
            val requestCode = getRequestCode(schedule.id)
            val intent = Intent(context, MandatoryAdhkarReceiver::class.java).apply {
                action = "ACTION_MANDATORY_ZIKIR_ALARM_${schedule.id}"
                putExtra("SCHEDULE_ID", schedule.id)
                putExtra("SCHEDULE_TITLE", schedule.title)
                putExtra("CATEGORY", schedule.category)
                putExtra("DURATION_MINUTES", schedule.durationMinutes)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (!schedule.enabled) {
                alarmManager.cancel(pendingIntent)
                Log.d(TAG, "schedule registration: Cancelled disabled alarm for schedule=${schedule.id} (${schedule.title})")
                continue
            }

            // Calculate next trigger time
            val now = System.currentTimeMillis()
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, schedule.hour)
                set(Calendar.MINUTE, schedule.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                // If already passed today or completed today, schedule for tomorrow
                val completedToday = isScheduleCompletedToday(context, schedule.id)
                if (timeInMillis <= now || completedToday) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            val triggerTime = cal.timeInMillis
            val formattedTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(triggerTime))
            Log.d(TAG, "schedule registration: Registering alarm for '${schedule.title}' at $formattedTime (reqCode=$requestCode)")

            try {
                // Best practice: AlarmClockInfo ensures reliable wakeups even in Doze Mode and on locked screens
                val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerTime, pendingIntent)
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
                Log.d(TAG, "schedule registration: setAlarmClock succeeded for '${schedule.title}'")
            } catch (e: Exception) {
                Log.w(TAG, "schedule registration: setAlarmClock failed (${e.message}), trying fallback exact alarm")
                try {
                    if (canExact && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                    }
                } catch (e2: Exception) {
                    Log.e(TAG, "schedule registration: Failed to set alarm for '${schedule.title}': ${e2.message}", e2)
                }
            }
        }

        // Set overall state to SCHEDULED if idle
        if (getSessionState(context) == MandatorySessionState.IDLE) {
            setSessionState(context, MandatorySessionState.SCHEDULED)
        }
    }

    private fun getRequestCode(scheduleId: String): Int {
        return when (scheduleId) {
            SCHEDULE_ID_MORNING -> 501
            SCHEDULE_ID_EVENING -> 502
            SCHEDULE_ID_ISHA -> 503
            else -> 500 + Math.abs(scheduleId.hashCode() % 1000)
        }
    }

    // ==========================================
    // NOTIFICATIONS & FULL-SCREEN INTENT
    // ==========================================

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID_MANDATORY,
                "Mandatory Zikir Sessions",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High-priority alarms and full-screen reminders for scheduled Morning, Evening, and Daily Zikir routines."
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 400, 200, 400, 200, 600)
                setSound(soundUri, audioAttributes)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "notification creation: Notification channel created/verified: $CHANNEL_ID_MANDATORY")
        }
    }

    fun showPersistentNotification(
        context: Context,
        scheduleId: String,
        scheduleTitle: String,
        category: String,
        durationMinutes: Int
    ) {
        createNotificationChannel(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MandatoryAdhkarActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            putExtra("SCHEDULE_ID", scheduleId)
            putExtra("SCHEDULE_TITLE", scheduleTitle)
            putExtra("CATEGORY", category)
            putExtra("DURATION_MINUTES", durationMinutes)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            getRequestCode(scheduleId) + 100,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notifTitle = "✨ $scheduleTitle Active"
        val notifBody = "Your scheduled $durationMinutes-minute Zikir session is in progress. Tap to open and recite."

        Log.d(TAG, "notification creation: Building active session notification (FullScreenIntent=true, category=ALARM)")

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_MANDATORY)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(notifTitle)
            .setContentText(notifBody)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 400, 200, 400, 200, 600))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setFullScreenIntent(pendingIntent, true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .build()

        notificationManager.notify(NOTIF_ID_ACTIVE_SESSION, notification)
    }

    fun cancelPersistentNotification(context: Context) {
        Log.d(TAG, "service start/stop: Removing active session notification ID=$NOTIF_ID_ACTIVE_SESSION")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIF_ID_ACTIVE_SESSION)
    }

    // ==========================================
    // TESTING & DIAGNOSTICS
    // ==========================================

    fun triggerTestNow(context: Context, scheduleId: String = SCHEDULE_ID_MORNING) {
        val schedules = getAllSchedules(context)
        val schedule = schedules.find { it.id == scheduleId } ?: schedules.firstOrNull() ?: MandatorySchedule(
            id = SCHEDULE_ID_MORNING,
            title = "Morning Zikir",
            category = "Morning & Evening",
            hour = 6,
            minute = 0,
            durationMinutes = 3,
            enabled = true
        )

        Log.d(TAG, "alarm trigger: Test alarm triggered manually for schedule=${schedule.id} (${schedule.title})")

        val intent = Intent(context, MandatoryAdhkarReceiver::class.java).apply {
            action = "ACTION_MANDATORY_ZIKIR_ALARM_${schedule.id}"
            putExtra("SCHEDULE_ID", schedule.id)
            putExtra("SCHEDULE_TITLE", schedule.title)
            putExtra("CATEGORY", schedule.category)
            putExtra("DURATION_MINUTES", schedule.durationMinutes)
            putExtra("IS_TEST", true)
        }
        context.sendBroadcast(intent)
    }

    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            pm?.isIgnoringBatteryOptimizations(context.packageName) ?: false
        } else {
            true
        }
    }
}
