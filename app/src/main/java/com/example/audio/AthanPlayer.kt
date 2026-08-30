package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin

/**
 * High quality Athan sound synthesis and playback engine for Islamic Prayer Times.
 * Supports multiple authentic Athan sound styles:
 * 1. Makkah (Al-Masjid Al-Haram - Beautiful Hijaz Maqam melodic call)
 * 2. Madinah (Al-Masjid An-Nabawi - Serene, deep tonal call)
 * 3. Al-Aqsa (Quds - Resonant and soulful call)
 * 4. Classical Egyptian (Traditional warm harmonic call)
 * 5. Text-To-Speech Arabic Recitation (Full clear Arabic phrase recital)
 * 6. Gentle Soft Takbeer (Soft chime & Takbeer tone)
 */
object AthanPlayer {

    private const val TAG = "AthanPlayer"
    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = null
    private var playbackJob: Job? = null

    enum class AthanSound(val id: String, val displayNameEn: String, val displayNameHa: String, val description: String) {
        MAKKAH("MAKKAH", "Makkah (Al-Haram)", "Makkah (Ka'aba)", "Harmonic Hijaz Maqam with resonant echo"),
        MADINAH("MADINAH", "Madinah (Al-Nabawi)", "Madinah (Masallacin Annabi)", "Calm, deep soulful melodic resonance"),
        AL_AQSA("AL_AQSA", "Al-Aqsa (Jerusalem)", "Al-Kudus (Masallacin Al-Aqsa)", "Clear reverberant traditional calling tone"),
        EGYPT("EGYPT", "Classical Egyptian", "Salon Masar (Masar)", "Warm harmonic vocal-like timbre"),
        ARABIC_TTS("ARABIC_TTS", "Arabic Vocal Recitation", "Karatun Larabci Kai Tsaye", "Full authentic Arabic recitation text"),
        SOFT_TAKBEER("SOFT_TAKBEER", "Gentle Takbeer Chime", "Natsuwar Takbira Mai Taushi", "Gentle acoustic bell & spiritual melody");

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
            if (mediaPlayer != null) {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.stop()
                }
                mediaPlayer?.release()
                mediaPlayer = null
            }
        } catch (_: Exception) {}

        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
        } catch (_: Exception) {}
    }

    fun playAthan(
        context: Context,
        sound: AthanSound,
        onCompletion: (() -> Unit)? = null
    ) {
        stop()

        if (sound == AthanSound.ARABIC_TTS) {
            playArabicTts(context, onCompletion)
            return
        }

        playbackJob = CoroutineScope(Dispatchers.Default).launch {
            try {
                playSynthesizedAthanMelody(context, sound)
                launch(Dispatchers.Main) {
                    onCompletion?.invoke()
                }
            } catch (e: Exception) {
                Log.w(TAG, "Synthesizer fallback due to: ${e.message}")
                launch(Dispatchers.Main) {
                    playRingtoneFallback(context)
                    onCompletion?.invoke()
                }
            }
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
                    val athanText = "الله أكبر الله أكبر. أشهد أن لا إله إلا الله. أشهد أن محمدا رسول الله. حي على الصلاة. حي على الفلاح. قد قامت الصلاة. الله أكبر الله أكبر. لا إله إلا الله."
                    tts?.speak(athanText, TextToSpeech.QUEUE_FLUSH, null, "ATHAN_PLAYBACK")
                } else {
                    playRingtoneFallback(context)
                }
                onCompletion?.invoke()
            }
        } catch (e: Exception) {
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

    /**
     * Synthesizes resonant acoustic tones with harmonics & vibrato matching Islamic Maqamat
     */
    private fun playSynthesizedAthanMelody(context: Context, sound: AthanSound) {
        val sampleRate = 44100
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val audioFormat = AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()

        val track = AudioTrack.Builder()
            .setAudioAttributes(audioAttributes)
            .setAudioFormat(audioFormat)
            .setBufferSizeInBytes(bufferSize * 4)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        track.play()

        // Notes and durations in ms tailored to each Athan style
        // Maqam Bayati / Hijaz intervals
        val melodyNotes: List<Pair<Double, Int>> = when (sound) {
            AthanSound.MAKKAH -> listOf(
                // "Allahu Akbar, Allahu Akbar"
                Pair(220.0, 900),   // A3
                Pair(277.18, 1400), // C#4 (Hijaz tone)
                Pair(293.66, 1200), // D4
                Pair(277.18, 1800), // C#4
                Pair(220.0, 2200),  // A3
                Pair(0.0, 400),     // pause
                Pair(277.18, 1000), // C#4
                Pair(329.63, 1500), // E4
                Pair(293.66, 1400), // D4
                Pair(277.18, 2000), // C#4
                Pair(220.0, 2500)   // A3
            )
            AthanSound.MADINAH -> listOf(
                // Deep, serene and elongated
                Pair(196.0, 1200),  // G3
                Pair(246.94, 1600), // B3
                Pair(261.63, 1400), // C4
                Pair(293.66, 2000), // D4
                Pair(246.94, 1800), // B3
                Pair(196.0, 2600),  // G3
                Pair(0.0, 500),     // pause
                Pair(220.0, 1400),  // A3
                Pair(261.63, 1600), // C4
                Pair(246.94, 2200), // B3
                Pair(196.0, 3000)   // G3
            )
            AthanSound.AL_AQSA -> listOf(
                // Resonant and clear
                Pair(261.63, 1000), // C4
                Pair(329.63, 1300), // E4
                Pair(349.23, 1500), // F4
                Pair(392.00, 1800), // G4
                Pair(329.63, 1600), // E4
                Pair(261.63, 2400), // C4
                Pair(0.0, 400),
                Pair(293.66, 1200), // D4
                Pair(349.23, 1600), // F4
                Pair(329.63, 2200), // E4
                Pair(261.63, 2800)  // C4
            )
            AthanSound.EGYPT -> listOf(
                // Warm, classical Bayati tone
                Pair(220.0, 1000),  // A3
                Pair(246.94, 1200), // B3
                Pair(261.63, 1600), // C4
                Pair(293.66, 1600), // D4
                Pair(261.63, 1400), // C4
                Pair(220.0, 2200),  // A3
                Pair(0.0, 400),
                Pair(261.63, 1300), // C4
                Pair(293.66, 1500), // D4
                Pair(329.63, 1800), // E4
                Pair(261.63, 2000), // C4
                Pair(220.0, 2600)   // A3
            )
            AthanSound.SOFT_TAKBEER -> listOf(
                // Soft chime bells
                Pair(523.25, 1200), // C5
                Pair(659.25, 1400), // E5
                Pair(783.99, 1800), // G5
                Pair(1046.5, 2400), // C6
                Pair(0.0, 500),
                Pair(783.99, 1400), // G5
                Pair(659.25, 1800), // E5
                Pair(523.25, 2800)  // C5
            )
            AthanSound.ARABIC_TTS -> emptyList()
        }

        try {
            for ((freq, durMs) in melodyNotes) {
                if (playbackJob?.isActive != true) break

                if (freq <= 0.0) {
                    val silenceSamples = (sampleRate * (durMs / 1000.0)).toInt()
                    val silenceBuffer = ShortArray(silenceSamples)
                    track.write(silenceBuffer, 0, silenceSamples)
                    continue
                }

                val totalSamples = (sampleRate * (durMs / 1000.0)).toInt()
                val audioBuffer = ShortArray(totalSamples)
                val attackSamples = (sampleRate * 0.08).toInt()
                val releaseSamples = (sampleRate * 0.25).toInt()

                var phase = 0.0
                val phaseIncrement = 2.0 * PI * freq / sampleRate
                val vibratoRate = 4.5 // Hz
                val vibratoDepth = 0.015

                for (i in 0 until totalSamples) {
                    // Vibrato calculation
                    val vibrato = 1.0 + vibratoDepth * sin(2.0 * PI * vibratoRate * (i.toDouble() / sampleRate))
                    val currentInc = phaseIncrement * vibrato

                    // Natural acoustic organ / vocal timbre synthesis: Fundamental + 2nd + 3rd + 4th Harmonics
                    var sampleVal = 0.55 * sin(phase) +
                            0.25 * sin(2.0 * phase) +
                            0.12 * sin(3.0 * phase) +
                            0.08 * sin(4.0 * phase)

                    phase += currentInc
                    if (phase >= 2.0 * PI) phase -= 2.0 * PI

                    // Smooth envelope ADSR
                    val envelope = when {
                        i < attackSamples -> i.toDouble() / attackSamples
                        i > totalSamples - releaseSamples -> (totalSamples - i).toDouble() / releaseSamples
                        else -> 1.0
                    }

                    val finalSample = (sampleVal * envelope * Short.MAX_VALUE * 0.85).toInt()
                    audioBuffer[i] = finalSample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                track.write(audioBuffer, 0, totalSamples)
            }
        } catch (_: Exception) {
        } finally {
            try {
                track.stop()
                track.release()
            } catch (_: Exception) {}
        }
    }
}
