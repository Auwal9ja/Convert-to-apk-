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

class PrayerAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "PrayerAlarmReceiver"
        const val CHANNEL_ID = "zakiru_prayer_athan_channel"
        const val CHANNEL_NAME = "Zakiru Muslim Prayer Times & Athan"
        const val ACTION_PRAYER_ALARM = "com.example.ACTION_PRAYER_ALARM"
        const val EXTRA_PRAYER_ID = "EXTRA_PRAYER_ID"

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
        val prayerId = intent.getStringExtra(EXTRA_PRAYER_ID) ?: "PRAYER"
        Log.d(TAG, "Prayer alarm triggered for: $prayerId")

        createNotificationChannel(context)

        val sharedPref = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val selectedLanguage = sharedPref.getString("selected_language", "English") ?: "English"

        val prayerName = when (prayerId) {
            "FAJR" -> when (selectedLanguage) {
                "Hausa" -> "Asuba"
                "Yoruba" -> "Fajr (Àfẹ̀mọ́jú)"
                "Igbo" -> "Fajr (Ụtụtụ)"
                "Arabic" -> "الفجر"
                "French" -> "Fajr (Aube)"
                "Spanish" -> "Fajr (Amanecer)"
                "Urdu" -> "فجر"
                "Chinese" -> "晨礼 (Fajr)"
                else -> "Fajr"
            }
            "DHUHR" -> when (selectedLanguage) {
                "Hausa" -> "Azahar"
                "Yoruba" -> "Dhuhr (Ọ̀sán)"
                "Igbo" -> "Dhuhr (Ehihie)"
                "Arabic" -> "الظهر"
                "French" -> "Dhuhr (Midi)"
                "Spanish" -> "Dhuhr (Mediodía)"
                "Urdu" -> "ظہر"
                "Chinese" -> "晌礼 (Dhuhr)"
                else -> "Dhuhr"
            }
            "ASR" -> when (selectedLanguage) {
                "Hausa" -> "La'asar"
                "Yoruba" -> "Asr (Ìrọ̀lẹ́)"
                "Igbo" -> "Asr (Mgbede Mbụ)"
                "Arabic" -> "العصر"
                "French" -> "Asr (Après-midi)"
                "Spanish" -> "Asr (Tarde)"
                "Urdu" -> "عصر"
                "Chinese" -> "晡礼 (Asr)"
                else -> "Asr"
            }
            "MAGHRIB" -> when (selectedLanguage) {
                "Hausa" -> "Magariba"
                "Yoruba" -> "Maghrib (Wọ̀rọ̀)"
                "Igbo" -> "Maghrib (Mgbede Ọdịda Anyanwụ)"
                "Arabic" -> "المغرب"
                "French" -> "Maghrib (Coucher du soleil)"
                "Spanish" -> "Maghrib (Ocaso)"
                "Urdu" -> "مغرب"
                "Chinese" -> "昏礼 (Maghrib)"
                else -> "Maghrib"
            }
            "ISHA" -> when (selectedLanguage) {
                "Hausa" -> "Isha'i"
                "Yoruba" -> "Isha (Alẹ́)"
                "Igbo" -> "Isha (Abalị)"
                "Arabic" -> "العشاء"
                "French" -> "Isha (Nuit)"
                "Spanish" -> "Isha (Noche)"
                "Urdu" -> "عشاء"
                "Chinese" -> "宵礼 (Isha)"
                else -> "Isha"
            }
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

        val title = when (selectedLanguage) {
            "Hausa" -> "🕌 Lokacin Sallar $prayerName"
            "Yoruba" -> "🕌 Àkókò Àdúrà $prayerName"
            "Igbo" -> "🕌 Oge Ekpere $prayerName"
            "Arabic" -> "🕌 حان وقت $prayerNameAr"
            "French" -> "🕌 Heure de la prière de $prayerName"
            "Spanish" -> "🕌 Hora de la oración de $prayerName"
            "Urdu" -> "🕌 نماز $prayerName کا وقت ہو گیا"
            "Chinese" -> "🕌 $prayerName 祈祷时间已到"
            else -> "🕌 Time for $prayerName Prayer"
        }

        val message = when (selectedLanguage) {
            "Hausa" -> "حي على الصلاة - An kira sallar $prayerName. Lokaci ya yi na samun dacewa da rahamar Ubangiji."
            "Yoruba" -> "حي على الصلاة - A ti pe àsìkò àdúrà $prayerName. Ẹ jẹ́ ká gbàdúrà."
            "Igbo" -> "حي على الصلاة - Oge ekpere $prayerName eruola. Kpee ekpere."
            "Arabic" -> "حي على الصلاة، حي على الفلاح - أقيمت صلاة $prayerNameAr."
            "French" -> "حي على الصلاة - C'est l'heure de la prière de $prayerName. Venez à la prière."
            "Spanish" -> "حي على الصلاة - Es la hora de la oración de $prayerName. Acude a la oración."
            "Urdu" -> "حي على الصلاة - نماز $prayerName کا وقت شروع ہو چکا ہے۔"
            "Chinese" -> "حي على الصلاة - $prayerName 祈祷时间已到，请准备礼拜。"
            else -> "حي على الصلاة - The time for $prayerName prayer has arrived. Come to prayer."
        }

        // Trigger vibration according to selected alert mode
        triggerVibration(context)

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

        // Play Athan based on configured sound and alert mode
        playAthanAudio(context)

        // Reschedule future prayer alarms
        PrayerTimeManager.reschedulePrayerAlarms(context)
    }
}
