package com.example.ui.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class DuaSpeaker(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    
    private val _isArabicReady = MutableStateFlow(false)
    val isArabicReady: StateFlow<Boolean> = _isArabicReady

    private val _isPlaying = MutableStateFlow<Int?>(null) // Currently playing Dua ID
    val isPlaying: StateFlow<Int?> = _isPlaying

    private val _isPlayingTranslation = MutableStateFlow<Int?>(null) // Currently playing English translation Dua ID
    val isPlayingTranslation: StateFlow<Int?> = _isPlayingTranslation

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
            val result = tts?.setLanguage(Locale.forLanguageTag("ar"))
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                _isArabicReady.value = true
            }
        }
    }

    fun speakArabic(id: Int, text: String) {
        if (tts != null) {
            stop()
            tts?.setLanguage(Locale.forLanguageTag("ar"))
            tts?.setPitch(0.95f)
            tts?.setSpeechRate(0.85f) // Slightly slower rate for clear Quranic pronunciation practice
            val params = android.os.Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "arabic_$id")
            }
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "arabic_$id")
        }
    }

    fun speakTranslation(id: Int, text: String) {
        if (tts != null) {
            stop()
            tts?.setLanguage(Locale.US)
            tts?.setPitch(1.0f)
            tts?.setSpeechRate(0.95f)
            val params = android.os.Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "translation_$id")
            }
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "translation_$id")
        }
    }

    fun stop() {
        tts?.stop()
        _isPlaying.value = null
        _isPlayingTranslation.value = null
    }

    fun shutdown() {
        tts?.shutdown()
        tts = null
    }
}
