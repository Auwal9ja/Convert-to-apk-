package com.example.ui.audio

import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class DuaSpeaker(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    
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
            setupArabicLocale()
            
            // Execute pending speech if any
            val pendId = pendingSpeechId
            val pendText = pendingSpeechText
            if (pendId != null && pendText != null) {
                pendingSpeechId = null
                pendingSpeechText = null
                speakArabic(pendId, pendText)
            }
        }
    }

    private fun setupArabicLocale(): Boolean {
        val saudiArabic = Locale("ar", "SA")
        val generalArabic = Locale("ar")
        
        var result = tts?.setLanguage(saudiArabic)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            result = tts?.setLanguage(generalArabic)
        }

        val isReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
        _isArabicReady.value = isReady

        // Select the most authentic Native Arabic human voice (prioritizing high quality, local neural/natural models)
        try {
            val allVoices = tts?.voices ?: emptySet()
            val arabicVoices = allVoices.filter { 
                it.locale.language.equals("ar", ignoreCase = true) 
            }

            if (arabicVoices.isNotEmpty()) {
                // Prioritize male/qari-style natural Arabic native voices, high quality, and non-network required first
                val bestVoice = arabicVoices.maxByOrNull { voice ->
                    var score = 0
                    if (voice.quality >= android.speech.tts.Voice.QUALITY_HIGH) score += 20
                    if (voice.quality >= android.speech.tts.Voice.QUALITY_VERY_HIGH) score += 30
                    if (voice.locale.country.equals("SA", ignoreCase = true)) score += 15
                    if (!voice.isNetworkConnectionRequired) score += 10
                    // Bonus for natural/reciter-style voices
                    val vName = voice.name.lowercase()
                    if (vName.contains("male") || vName.contains("ard") || vName.contains("arc")) score += 10
                    score
                } ?: arabicVoices.first()

                tts?.voice = bestVoice
            }
        } catch (_: Exception) {}

        return isReady
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

    fun speakArabic(id: Int, text: String, customRate: Float = 0.78f) {
        if (tts == null) {
            tts = TextToSpeech(context.applicationContext, this)
        }

        if (!isInitialized) {
            pendingSpeechId = id
            pendingSpeechText = text
            return
        }

        stop()
        setupArabicLocale()

        val cleanedText = cleanArabicForPronunciation(text)
        if (cleanedText.isBlank()) return

        // Rich, warm tone resembling authentic human Arab recitation
        tts?.setPitch(0.92f)
        // Gentle, dignified pace optimal for memorization and accurate pronunciation learning
        tts?.setSpeechRate(customRate)

        val params = android.os.Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "arabic_$id")
        }

        val res = tts?.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, params, "arabic_$id")
        if (res == TextToSpeech.ERROR) {
            // Re-attempt setup
            setupArabicLocale()
            tts?.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, params, "arabic_$id")
        }
    }

    fun speakTranslation(id: Int, text: String, languageCode: String = "en") {
        if (tts == null) {
            tts = TextToSpeech(context.applicationContext, this)
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

        val params = android.os.Bundle().apply {
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

