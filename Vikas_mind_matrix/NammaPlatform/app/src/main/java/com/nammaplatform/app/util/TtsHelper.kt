package com.nammaplatform.app.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * Thin wrapper around Android's [TextToSpeech] specialised for Kannada.
 *
 * Behaviour:
 *  • Tries Kannada (`kn-IN`) first.
 *  • If the device has no Kannada voice, falls back to Hindi, then English,
 *    and reports the fallback via [onReady] so the UI can warn the user.
 *  • Sets a slightly slower rate (0.9×) and slightly higher pitch — the
 *    PDF asks for "loud and clear" announcements aimed at elderly users.
 */
class TtsHelper(
    context: Context,
    private val onReady: (success: Boolean, usedLocale: Locale?) -> Unit
) {

    private var tts: TextToSpeech? = null
    private var ready = false
    private var usedLocale: Locale? = null

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                usedLocale = pickBestLocale()
                ready = usedLocale != null
                tts?.setSpeechRate(0.9f)
                tts?.setPitch(1.05f)
                onReady(ready, usedLocale)
            } else {
                Log.e(TAG, "TTS init failed, status=$status")
                onReady(false, null)
            }
        }
    }

    /** Speak [text]. No-op if TTS isn't ready. */
    fun speak(text: String) {
        if (!ready) {
            Log.w(TAG, "speak() called before TTS ready")
            return
        }
        // QUEUE_FLUSH = interrupt anything currently being spoken (good for
        // an announcement button that the user might tap repeatedly).
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "namma-tts-1")
    }

    fun stop() { tts?.stop() }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        ready = false
    }

    /** Return the best available locale, preferring Kannada. */
    private fun pickBestLocale(): Locale? {
        val candidates = listOf(
            Locale("kn", "IN"),   // ಕನ್ನಡ — preferred (PDF requirement)
            Locale("hi", "IN"),   // Hindi fallback (most rural devices have it)
            Locale.US             // English last-resort
        )
        for (loc in candidates) {
            val res = tts?.isLanguageAvailable(loc) ?: TextToSpeech.LANG_NOT_SUPPORTED
            val ok = res == TextToSpeech.LANG_AVAILABLE ||
                     res == TextToSpeech.LANG_COUNTRY_AVAILABLE ||
                     res == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE
            if (ok) {
                tts?.language = loc
                Log.i(TAG, "TTS locale = $loc")
                return loc
            }
        }
        return null
    }

    companion object { private const val TAG = "TtsHelper" }
}
