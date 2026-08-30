package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.util.PrayerTimeManager
import java.util.Locale

class PrayerAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "PrayerAlarmReceiver"
        const val CHANNEL_ID = "zakiru_prayer_athan_channel"
        const val CHANNEL_NAME = "Zakiru Muslim Prayer Times & Athan"
        const val ACTION_PRAYER_ALARM = "com.example.ACTION_PRAYER_ALARM"
        const val EXTRA_PRAYER_ID = "EXTRA_PRAYER_ID"
        private var tts: TextToSpeech? = null

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

        fun playAthanAudio(context: Context) {
            val athanMode = PrayerTimeManager.getAthanMode(context)
            if (athanMode == "SILENT" || athanMode == "NOTIFICATION_ONLY") {
                return
            }

            try {
                // Initialize TTS for Arabic Athan Call
                tts = TextToSpeech(context.applicationContext) { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        tts?.language = Locale("ar")
                        val athanText = "الله أكبر الله أكبر. أشهد أن لا إله إلا الله. أشهد أن محمدا رسول الله. حي على الصلاة. حي على الفلاح. الله أكبر الله أكبر. لا إله إلا الله."
                        tts?.speak(athanText, TextToSpeech.QUEUE_FLUSH, null, "ATHAN_CALL")
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Fallback to system alarm tone: ${e.message}")
                try {
                    val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    val ringtone = RingtoneManager.getRingtone(context, alarmUri)
                    ringtone.play()
                } catch (_: Exception) {}
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prayerId = intent.getStringExtra(EXTRA_PRAYER_ID) ?: "PRAYER"
        Log.d(TAG, "Prayer alarm triggered for: $prayerId")

        createNotificationChannel(context)

        val prayerNameHausa = when (prayerId) {
            "FAJR" -> "Asuba"
            "DHUHR" -> "Azahar"
            "ASR" -> "La'asar"
            "MAGHRIB" -> "Magariba"
            "ISHA" -> "Isha'i"
            else -> "Sallah"
        }

        val prayerNameEn = when (prayerId) {
            "FAJR" -> "Fajr"
            "DHUHR" -> "Dhuhr"
            "ASR" -> "Asr"
            "MAGHRIB" -> "Maghrib"
            "ISHA" -> "Isha"
            else -> "Prayer"
        }

        val prayerNameAr = when (prayerId) {
            "FAJR" -> "صلاة الفجر"
            "DHUHR" -> "صلاة الظهر"
            "ASR" -> "صلاة العصر"
            "MAGHRIB" -> "صلاة المغرب"
            "ISHA" -> "صلاة العشاء"
            else -> "الصلاة"
        }

        val title = "🕌 Lokacin Sallar $prayerNameHausa ($prayerNameEn)"
        val message = "حي على الصلاة - An kira sallar $prayerNameHausa. Lokaci ya yi na samun dacewa da rahamar Ubangiji."

        // Vibration
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 300, 500), -1))
            } else {
                vibrator?.vibrate(longArrayOf(0, 500, 300, 500), -1)
            }
        } catch (_: Exception) {}

        // Launch App on click
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_TAB", 0)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            prayerId.hashCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationId = when (prayerId) {
            "FAJR" -> 3001
            "DHUHR" -> 3002
            "ASR" -> 3003
            "MAGHRIB" -> 3004
            "ISHA" -> 3005
            else -> 3000
        }

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$message\n\n$prayerNameAr\n\"إن الصلاة كانت على المؤمنين كتابا موقوتا\"")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())

        // Play Athan
        playAthanAudio(context)

        // Reschedule future prayer alarms
        PrayerTimeManager.reschedulePrayerAlarms(context)
    }
}
