package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

/**
 * Authentic Islamic Athan (Adhan) Audio Player Engine.
 * Plays genuine human voice recordings by renowned Mu'adhins:
 * 1. Makkah (Al-Masjid Al-Haram - Sheikh Ali Ahmed Mulla)
 * 2. Madinah (Al-Masjid An-Nabawi - Sheikh Essam Bukhari)
 * 3. Al-Aqsa (Jerusalem / Al-Quds)
 * 4. Classical Egyptian (Sheikh Abdul Basit Abdul Samad)
 * 5. Sheikh Mishary Rashid Alafasy
 * 6. Arabic Vocal Recitation (Full clear Arabic speech recital)
 * 7. Short Takbeer Call (Allahu Akbar Allahu Akbar)
 *
 * Features:
 * - Direct playback via Android MediaPlayer
 * - Automatic background caching to local storage for 100% offline availability
 * - Seamless fallback to high-clarity Arabic TTS / Alarm Ringtone if offline before cache
 */
object AthanPlayer {

    private const val TAG = "AthanPlayer"
    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = null
    private var playbackJob: Job? = null

    enum class AthanSound(
        val id: String,
        val displayNameEn: String,
        val displayNameHa: String,
        val description: String,
        val audioUrl: String
    ) {
        MAKKAH(
            id = "MAKKAH",
            displayNameEn = "Makkah (Al-Haram - Ali Mulla)",
            displayNameHa = "Makkah (Ka'aba - Ali Mulla)",
            description = "Authentic call from Al-Masjid Al-Haram in Makkah",
            audioUrl = "https://media.sd.ma/assabile/adhan_3748/001.mp3"
        ),
        MADINAH(
            id = "MADINAH",
            displayNameEn = "Madinah (Al-Nabawi - Essam Bukhari)",
            displayNameHa = "Madinah (Masallacin Annabi)",
            description = "Soulful authentic call from the Prophet's Mosque in Madinah",
            audioUrl = "https://ia801406.us.archive.org/34/items/AdhanMadinah/AdhanMadinah.mp3"
        ),
        AL_AQSA(
            id = "AL_AQSA",
            displayNameEn = "Al-Aqsa (Jerusalem / Quds)",
            displayNameHa = "Al-Kudus (Masallacin Al-Aqsa)",
            description = "Reverberant historical call from Al-Aqsa Mosque",
            audioUrl = "https://ia801503.us.archive.org/15/items/AdhanAlAqsa/AdhanAlAqsa.mp3"
        ),
        EGYPT(
            id = "EGYPT",
            displayNameEn = "Egypt (Sheikh Abdul Basit)",
            displayNameHa = "Salon Masar (Abdul Basit)",
            description = "Warm classical Egyptian recitation by Sheikh Abdul Basit",
            audioUrl = "https://ia800302.us.archive.org/10/items/AdhanEgypt/AdhanEgypt.mp3"
        ),
        MISHARY(
            id = "MISHARY",
            displayNameEn = "Sheikh Mishary Alafasy",
            displayNameHa = "Mishary Rashid Alafasy",
            description = "Melodic authentic Athan by Sheikh Mishary Alafasy",
            audioUrl = "https://ia800701.us.archive.org/22/items/AthanMishary/AthanMishary.mp3"
        ),
        ARABIC_TTS(
            id = "ARABIC_TTS",
            displayNameEn = "Arabic Voice Recitation",
            displayNameHa = "Karatun Larabci Kai Tsaye",
            description = "Full authentic Arabic recitation text (Offline Voice)",
            audioUrl = ""
        ),
        SOFT_TAKBEER(
            id = "SOFT_TAKBEER",
            displayNameEn = "Short Takbeer Alert",
            displayNameHa = "Gajeren Takbira (Takbeer)",
            description = "Short authentic Takbeer call (Allahu Akbar)",
            audioUrl = "https://ia800203.us.archive.org/24/items/AdhanMakkah/AdhanTakbeerShort.mp3"
        );

        companion object {
            fun fromId(id: String): AthanSound {
                return entries.find { it.id.equals(id, ignoreCase = true) } ?: MAKKAH
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
        playbackJob?.cancel()
        playbackJob = null

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

        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
        } catch (_: Exception) {}
    }

    fun isPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying == true || tts?.isSpeaking == true
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

        if (sound == AthanSound.ARABIC_TTS || sound.audioUrl.isBlank()) {
            playArabicTts(context, onCompletion)
            return
        }

        val cacheFile = File(context.filesDir, "athan_${sound.id.lowercase()}.mp3")

        playbackJob = CoroutineScope(Dispatchers.IO).launch {
            if (cacheFile.exists() && cacheFile.length() > 5000) {
                // Play directly from offline cached genuine audio file
                playLocalAudioFile(context, cacheFile, onCompletion)
            } else {
                // Stream directly and save in background for offline use
                val streamed = playFromNetworkStream(context, sound.audioUrl, onCompletion)
                if (!streamed) {
                    // Fallback to Arabic voice recitation
                    withContext(Dispatchers.Main) {
                        playArabicTts(context, onCompletion)
                    }
                }
                // Background download & cache for next time
                tryDownloadAndCache(sound.audioUrl, cacheFile)
            }
        }
    }

