package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.util.Log
import com.example.R

/**
 * Authentic Islamic Athan (Adhan) Audio Player Engine.
 * Plays genuine, locally bundled authentic Islamic Athan and Takbeer recordings:
 * 1. Makkah (Al-Masjid Al-Haram - Ali Ahmed Mulla style)
 * 2. Madinah (Al-Masjid An-Nabawi - Essam Bukhari style)
 * 3. Al-Aqsa (Jerusalem / Al-Quds)
 * 4. Classical Egyptian (Sheikh Abdul Basit style)
 * 5. Sheikh Mishary Rashid Alafasy style
 * 6. Short Takbeer Call (Allahu Akbar Allahu Akbar)
 *
 * 100% Offline, Instant Playback, Zero Network Dependency.
 */
object AthanPlayer {

    private const val TAG = "AthanPlayer"
    private var mediaPlayer: MediaPlayer? = null

    enum class AthanSound(
        val id: String,
        val displayNameEn: String,
        val displayNameHa: String,
        val descriptionEn: String,
        val descriptionHa: String,
        val rawResId: Int
    ) {
        SOFT_TAKBEER(
            id = "SOFT_TAKBEER",
            displayNameEn = "Short Takbeer (Allahu Akbar x1)",
            displayNameHa = "Allahu Akbar (Sau Ɗaya Kawai)",
            descriptionEn = "Authentic short Takbeer (Allahu Akbar, Allahu Akbar) once",
            descriptionHa = "Gajeren kabbara na gaskiya (Allahu Akbar, Allahu Akbar) sau ɗaya",
            rawResId = R.raw.athan_takbeer
        ),
        MAKKAH(
            id = "MAKKAH",
            displayNameEn = "Makkah (Al-Masjid Al-Haram)",
            displayNameHa = "Makkah (Ka'aba - Ali Mulla)",
            descriptionEn = "Full authentic Call to Prayer by Sheikh Ali Ahmed Mulla (Al-Haram)",
            descriptionHa = "Cikakken kiran sallah na Sheikh Ali Ahmed Mulla",
            rawResId = R.raw.athan_makkah
        ),
        MADINAH(
            id = "MADINAH",
            displayNameEn = "Madinah (Al-Masjid An-Nabawi)",
            displayNameHa = "Madinah (Masallacin Annabi)",
            descriptionEn = "Full authentic Call to Prayer from the Prophet's Mosque in Madinah",
            descriptionHa = "Cikakken kiran sallah na Masallacin Annabi a Madinah",
            rawResId = R.raw.athan_madinah
        ),
        AL_AQSA(
            id = "AL_AQSA",
            displayNameEn = "Al-Aqsa (Jerusalem / Quds)",
            displayNameHa = "Al-Kudus (Masallacin Al-Aqsa)",
            descriptionEn = "Full authentic historical Call to Prayer from Al-Masjid Al-Aqsa",
            descriptionHa = "Cikakken kiran sallah na Masallacin Al-Aqsa",
            rawResId = R.raw.athan_alaqsa
        ),
        EGYPT(
            id = "EGYPT",
            displayNameEn = "Egypt (Sheikh Muhammad Rifaat)",
            displayNameHa = "Masar (Sheikh Muhammad Rifaat)",
            descriptionEn = "Full classical Egyptian Call to Prayer by Sheikh Muhammad Rifaat",
            descriptionHa = "Cikakken kiran sallah na Sheikh Muhammad Rifaat",
            rawResId = R.raw.athan_egypt
        ),
        MISHARY(
            id = "MISHARY",
            displayNameEn = "Sheikh Mishary Rashid Alafasy",
            displayNameHa = "Sheikh Mishary Rashid Alafasy",
            descriptionEn = "Full authentic melodic Call to Prayer by Sheikh Mishary Alafasy",
            descriptionHa = "Cikakken kiran sallah na Sheikh Mishary Alafasy",
            rawResId = R.raw.athan_mishary
        ),
        ARABIC_TTS(
            id = "ARABIC_TTS",
            displayNameEn = "Short Takbeer Call",
            displayNameHa = "Takbira (Allahu Akbar)",
            descriptionEn = "Short Takbeer call alert (Allahu Akbar)",
            descriptionHa = "Allahu Akbar Allahu Akbar sau ɗaya",
            rawResId = R.raw.athan_takbeer
        );

        fun getDisplayName(language: String): String = if (language == "Hausa") displayNameHa else displayNameEn
        fun getDescription(language: String): String = if (language == "Hausa") descriptionHa else descriptionEn

        companion object {
            fun fromId(id: String): AthanSound {
                return entries.find { it.id.equals(id, ignoreCase = true) } ?: SOFT_TAKBEER
            }
        }
    }

    enum class AlertMode(val id: String, val titleEn: String, val titleHa: String) {
        SOUND_AND_VIBRATE("SOUND_AND_VIBRATE", "Sound + Vibration", "Sauti + Girgiza (Vibrate)"),
        SOUND_ONLY("SOUND_ONLY", "Sound Only", "Sauti Kawai"),
        VIBRATE_ONLY("VIBRATE_ONLY", "Vibration Only", "Girgiza Kawai (Vibrate)"),
        SILENT("SILENT", "Silent (Notification Only)", "Shiru (Sanarwa Kawai)");

        companion object {
            fun fromId(id: String): AlertMode {
                return entries.find { it.id.equals(id, ignoreCase = true) } ?: SOUND_AND_VIBRATE
            }
        }
    }

    fun stop() {
        try {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) {
                    mp.stop()
                }
                mp.reset()
                mp.release()
            }
            mediaPlayer = null
        } catch (_: Exception) {}
    }

    fun isPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying == true
        } catch (_: Exception) {
            false
        }
    }

    fun playAthan(
        context: Context,
        sound: AthanSound,
        onCompletion: (() -> Unit)? = null
    ) {
        stop()

        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            // If music stream is at 0, nudge it so it is clearly audible
            audioManager?.let { am ->
                val maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val currentVol = am.getStreamVolume(AudioManager.STREAM_MUSIC)
                if (currentVol == 0 && maxVol > 0) {
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVol * 0.75f).toInt(), 0)
                }
            }

            val player = MediaPlayer.create(context, sound.rawResId).apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                        .build()
                )
                setVolume(1.0f, 1.0f)
                setOnCompletionListener {
                    stop()
                    onCompletion?.invoke()
                }
                setOnErrorListener { _, what, extra ->
                    Log.w(TAG, "MediaPlayer error what=$what extra=$extra, playing fallback ringtone")
                    stop()
                    playRingtoneFallback(context)
                    onCompletion?.invoke()
                    true
                }
            }

            mediaPlayer = player
            player.start()
            Log.d(TAG, "Successfully started Athan playback for ${sound.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Error initiating Athan audio: ${e.message}", e)
            playRingtoneFallback(context)
            onCompletion?.invoke()
        }
    }

    private fun playRingtoneFallback(context: Context) {
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val ringtone = RingtoneManager.getRingtone(context, alarmUri)
            ringtone?.play()
        } catch (_: Exception) {}
    }
}
