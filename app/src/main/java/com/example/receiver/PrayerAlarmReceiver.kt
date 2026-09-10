package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.audio.AthanPlayer
import com.example.data.local.AppLocalizer
import com.example.util.PrayerTimeManager
import com.example.util.VibrationHelper

class PrayerAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "PrayerAlarmReceiver"
        const val CHANNEL_ID = "zakiru_prayer_athan_channel"
        const val CHANNEL_NAME = "Zakiru Muslim Prayer Times & Athan"
        const val ACTION_PRAYER_ALARM = "com.example.ACTION_PRAYER_ALARM"
        const val ACTION_PRE_PRAYER_VIBRATION = "com.example.ACTION_PRE_PRAYER_VIBRATION"
        const val ACTION_STOP_ATHAN = "com.example.ACTION_STOP_ATHAN"
        const val EXTRA_PRAYER_ID = "EXTRA_PRAYER_ID"
        const val EXTRA_NOTIFICATION_ID = "EXTRA_NOTIFICATION_ID"
        const val EXTRA_STOP_ATHAN = "EXTRA_STOP_ATHAN"

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val existing = notificationManager.getNotificationChannel(CHANNEL_ID)
                if (existing == null) {
                    val channel = NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        description = "Notifications and Athan alerts for mandatory Islamic daily prayers."
                        enableVibration(true)
                        vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
                        setBypassDnd(true)
                        lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                    }
                    notificationManager.createNotificationChannel(channel)
                }
            }
        }

        fun playAthanAudio(context: Context, onCompletion: (() -> Unit)? = null) {
            val modeStr = PrayerTimeManager.getAthanMode(context)
            val alertMode = AthanPlayer.AlertMode.fromId(modeStr)

            if (alertMode == AthanPlayer.AlertMode.SILENT || alertMode == AthanPlayer.AlertMode.VIBRATE_ONLY) {
                onCompletion?.invoke()
                return
            }

            val soundId = PrayerTimeManager.getAthanSound(context)
            val sound = AthanPlayer.AthanSound.fromId(soundId)
            AthanPlayer.playAthan(context, sound, onCompletion)
        }

        fun triggerVibration(context: Context) {
            try {
                val modeStr = PrayerTimeManager.getAthanMode(context)
                val alertMode = AthanPlayer.AlertMode.fromId(modeStr)
                if (alertMode == AthanPlayer.AlertMode.SOUND_ONLY || alertMode == AthanPlayer.AlertMode.SILENT) {
                    return
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    vibratorManager?.defaultVibrator?.vibrate(
                        VibrationEffect.createWaveform(longArrayOf(0, 600, 300, 600, 300, 800), -1)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 600, 300, 600, 300, 800), -1))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator?.vibrate(longArrayOf(0, 600, 300, 600, 300, 800), -1)
                    }
                }
            } catch (_: Exception) {}
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == ACTION_STOP_ATHAN) {
            Log.d(TAG, "ACTION_STOP_ATHAN received - stopping Athan playback immediately")
            AthanPlayer.stop()
            val notifId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
            if (notifId != 0) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                notificationManager?.cancel(notifId)
            }
            return
        }

        if (action == ACTION_PRE_PRAYER_VIBRATION) {
            val prayerId = intent.getStringExtra(EXTRA_PRAYER_ID) ?: "PRAYER"
            Log.d(TAG, "Pre-prayer 1-minute alert triggered for $prayerId. Giving 2 soft vibrations.")
            VibrationHelper.triggerTwoSoftVibrations(context)
            return
        }

        val prayerId = intent.getStringExtra(EXTRA_PRAYER_ID) ?: "PRAYER"
        Log.d(TAG, "Prayer alarm triggered for: $prayerId")

        createNotificationChannel(context)

        val selectedLanguage = AppLocalizer.getAppSelectedLanguage(context)

        val title = AppLocalizer.getPrayerNotificationTitle(prayerId, selectedLanguage)
        val message = AppLocalizer.getPrayerNotificationMessage(prayerId, selectedLanguage)
        val bigText = AppLocalizer.getPrayerNotificationBigText(prayerId, selectedLanguage)
        val stopButtonText = AppLocalizer.getStopAthanButtonLabel(selectedLanguage)
        val openButtonText = AppLocalizer.getOpenAppButtonLabel(selectedLanguage)

        // Trigger vibration according to selected alert mode
        triggerVibration(context)

        val notificationId = when (prayerId.uppercase()) {
            "FAJR" -> 3001
            "DHUHR" -> 3002
            "ASR" -> 3003
            "MAGHRIB" -> 3004
            "ISHA" -> 3005
            "SUNRISE" -> 3006
            else -> 3000
        }

        // 1. Launch App on click (and stop Athan)
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("OPEN_TAB", 0)
            putExtra(EXTRA_STOP_ATHAN, true)
            putExtra(EXTRA_PRAYER_ID, prayerId)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            prayerId.hashCode() + 101,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 2. Stop Athan PendingIntent (Broadcast to this receiver when dismissed or stop button clicked)
        val stopAthanIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            this.action = ACTION_STOP_ATHAN
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }
        val stopAthanPendingIntent = PendingIntent.getBroadcast(
            context,
            prayerId.hashCode() + 202,
            stopAthanIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .setDeleteIntent(stopAthanPendingIntent)
            .addAction(android.R.drawable.ic_lock_silent_mode, stopButtonText, stopAthanPendingIntent)
            .addAction(android.R.drawable.ic_menu_compass, openButtonText, contentPendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())

        // Play Athan based on configured sound and alert mode
        playAthanAudio(context)

        // Reschedule future prayer alarms
        PrayerTimeManager.reschedulePrayerAlarms(context)
    }
}
