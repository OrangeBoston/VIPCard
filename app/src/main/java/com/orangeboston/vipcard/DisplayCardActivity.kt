package com.orangeboston.vipcard

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.util.Locale

class DisplayCardActivity : AppCompatActivity() {

    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var ttsFailed = false
    private var pendingSpeech: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        enableEdgeToEdge()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContentView(R.layout.activity_display_card)

        val storeName = intent.getStringExtra(EXTRA_STORE_NAME).orEmpty()
        val phoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER).orEmpty()

        findViewById<TextView>(R.id.textStoreName).text = storeName
        findViewById<TextView>(R.id.textPhoneNumber).text = buildPhoneNumberText(phoneNumber)

        findViewById<View>(R.id.btnClose).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btnSpeak).setOnClickListener {
            speakPhoneNumber(phoneNumber)
        }

        initTts()
    }

    private fun initTts() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.CHINA)
                ttsReady = result != null &&
                    result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
                tts?.setSpeechRate(0.85f)
                if (ttsReady) {
                    pendingSpeech?.let {
                        tts?.speak(it, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID)
                        pendingSpeech = null
                    }
                }
            }
            if (!ttsReady) {
                ttsFailed = true
                if (pendingSpeech != null) {
                    pendingSpeech = null
                    showTtsUnavailable()
                }
            }
        }
    }

    private fun speakPhoneNumber(phone: String) {
        val digits = phone.replace(Regex("[^0-9]"), "")
        if (digits.isEmpty()) return
        val grouped = buildString {
            digits.forEachIndexed { index, c ->
                append(DIGIT_NAMES[c] ?: c)
                if (digits.length in 10..11 && (index == 2 || index == 6)) {
                    append('，')
                }
            }
        }
        when {
            ttsReady -> tts?.speak(grouped, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID)
            ttsFailed -> showTtsUnavailable()
            else -> pendingSpeech = grouped
        }
    }

    private fun showTtsUnavailable() {
        Toast.makeText(this, R.string.tts_unavailable, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        super.onDestroy()
    }

    private fun buildPhoneNumberText(phone: String): CharSequence {
        val digits = phone.replace(Regex("[^0-9]"), "")
        val middleStart = when (digits.length) {
            11, 10 -> 4
            else -> return phone
        }
        val text = "${digits.take(3)} ${digits.slice(3..6)} ${digits.takeLast(digits.length - 7)}"
        val highlightColor = ContextCompat.getColor(this, R.color.accent)
        return SpannableString(text).apply {
            setSpan(
                ForegroundColorSpan(highlightColor),
                middleStart,
                middleStart + 4,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    companion object {
        private const val EXTRA_STORE_NAME = "storeName"
        private const val EXTRA_PHONE_NUMBER = "phoneNumber"
        private const val UTTERANCE_ID = "phone_number"
        // 电话号码里的 1 习惯读「幺」，也避免被引擎当成普通数字
        private val DIGIT_NAMES = mapOf(
            '0' to '零', '1' to '幺', '2' to '二', '3' to '三',
            '4' to '四', '5' to '五', '6' to '六', '7' to '七',
            '8' to '八', '9' to '九'
        )

        fun newIntent(context: Context, card: MemberCard): Intent {
            return Intent(context, DisplayCardActivity::class.java).apply {
                putExtra(EXTRA_STORE_NAME, card.storeName)
                putExtra(EXTRA_PHONE_NUMBER, card.phoneNumber)
            }
        }
    }
}