    private suspend fun playLocalAudioFile(
        context: Context,
        file: File,
        onCompletion: (() -> Unit)?
    ) = withContext(Dispatchers.Main) {
        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setLegacyStreamType(AudioManager.STREAM_ALARM)
                        .build()
                )
                setDataSource(context, Uri.fromFile(file))
                setOnPreparedListener { mp ->
                    mp.start()
                }
                setOnCompletionListener {
                    stop()
                    onCompletion?.invoke()
                }
                setOnErrorListener { _, _, _ ->
                    stop()
                    playArabicTts(context, onCompletion)
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed playing local Athan file: ${e.message}")
            playArabicTts(context, onCompletion)
        }
    }

    private suspend fun playFromNetworkStream(
        context: Context,
        urlStr: String,
        onCompletion: (() -> Unit)?
    ): Boolean = withContext(Dispatchers.Main) {
        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setLegacyStreamType(AudioManager.STREAM_ALARM)
                        .build()
                )
                setDataSource(urlStr)
                setOnPreparedListener { mp ->
                    mp.start()
                }
                setOnCompletionListener {
                    stop()
                    onCompletion?.invoke()
                }
                setOnErrorListener { _, what, extra ->
                    Log.w(TAG, "MediaPlayer streaming error what=$what extra=$extra")
                    stop()
                    playArabicTts(context, onCompletion)
                    true
                }
                prepareAsync()
            }
            true
        } catch (e: Exception) {
            Log.w(TAG, "Network stream failed: ${e.message}")
            false
        }
    }

    private fun tryDownloadAndCache(urlStr: String, destinationFile: File) {
        try {
            if (destinationFile.exists() && destinationFile.length() > 5000) return
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 8000
            connection.readTimeout = 15000
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val tempFile = File(destinationFile.parentFile, "${destinationFile.name}.tmp")
                connection.inputStream.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }
                if (tempFile.length() > 5000) {
                    tempFile.renameTo(destinationFile)
                    Log.d(TAG, "Successfully cached authentic Athan: ${destinationFile.name}")
                } else {
                    tempFile.delete()
                }
            }
            connection.disconnect()
        } catch (e: Exception) {
            Log.w(TAG, "Could not cache Athan file: ${e.message}")
        }
    }

    private fun playArabicTts(context: Context, onCompletion: (() -> Unit)?) {
        try {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val langResult = tts?.setLanguage(Locale("ar"))
                    if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts?.setLanguage(Locale.ENGLISH)
                    }
                    val athanText = "اللهُ أَكْبَرُ، اللهُ أَكْبَرُ. اللهُ أَكْبَرُ، اللهُ أَكْبَرُ. أَشْهَدُ أَنْ لَا إِلٰهَ إِلَّا اللهُ. أَشْهَدُ أَنْ لَا إِلٰهَ إِلَّا اللهُ. أَشْهَدُ أَنَّ مُحَمَّدًا رَسُولُ اللهِ. أَشْهَدُ أَنَّ مُحَمَّدًا رَسُولُ اللهِ. حَيَّ عَلَى الصَّلَاةِ. حَيَّ عَلَى الصَّلَاةِ. حَيَّ عَلَى الْفَلَاحِ. حَيَّ عَلَى الْفَلَاحِ. اللهُ أَكْبَرُ، اللهُ أَكْبَرُ. لَا إِلٰهَ إِلَّا اللهُ."
                    
                    tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {}
                        override fun onDone(utteranceId: String?) {
                            onCompletion?.invoke()
                        }
                        override fun onError(utteranceId: String?) {
                            onCompletion?.invoke()
                        }
                    })

                    tts?.speak(athanText, TextToSpeech.QUEUE_FLUSH, null, "ATHAN_PLAYBACK")
                } else {
                    playRingtoneFallback(context)
                    onCompletion?.invoke()
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "TTS failed, fallback to Ringtone: ${e.message}")
            playRingtoneFallback(context)
            onCompletion?.invoke()
        }
    }

    private fun playRingtoneFallback(context: Context) {
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, alarmUri)
            ringtone.play()
        } catch (_: Exception) {}
    }
}
