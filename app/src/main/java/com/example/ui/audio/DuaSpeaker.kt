package com.example.ui.audio

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class DuaSpeaker(private val context: Context) : TextToSpeech.OnInitListener {

    private val TAG = "DuaSpeaker"
    private var tts: TextToSpeech? = null
    
    private val _isArabicReady = MutableStateFlow(false)
    val isArabicReady: StateFlow<Boolean> = _isArabicReady

    private val _isPlaying = MutableStateFlow<Int?>(null) // Currently playing Dua ID
    val isPlaying: StateFlow<Int?> = _isPlaying

    private val _isPlayingTranslation = MutableStateFlow<Int?>(null) // Currently playing translation Dua ID
    val isPlayingTranslation: StateFlow<Int?> = _isPlayingTranslation

    private var pendingSpeechId: Int? = null
    private var pendingSpeechText: String? = null
    private var isInitialized = false

    init {
        initTts()
    }

    private fun initTts() {
        try {
            // First attempt to initialize with Google TTS engine if available for best Arabic pronunciation
            tts = TextToSpeech(context.applicationContext, this, "com.google.android.tts")
        } catch (e: Exception) {
            Log.w(TAG, "Failed initializing with Google TTS engine, falling back to default TTS", e)
            try {
                tts = TextToSpeech(context.applicationContext, this)
            } catch (ex: Exception) {
                Log.e(TAG, "Failed initializing default TTS", ex)
            }
        }
        setupProgressListener()
    }

    private fun setupProgressListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                val parts = utteranceId?.split("_")
                if (parts != null && parts.size == 2) {
                    val id = parts[1].toIntOrNull()
                    if (parts[0] == "arabic") {
                        _isPlaying.value = id
                    } else if (parts[0] == "translation") {
                        _isPlayingTranslation.value = id
                    }
                }
            }

            override fun onDone(utteranceId: String?) {
                _isPlaying.value = null
                _isPlayingTranslation.value = null
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isPlaying.value = null
                _isPlayingTranslation.value = null
            }
        })
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            try {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
                tts?.setAudioAttributes(audioAttributes)
            } catch (_: Exception) {}

            setupArabicLocale()
            
            // Execute pending speech if any
            val pendId = pendingSpeechId
            val pendText = pendingSpeechText
            if (pendId != null && pendText != null) {
                pendingSpeechId = null
                pendingSpeechText = null
                speakArabic(pendId, pendText)
            }
        } else {
            Log.e(TAG, "TTS Init failed with status: $status. Retrying with default engine...")
            // Fallback retry with default engine
            try {
                tts = TextToSpeech(context.applicationContext) { retryStatus ->
                    if (retryStatus == TextToSpeech.SUCCESS) {
                        isInitialized = true
                        setupArabicLocale()
                        val pendId = pendingSpeechId
                        val pendText = pendingSpeechText
                        if (pendId != null && pendText != null) {
                            pendingSpeechId = null
                            pendingSpeechText = null
                            speakArabic(pendId, pendText)
                        }
                    }
                }
                setupProgressListener()
            } catch (e: Exception) {
                Log.e(TAG, "Secondary TTS init failed", e)
            }
        }
    }

    private fun setupArabicLocale(): Boolean {
        val candidateLocales = listOf(
            Locale.forLanguageTag("ar-SA"),
            Locale("ar", "SA"),
            Locale.forLanguageTag("ar"),
            Locale("ar"),
            Locale("ara")
        )

        var languageSet = false
        for (loc in candidateLocales) {
            val result = tts?.setLanguage(loc)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                languageSet = true
                break
            }
        }

        if (!languageSet) {
            // Still attempt to set standard Arabic locale even if missing data warning
            tts?.setLanguage(Locale("ar"))
        }

        _isArabicReady.value = languageSet

        // Select the most authentic Native Arabic human voice (prioritizing high quality, local neural/natural models)
        try {
            val allVoices = tts?.voices ?: emptySet()
            val arabicVoices = allVoices.filter { 
                it.locale.language.equals("ar", ignoreCase = true) || it.locale.isO3Language.equals("ara", ignoreCase = true)
            }

            if (arabicVoices.isNotEmpty()) {
                val bestVoice = arabicVoices.maxByOrNull { voice ->
                    var score = 0
                    if (voice.quality >= android.speech.tts.Voice.QUALITY_HIGH) score += 20
                    if (voice.quality >= android.speech.tts.Voice.QUALITY_VERY_HIGH) score += 30
                    if (voice.locale.country.equals("SA", ignoreCase = true)) score += 15
                    if (!voice.isNetworkConnectionRequired) score += 10
                    val vName = voice.name.lowercase()
                    if (vName.contains("male") || vName.contains("ard") || vName.contains("arc")) score += 10
                    score
                } ?: arabicVoices.first()

                tts?.voice = bestVoice
            }
        } catch (_: Exception) {}

        return languageSet
    }

    /**
     * Clean Arabic text to ensure smooth, natural Quranic/Adhkar pronunciation without reading footnote numbers or brackets
     */
    private fun cleanArabicForPronunciation(text: String): String {
        return text
            .replace(Regex("\\[[^\\]]*\\]"), "") // Remove [Hadith references]
            .replace(Regex("\\([0-9٠-٩]+\\)"), "") // Remove (1) or (١) numbers
            .replace(Regex("[0-9٠-٩]+"), "") // Remove standalone digits
            .replace("•", " ")
            .replace("—", " ")
            .replace("-", " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    fun playArabic(id: Int, text: String, customRate: Float = 0.78f) {
        speakArabic(id, text, customRate)
    }

    fun speakArabic(id: Int, text: String, customRate: Float = 0.78f) {
        if (tts == null) {
            initTts()
        }

        if (!isInitialized) {
            pendingSpeechId = id
            pendingSpeechText = text
            return
        }

        stop()
        val isReady = setupArabicLocale()

        val cleanedText = cleanArabicForPronunciation(text)
        if (cleanedText.isBlank()) return

        // Rich, warm tone resembling authentic human Arab recitation
        tts?.setPitch(0.92f)
        // Gentle, dignified pace optimal for memorization and accurate pronunciation learning
        tts?.setSpeechRate(customRate)

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "arabic_$id")
        }

        val res = tts?.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, params, "arabic_$id")
        if (res == TextToSpeech.ERROR) {
            Log.e(TAG, "TTS speak failed. Attempting fallback setup...")
            setupArabicLocale()
            val retryRes = tts?.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, params, "arabic_$id")
            if (retryRes == TextToSpeech.ERROR && !isReady) {
                // Inform user if voice data needs installation on physical device
                try {
                    Toast.makeText(
                        context,
                        "Ana buƙatar kunna ko saukar da muryar Larabci (Arabic TTS) a Settings na wayarka.",
                        Toast.LENGTH_LONG
                    ).show()
                    val installIntent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(installIntent)
                } catch (_: Exception) {}
            }
        }
    }

    fun speakTranslation(id: Int, text: String, languageCode: String = "en") {
        if (tts == null) {
            initTts()
        }

        if (!isInitialized) {
            return
        }

        stop()
        val loc = when (languageCode.lowercase()) {
            "fr", "french" -> Locale.FRENCH
            "es", "spanish" -> Locale("es", "ES")
            "ar", "arabic" -> Locale("ar", "SA")
            "zh", "chinese" -> Locale.CHINESE
            else -> Locale.US
        }
        tts?.setLanguage(loc)
        tts?.setPitch(1.0f)
        tts?.setSpeechRate(0.92f)

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "translation_$id")
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "translation_$id")
    }

    fun stop() {
        tts?.stop()
        _isPlaying.value = null
        _isPlayingTranslation.value = null
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}

