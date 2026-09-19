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
import com.example.MainActivity
import com.example.ui.screens.MandatoryAdhkarActivity
import com.example.util.VibrationHelper
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object MandatoryAdhkarManager {

    const val TAG = "NOOR_ZIKIR_MANDATORY"
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

    // Notification Channel & IDs
    const val CHANNEL_ID_MANDATORY = "noor_zikir_mandatory_session_channel"
    const val NOTIF_ID_ACTIVE_SESSION = 2000
    const val NOTIF_ID_MISSED_SESSION = 2010
    const val NOTIF_ID_UNREAD_REMINDER = 2020

    // Unread Tracking Keys
    const val KEY_UNREAD_PENDING = "mandatory_unread_pending"
    const val KEY_UNREAD_SCHEDULE_ID = "mandatory_unread_schedule_id"
    const val KEY_UNREAD_SCHEDULE_TITLE = "mandatory_unread_schedule_title"
    const val KEY_UNREAD_CATEGORY = "mandatory_unread_category"
    const val KEY_UNREAD_TIMESTAMP = "mandatory_unread_timestamp"
    const val KEY_UNREAD_REMINDER_SHOWN = "mandatory_unread_reminder_shown"

    fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    fun formatTimestamp(millis: Long): String {
        if (millis <= 0L) return "Not Scheduled"
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(millis))
    }

    fun getOccurrenceId(scheduleId: String, timestampMillis: Long): String {
        val timePart = SimpleDateFormat("yyyy-MM-dd_HH:mm", Locale.US).format(Date(timestampMillis))
        return "${scheduleId}_$timePart"
    }

    /**
     * Mathematically calculates the next trigger timestamp strictly in the future (> baselineMillis).
     * Handles timezone and daylight savings adjustments accurately.
     */
    fun calculateNextOccurrence(hour: Int, minute: Int, baselineMillis: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = baselineMillis
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If the calculated time is <= baselineMillis (i.e., today's slot has already arrived or passed), step to tomorrow
        if (cal.timeInMillis <= baselineMillis) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }

    // ==========================================
    // SCHEDULE RETRIEVAL & PERSISTENCE
    // ==========================================

    fun getAllSchedules(context: Context): List<MandatorySchedule> {
        val prefs = getPrefs(context)
        val list = mutableListOf<MandatorySchedule>()
        val defaultTz = TimeZone.getDefault().id

        // 1. Morning Schedule
        val morningHour = prefs.getInt(KEY_MORNING_HOUR, 6)
        val morningMin = prefs.getInt(KEY_MORNING_MINUTE, 0)
        val morningDur = prefs.getInt("duration_$SCHEDULE_ID_MORNING", prefs.getInt(KEY_READING_DURATION, 3))
        val morningEnabled = prefs.getBoolean(KEY_MORNING_ENABLED, true)
        val morningNext = prefs.getLong("next_occurrence_$SCHEDULE_ID_MORNING", 0L)
        val morningLastTriggered = prefs.getString("last_triggered_$SCHEDULE_ID_MORNING", "") ?: ""
        val morningLastCompleted = prefs.getString("last_completed_$SCHEDULE_ID_MORNING", "") ?: ""

        list.add(
            MandatorySchedule(
                id = SCHEDULE_ID_MORNING,
                title = "Morning Zikir",
                category = "Morning Adhkar",
                hour = morningHour,
                minute = morningMin,
                durationMinutes = morningDur,
                enabled = morningEnabled,
                repeatType = "EVERY_DAY",
                timezone = prefs.getString("tz_$SCHEDULE_ID_MORNING", defaultTz) ?: defaultTz,
                nextOccurrence = morningNext,
                lastTriggeredOccurrence = morningLastTriggered,
                lastCompletedOccurrence = morningLastCompleted
            )
        )

        // 2. Evening Schedule
        val eveningHour = prefs.getInt(KEY_EVENING_HOUR, 18)
        val eveningMin = prefs.getInt(KEY_EVENING_MINUTE, 0)
        val eveningDur = prefs.getInt("duration_$SCHEDULE_ID_EVENING", prefs.getInt(KEY_READING_DURATION, 3))
        val eveningEnabled = prefs.getBoolean(KEY_EVENING_ENABLED, true)
        val eveningNext = prefs.getLong("next_occurrence_$SCHEDULE_ID_EVENING", 0L)
        val eveningLastTriggered = prefs.getString("last_triggered_$SCHEDULE_ID_EVENING", "") ?: ""
        val eveningLastCompleted = prefs.getString("last_completed_$SCHEDULE_ID_EVENING", "") ?: ""

        list.add(
            MandatorySchedule(
                id = SCHEDULE_ID_EVENING,
                title = "Evening Zikir",
                category = "Evening Adhkar",
                hour = eveningHour,
                minute = eveningMin,
                durationMinutes = eveningDur,
                enabled = eveningEnabled,
                repeatType = "EVERY_DAY",
                timezone = prefs.getString("tz_$SCHEDULE_ID_EVENING", defaultTz) ?: defaultTz,
                nextOccurrence = eveningNext,
                lastTriggeredOccurrence = eveningLastTriggered,
                lastCompletedOccurrence = eveningLastCompleted
            )
        )

        // 3. Custom Schedules
        val customJson = prefs.getString(KEY_CUSTOM_SCHEDULES_JSON, null)
        if (!customJson.isNullOrBlank()) {
            try {
                val array = JSONArray(customJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val id = obj.getString("id")
                    list.add(
                        MandatorySchedule(
                            id = id,
                            title = obj.getString("title"),
                            category = obj.getString("category"),
                            hour = obj.getInt("hour"),
                            minute = obj.getInt("minute"),
                            durationMinutes = obj.getInt("durationMinutes"),
                            enabled = obj.getBoolean("enabled"),
                            repeatType = obj.optString("repeatType", "EVERY_DAY"),
                            timezone = obj.optString("timezone", defaultTz),
                            nextOccurrence = prefs.getLong("next_occurrence_$id", 0L),
                            lastTriggeredOccurrence = prefs.getString("last_triggered_$id", "") ?: "",
                            lastCompletedOccurrence = prefs.getString("last_completed_$id", "") ?: ""
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "errors: Error parsing custom schedules JSON: ${e.message}", e)
            }
        }

        return list
    }

    /**
     * Validates and persists a schedule, updating AlarmManager immediately.
     */
    fun saveSchedule(context: Context, schedule: MandatorySchedule) {
        val prefs = getPrefs(context)
        val validHour = schedule.hour.coerceIn(0, 23)
        val validMinute = schedule.minute.coerceIn(0, 59)
        val validDuration = schedule.durationMinutes.coerceIn(1, 120)
        val currentTz = TimeZone.getDefault().id

        Log.d(TAG, "schedule updated: id=${schedule.id}, title=${schedule.title}, time=$validHour:$validMinute, duration=${validDuration}m, enabled=${schedule.enabled}, tz=$currentTz")

        val editor = prefs.edit()
        when (schedule.id) {
            SCHEDULE_ID_MORNING -> {
                editor.putBoolean(KEY_MORNING_ENABLED, schedule.enabled)
                    .putInt(KEY_MORNING_HOUR, validHour)
                    .putInt(KEY_MORNING_MINUTE, validMinute)
                    .putInt("duration_$SCHEDULE_ID_MORNING", validDuration)
                    .putString("tz_$SCHEDULE_ID_MORNING", currentTz)
            }
            SCHEDULE_ID_EVENING -> {
                editor.putBoolean(KEY_EVENING_ENABLED, schedule.enabled)
                    .putInt(KEY_EVENING_HOUR, validHour)
                    .putInt(KEY_EVENING_MINUTE, validMinute)
                    .putInt("duration_$SCHEDULE_ID_EVENING", validDuration)
                    .putString("tz_$SCHEDULE_ID_EVENING", currentTz)
            }
            SCHEDULE_ID_ISHA -> {
                editor.putBoolean("enabled_$SCHEDULE_ID_ISHA", schedule.enabled)
                    .putInt("hour_$SCHEDULE_ID_ISHA", validHour)
                    .putInt("min_$SCHEDULE_ID_ISHA", validMinute)
                    .putInt("duration_$SCHEDULE_ID_ISHA", validDuration)
                    .putString("tz_$SCHEDULE_ID_ISHA", currentTz)
            }
            else -> {
                val customSchedules = getAllSchedules(context)
                    .filter { it.id != SCHEDULE_ID_MORNING && it.id != SCHEDULE_ID_EVENING && it.id != SCHEDULE_ID_ISHA }
                    .toMutableList()
                val idx = customSchedules.indexOfFirst { it.id == schedule.id }
                val updated = schedule.copy(hour = validHour, minute = validMinute, durationMinutes = validDuration, timezone = currentTz)
                if (idx >= 0) {
                    customSchedules[idx] = updated
                } else {
                    customSchedules.add(updated)
                }
                saveCustomSchedules(context, customSchedules)
            }
        }
        editor.apply()

        if (!schedule.enabled) {
            cancelAlarmForSchedule(context, schedule.id)
            prefs.edit()
                .putLong("next_occurrence_${schedule.id}", 0L)
                .remove("last_triggered_${schedule.id}")
                .apply()
            Log.d(TAG, "schedule disabled: Cancelled alarm for schedule ID=${schedule.id}")
        } else {
            // First cancel any existing alarm to avoid stale triggers at old time
            cancelAlarmForSchedule(context, schedule.id)
            val nextTime = calculateNextOccurrence(validHour, validMinute, System.currentTimeMillis())
            prefs.edit()
                .putLong("next_occurrence_${schedule.id}", nextTime)
                .remove("last_triggered_${schedule.id}")
                .apply()
            val cleanSchedule = schedule.copy(
                hour = validHour,
                minute = validMinute,
                durationMinutes = validDuration,
                nextOccurrence = nextTime,
                timezone = currentTz
            )
            registerAlarmForSchedule(context, cleanSchedule)
            Log.d(TAG, "schedule enabled: Registered exact alarm for schedule ID=${schedule.id} at ${formatTimestamp(nextTime)}")
        }
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
                put("repeatType", s.repeatType)
                put("timezone", s.timezone)
            }
            array.put(obj)
        }
        getPrefs(context).edit().putString(KEY_CUSTOM_SCHEDULES_JSON, array.toString()).apply()
    }

    // ==========================================
    // EXACT ALARM REGISTRATION & CANCELLATION
    // ==========================================

    fun registerAlarmForSchedule(context: Context, schedule: MandatorySchedule) {
        if (!schedule.enabled) {
            Log.d(TAG, "alarm registration: Skipping disabled schedule ID=${schedule.id}")
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        if (alarmManager == null) {
            Log.e(TAG, "errors: AlarmManager is null during registration for ${schedule.id}")
            return
        }

        val now = System.currentTimeMillis()
        var triggerTime = schedule.nextOccurrence
        if (triggerTime <= now) {
            triggerTime = calculateNextOccurrence(schedule.hour, schedule.minute, now)
            getPrefs(context).edit().putLong("next_occurrence_${schedule.id}", triggerTime).apply()
        }

        val requestCode = getRequestCode(schedule.id)
        val occurrenceId = getOccurrenceId(schedule.id, triggerTime)

        val intent = Intent(context, MandatoryAdhkarReceiver::class.java).apply {
            action = "com.example.ACTION_MANDATORY_ZIKIR_${schedule.id}"
            putExtra("SCHEDULE_ID", schedule.id)
            putExtra("SCHEDULE_TITLE", schedule.title)
            putExtra("CATEGORY", schedule.category)
            putExtra("DURATION_MINUTES", schedule.durationMinutes)
            putExtra("EXPECTED_OCCURRENCE_ID", occurrenceId)
            putExtra("EXPECTED_TIMESTAMP", triggerTime)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Show intent for AlarmClockInfo (tapping status bar clock icon opens app)
        val showIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val showPendingIntent = PendingIntent.getActivity(
            context,
            requestCode + 1000,
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val canExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
        Log.d(TAG, "exact alarm permission status: canScheduleExactAlarms=$canExact for schedule=${schedule.id}")

        try {
            // AlarmClockInfo guarantees exact wakeups across Doze mode and all OEM restrictions
            val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerTime, showPendingIntent)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            Log.d(TAG, "alarm registered: schedule ID=${schedule.id} (${schedule.title}), triggerTime=${formatTimestamp(triggerTime)} (reqCode=$requestCode) via setAlarmClock")
        } catch (e: Exception) {
            Log.w(TAG, "errors: setAlarmClock failed (${e.message}), attempting fallback exact alarm")
            try {
                if (canExact && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                    Log.d(TAG, "alarm registered: schedule ID=${schedule.id} via setExactAndAllowWhileIdle at ${formatTimestamp(triggerTime)}")
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                    Log.d(TAG, "alarm registered: schedule ID=${schedule.id} via setAndAllowWhileIdle at ${formatTimestamp(triggerTime)}")
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                    Log.d(TAG, "alarm registered: schedule ID=${schedule.id} via standard set at ${formatTimestamp(triggerTime)}")
                }
            } catch (e2: Exception) {
                Log.e(TAG, "errors: Failed completely to register alarm for schedule ${schedule.id}: ${e2.message}", e2)
            }
        }
    }

    fun cancelAlarmForSchedule(context: Context, scheduleId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val requestCode = getRequestCode(scheduleId)
        val intent = Intent(context, MandatoryAdhkarReceiver::class.java).apply {
            action = "com.example.ACTION_MANDATORY_ZIKIR_$scheduleId"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "alarm cancelled: schedule ID=$scheduleId (reqCode=$requestCode)")
        }
    }

    fun scheduleAlarms(context: Context) {
        val schedules = getAllSchedules(context)
        Log.d(TAG, "schedule registration: Syncing all active schedules (count=${schedules.size})")
        for (schedule in schedules) {
            if (schedule.enabled) {
                registerAlarmForSchedule(context, schedule)
            } else {
                cancelAlarmForSchedule(context, schedule.id)
            }
        }
    }

    // ==========================================
    // ALARM TRIGGER / RECEIVER EXECUTION PIPELINE
    // ==========================================

    /**
     * Executes when an alarm fires.
     * Guaranteed sequence:
     * 1. Validate schedule.
     * 2. Prevent duplicates (check lastTriggeredOccurrence).
     * 3. Persist current occurrence as triggered.
     * 4. IMMEDIATELY CALCULATE AND REGISTER NEXT OCCURRENCE with AlarmManager.
     * 5. Launch WakeLock, Notification with FullScreenIntent, and session UI.
     */
    fun onAlarmTriggered(
        context: Context,
        scheduleId: String,
        scheduleTitle: String,
        category: String,
        durationMinutes: Int,
        isTest: Boolean
    ) {
        val currentTimestamp = System.currentTimeMillis()
        val occurrenceId = getOccurrenceId(scheduleId, currentTimestamp)
        val prefs = getPrefs(context)

        Log.d(TAG, "alarm fired: schedule ID=$scheduleId, occurrence ID=$occurrenceId, current timestamp=${formatTimestamp(currentTimestamp)}, isTest=$isTest")

        // 1. Verify schedule enabled
        val allSchedules = getAllSchedules(context)
        val schedule = allSchedules.find { it.id == scheduleId }
        val isEnabled = schedule?.enabled ?: true

        if (!isEnabled && !isTest) {
            Log.d(TAG, "receiver execution: Schedule $scheduleId is currently disabled. Cancelling downstream actions.")
            cancelAlarmForSchedule(context, scheduleId)
            return
        }

        // 2. Duplicate Detection
        val lastTriggered = prefs.getString("last_triggered_$scheduleId", "")
        if (lastTriggered == occurrenceId && !isTest) {
            Log.w(TAG, "duplicate detection: Occurrence ID $occurrenceId already processed. Skipping duplicate trigger.")
            // Ensure next alarm is in place
            if (schedule != null) {
                registerAlarmForSchedule(context, schedule)
            }
            return
        }

        // 3. Mark current occurrence as triggered
        prefs.edit().putString("last_triggered_$scheduleId", occurrenceId).apply()
        setSessionState(context, MandatorySessionState.TRIGGERED)

        // 4. CRITICAL: CALCULATE AND REGISTER NEXT ALARM IMMEDIATELY BEFORE SESSION UI
        if (!isTest && schedule != null) {
            val nextOccurrence = calculateNextOccurrence(schedule.hour, schedule.minute, currentTimestamp)
            prefs.edit().putLong("next_occurrence_$scheduleId", nextOccurrence).apply()
            registerAlarmForSchedule(context, schedule.copy(nextOccurrence = nextOccurrence))
            Log.d(TAG, "next alarm registration: Next occurrence for schedule ID=$scheduleId registered for ${formatTimestamp(nextOccurrence)}")
        }

        // 5. Acquire temporary WakeLock to wake up screen
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        @Suppress("DEPRECATION")
        val wakeLock = powerManager?.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
            "NoorZikir:MandatoryWakeLock"
        )
        try {
            wakeLock?.acquire(15000L)
            Log.d(TAG, "alarm trigger: WakeLock acquired for screen wakeup (15s)")
        } catch (e: Exception) {
            Log.w(TAG, "errors: WakeLock acquire failed: ${e.message}")
        }

        // 6. Trigger two soft vibrations for Auto Azkar opening
        VibrationHelper.triggerTwoSoftVibrations(context)

        // 7. Show persistent notification with FullScreenIntent
        setSessionState(context, MandatorySessionState.STARTING)
        showPersistentNotification(
            context = context,
            scheduleId = scheduleId,
            scheduleTitle = scheduleTitle,
            category = category,
            durationMinutes = durationMinutes
        )

        // 8. Direct activity launch where permitted
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
            context.startActivity(fullScreenIntent)
            Log.d(TAG, "session start: Direct activity launch initiated for $scheduleTitle")
        } catch (e: Exception) {
            Log.d(TAG, "session start: Direct launch constrained (${e.message}). High priority FullScreenIntent notification is active.")
            try {
                val pi = PendingIntent.getActivity(
                    context,
                    999,
                    fullScreenIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                pi.send()
            } catch (e2: Exception) {
                Log.w(TAG, "errors: PendingIntent send fallback error: ${e2.message}")
            }
        }
    }

    // ==========================================
    // REBOOT RECOVERY & MISSED ALARM RESTORATION
    // ==========================================

    fun recoverAndRescheduleAll(context: Context, reason: String) {
        Log.d(TAG, "reboot recovery: Initiated recovery pipeline. Reason = $reason")
        createNotificationChannel(context)

        val prefs = getPrefs(context)
        val now = System.currentTimeMillis()
        val schedules = getAllSchedules(context)

        for (schedule in schedules) {
            if (!schedule.enabled) {
                cancelAlarmForSchedule(context, schedule.id)
                continue
            }

            val savedNext = schedule.nextOccurrence
            val occurrenceIdForSavedNext = if (savedNext > 0L) getOccurrenceId(schedule.id, savedNext) else ""

            // Check if alarm was missed during shutdown / sleep
            if (savedNext in 1..(now - 60000L)) {
                val wasTriggered = prefs.getString("last_triggered_${schedule.id}", "") == occurrenceIdForSavedNext
                if (!wasTriggered) {
                    Log.w(TAG, "missed alarm recovery: Detected missed occurrence for schedule ID=${schedule.id} at ${formatTimestamp(savedNext)} (current time: ${formatTimestamp(now)})")
                    recordHistory(context, schedule.title, "MISSED")
                }
            }

            // Calculate next valid future occurrence
            val nextOccurrence = calculateNextOccurrence(schedule.hour, schedule.minute, now)
            prefs.edit().putLong("next_occurrence_${schedule.id}", nextOccurrence).apply()
            registerAlarmForSchedule(context, schedule.copy(nextOccurrence = nextOccurrence))
            Log.d(TAG, "reboot recovery: Successfully restored schedule ID=${schedule.id} (${schedule.title}) -> next trigger at ${formatTimestamp(nextOccurrence)}")
        }
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

        Log.d(TAG, "session start: sessionId=$sessionId, schedule=$scheduleTitle, duration=${durationMinutes}m, targetEndTime=${formatTimestamp(targetEndTime)}")

        getPrefs(context).edit()
            .putString(KEY_SESSION_STATE, MandatorySessionState.ACTIVE.name)
            .putString(KEY_CURRENT_SESSION_ID, sessionId)
            .putLong(KEY_SESSION_START_TIME, startTime)
            .putLong(KEY_SESSION_TARGET_END_TIME, targetEndTime)
            .putString(KEY_SESSION_SCHEDULE_ID, scheduleId)
            .putString(KEY_SESSION_SCHEDULE_TITLE, scheduleTitle)
            .putString(KEY_SESSION_CATEGORY, category)
            .putInt(KEY_SESSION_DURATION_MINS, durationMinutes)
            // Record unread status: assume unread until user scrolls/interacts with azkar
            .putBoolean(KEY_UNREAD_PENDING, true)
            .putString(KEY_UNREAD_SCHEDULE_ID, scheduleId)
            .putString(KEY_UNREAD_SCHEDULE_TITLE, scheduleTitle)
            .putString(KEY_UNREAD_CATEGORY, category)
            .putLong(KEY_UNREAD_TIMESTAMP, startTime)
            .putBoolean(KEY_UNREAD_REMINDER_SHOWN, false)
            .apply()

        showPersistentNotification(context, scheduleId, scheduleTitle, category, durationMinutes)
        return sessionId
    }

    /**
     * Called when the user has actively read/interacted with the Adhkar during the session.
     * Clears any pending unread reminder state so no pickup alert is shown.
     */
    fun markSessionAsRead(context: Context, scheduleId: String) {
        Log.d(TAG, "session marked as read by user interaction for schedule=$scheduleId")
        getPrefs(context).edit()
            .putBoolean(KEY_UNREAD_PENDING, false)
            .putBoolean(KEY_UNREAD_REMINDER_SHOWN, true)
            .apply()
        cancelUnreadReminderNotification(context)
    }

    fun completeSession(context: Context, scheduleId: String, scheduleTitle: String, wasRead: Boolean = true) {
        Log.d(TAG, "session completion: schedule ID=$scheduleId, title=$scheduleTitle, wasRead=$wasRead")
        setSessionState(context, MandatorySessionState.COMPLETED)
        cancelPersistentNotification(context)

        val today = getTodayDateString()
        val occurrenceId = getOccurrenceId(scheduleId, System.currentTimeMillis())
        val prefs = getPrefs(context)

        if (scheduleId == SCHEDULE_ID_MORNING) {
            prefs.edit().putString(KEY_MORNING_COMPLETED_DATE, today).apply()
        } else if (scheduleId == SCHEDULE_ID_EVENING) {
            prefs.edit().putString(KEY_EVENING_COMPLETED_DATE, today).apply()
        }
        val editor = prefs.edit()
            .putString("completed_${scheduleId}_$today", today)
            .putString("last_completed_$scheduleId", occurrenceId)

        if (wasRead) {
            editor.putBoolean(KEY_UNREAD_PENDING, false)
            editor.putBoolean(KEY_UNREAD_REMINDER_SHOWN, true)
        } else {
            // Keep unread pending flag so when user picks up the phone they get reminded
            editor.putBoolean(KEY_UNREAD_PENDING, true)
            editor.putString(KEY_UNREAD_SCHEDULE_ID, scheduleId)
            editor.putString(KEY_UNREAD_SCHEDULE_TITLE, scheduleTitle)
            editor.putBoolean(KEY_UNREAD_REMINDER_SHOWN, false)
        }
        editor.apply()

        if (wasRead) {
            cancelUnreadReminderNotification(context)
            updateStreakOnCompletion(context)
            recordHistory(context, scheduleTitle, "COMPLETED")
        } else {
            recordHistory(context, scheduleTitle, "AUTO_CLOSED_UNREAD")
        }

        // Ensure next occurrence is intact
        val allSchedules = getAllSchedules(context)
        val schedule = allSchedules.find { it.id == scheduleId }
        if (schedule != null && schedule.enabled) {
            registerAlarmForSchedule(context, schedule)
        }
    }

    fun cancelSession(context: Context, scheduleId: String, scheduleTitle: String) {
        Log.d(TAG, "session cancelled by user: scheduleId=$scheduleId, title=$scheduleTitle")
        setSessionState(context, MandatorySessionState.CANCELLED)
        cancelPersistentNotification(context)
        recordHistory(context, scheduleTitle, "CANCELLED")
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
            val updated = JSONArray()
            updated.put(obj)
            for (i in 0 until minOf(array.length(), 49)) {
                updated.put(array.get(i))
            }
            prefs.edit().putString(KEY_COMPLETION_HISTORY, updated.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "errors: Error recording history: ${e.message}")
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

        val selectedLanguage = com.example.data.local.AppLocalizer.getAppSelectedLanguage(context)

        val localizedTitle = when (scheduleId) {
            SCHEDULE_ID_MORNING -> when (selectedLanguage) {
                "Hausa" -> "Zikirin Safe na Wajibi"
                "Yoruba" -> "Zikiri Ọ̀sán Ti O Ṣe Kókó"
                "Igbo" -> "Ekpere Ụtụtụ nke Iwu"
                "Arabic" -> "أذكار الصباح الإلزامية"
                "French" -> "Adhkar du Matin Obligatoire"
                "Spanish" -> "Adhkar Matutino Obligatorio"
                "Urdu" -> "لازمی صبح کے اذکار"
                "Chinese" -> "早晨必念赞念"
                else -> scheduleTitle
            }
            SCHEDULE_ID_EVENING -> when (selectedLanguage) {
                "Hausa" -> "Zikirin Yamma na Wajibi"
                "Yoruba" -> "Zikiri Alẹ́ Ti O Ṣe Kókó"
                "Igbo" -> "Ekpere Mgbede nke Iwu"
                "Arabic" -> "أذكار المساء الإلزامية"
                "French" -> "Adhkar du Soir Obligatoire"
                "Spanish" -> "Adhkar Vespertino Obligatorio"
                "Urdu" -> "لازمی شام کے اذکار"
                "Chinese" -> "晚夕必念赞念"
                else -> scheduleTitle
            }
            else -> scheduleTitle
        }

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

        val notifTitle = when (selectedLanguage) {
            "Hausa" -> "✨ $localizedTitle Ya Fara"
            "Yoruba" -> "✨ $localizedTitle Ti Bẹ̀rẹ̀"
            "Igbo" -> "✨ $localizedTitle Ebidola"
            "Arabic" -> "✨ $localizedTitle قيد التشغيل"
            "French" -> "✨ $localizedTitle Actif"
            "Spanish" -> "✨ $localizedTitle Activo"
            "Urdu" -> "✨ $localizedTitle جاری ہے"
            "Chinese" -> "✨ $localizedTitle 进行中"
            else -> "✨ $localizedTitle Active"
        }

        val notifBody = when (selectedLanguage) {
            "Hausa" -> "Zaman karatun zikiri na minti $durationMinutes na gudana. Danna nan domin budewa da karantawa."
            "Yoruba" -> "Àkókò kíkà zikiri ti ìṣẹ́jú $durationMinutes ń lọ lọ́wọ́. Tẹ́ ibí láti ṣí i kí o sì kà á."
            "Igbo" -> "Oge ịgụ ekpere nke nkeji $durationMinutes na-aga n'ihu. Pịa ebe a ka imeghe ma gụọ."
            "Arabic" -> "جلسة الذكر المحددة بـ $durationMinutes دقائق قيد التشغيل. اضغط للفتح والقراءة."
            "French" -> "Votre séance d'Adhkar de $durationMinutes minutes est en cours. Appuyez pour ouvrir et réciter."
            "Spanish" -> "Tu sesión de Adhkar de $durationMinutes minutos está en curso. Toca para abrir y recitar."
            "Urdu" -> "آپ کے $durationMinutes منٹ کا ذکر کا سیشن جاری ہے۔ کھولنے اور پڑھنے کے لیے دبائیں۔"
            "Chinese" -> "您设定的 $durationMinutes 分钟必念赞念正在进行中。点击开启诵读。"
            else -> "Your scheduled $durationMinutes-minute Zikir session is in progress. Tap to open and recite."
        }

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
        Log.d(TAG, "notification cancelled: Removing active session notification ID=$NOTIF_ID_ACTIVE_SESSION")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIF_ID_ACTIVE_SESSION)
    }

    /**
     * Checks whether an auto-adhkar session ended without the user having read it,
     * and shows a top reminder notification as soon as the user picks up or unlocks their phone.
     */
    fun checkAndNotifyIfUnreadOnPickup(context: Context) {
        val prefs = getPrefs(context)
        val isUnreadPending = prefs.getBoolean(KEY_UNREAD_PENDING, false)
        val reminderAlreadyShown = prefs.getBoolean(KEY_UNREAD_REMINDER_SHOWN, false)

        Log.d(TAG, "checkAndNotifyIfUnreadOnPickup: isUnreadPending=$isUnreadPending, reminderAlreadyShown=$reminderAlreadyShown")

        if (isUnreadPending && !reminderAlreadyShown) {
            val scheduleId = prefs.getString(KEY_UNREAD_SCHEDULE_ID, SCHEDULE_ID_MORNING) ?: SCHEDULE_ID_MORNING
            val scheduleTitle = prefs.getString(KEY_UNREAD_SCHEDULE_TITLE, "Zikir") ?: "Zikir"
            val category = prefs.getString(KEY_UNREAD_CATEGORY, "Morning Adhkar") ?: "Morning Adhkar"
            val durationMinutes = prefs.getInt("duration_$scheduleId", 3)

            showUnreadReminderNotification(context, scheduleId, scheduleTitle, category, durationMinutes)
            prefs.edit().putBoolean(KEY_UNREAD_REMINDER_SHOWN, true).apply()
        }
    }

    fun showUnreadReminderNotification(
        context: Context,
        scheduleId: String,
        scheduleTitle: String,
        category: String,
        durationMinutes: Int
    ) {
        createNotificationChannel(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val selectedLanguage = com.example.data.local.AppLocalizer.getAppSelectedLanguage(context)

        val localizedTitle = when (scheduleId) {
            SCHEDULE_ID_MORNING -> when (selectedLanguage) {
                "Hausa" -> "Tunatarwar Zikirin Safe"
                "Yoruba" -> "Ìránlétí Zikiri Ọ̀sán"
                "Igbo" -> "Nchetara Ekpere Ụtụtụ"
                "Arabic" -> "تذكير بأذكار الصباح"
                "French" -> "Rappel des Adhkar du Matin"
                "Spanish" -> "Recordatorio de Adhkar Matutino"
                "Urdu" -> "صبح کے اذکار کی یاددہانی"
                "Chinese" -> "早晨赞念提醒"
                else -> "Morning Adhkar Reminder"
            }
            SCHEDULE_ID_EVENING -> when (selectedLanguage) {
                "Hausa" -> "Tunatarwar Zikirin Yamma"
                "Yoruba" -> "Ìránlétí Zikiri Irọlẹ"
                "Igbo" -> "Nchetara Ekpere Anyasị"
                "Arabic" -> "تذكير بأذكار المساء"
                "French" -> "Rappel des Adhkar du Soir"
                "Spanish" -> "Recordatorio de Adhkar Vespertino"
                "Urdu" -> "شام کے اذکار کی یاددہانی"
                "Chinese" -> "傍晚赞念提醒"
                else -> "Evening Adhkar Reminder"
            }
            else -> scheduleTitle
        }

        val notifBody = when (selectedLanguage) {
            "Hausa" -> "Ba a karanta zikirin ba a lokacin da ya bude. Danna nan domin karanta azkar dinka yanzu."
            "Yoruba" -> "A kò ka zikiri náà nígbà tí ó ṣí sílẹ̀. Tẹ́ ibí láti kà á ní báyìí."
            "Igbo" -> "A gụbeghị ekpere ahụ mgbe o mepere. Pịa ebe a ka ịgụọ ya ugbu a."
            "Arabic" -> "لم تتم قراءة الأذكار عندما فُتحت تلقائياً. اضغط هنا لقراءتها الآن ونيل الأجر."
            "French" -> "Les adhkar n'ont pas été lus. Appuyez ici pour réciter vos invocations maintenant."
            "Spanish" -> "No se recitaron los adhkar. Toca aquí para leerlos ahora y obtener la recompensa."
            "Urdu" -> "اذکار پڑھے نہیں گئے تھے۔ اپنے اذکار ابھی پڑھنے کے لیے یہاں دبائیں۔"
            "Chinese" -> "赞念此前未被诵读。点击此处立即开启诵读。"
            else -> "Your scheduled Adhkar was not read earlier. Tap here to read your Adhkar now."
        }

        val launchIntent = Intent(context, MandatoryAdhkarActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("SCHEDULE_ID", scheduleId)
            putExtra("SCHEDULE_TITLE", scheduleTitle)
            putExtra("CATEGORY", category)
            putExtra("DURATION_MINUTES", durationMinutes)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIF_ID_UNREAD_REMINDER,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_MANDATORY)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(localizedTitle)
            .setContentText(notifBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notifBody))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 300, 150, 300))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIF_ID_UNREAD_REMINDER, notification)
        Log.d(TAG, "showUnreadReminderNotification: Displayed unread reminder notification for $scheduleTitle")
    }

    fun cancelUnreadReminderNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIF_ID_UNREAD_REMINDER)
    }

    // ==========================================
    // TESTING & DIAGNOSTICS
    // ==========================================

    fun triggerTestNow(context: Context, scheduleId: String = SCHEDULE_ID_MORNING) {
        val schedules = getAllSchedules(context)
        val schedule = schedules.find { it.id == scheduleId } ?: schedules.firstOrNull() ?: MandatorySchedule(
            id = SCHEDULE_ID_MORNING,
            title = "Morning Zikir",
            category = "Morning Adhkar",
            hour = 6,
            minute = 0,
            durationMinutes = 3,
            enabled = true
        )

        Log.d(TAG, "alarm fired: Test alarm manually triggered for schedule=${schedule.id} (${schedule.title})")

        val intent = Intent(context, MandatoryAdhkarReceiver::class.java).apply {
            action = "com.example.ACTION_MANDATORY_ZIKIR_${schedule.id}"
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
