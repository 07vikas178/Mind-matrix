package com.nammaplatform.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nammaplatform.app.databinding.ActivityPlatformBinding
import com.nammaplatform.app.model.Train
import com.nammaplatform.app.ui.platform.PlatformViewModel
import com.nammaplatform.app.ui.platform.TrainAdapter
import com.nammaplatform.app.util.TtsHelper
import java.util.Locale

/**
 * Shows:
 *   • Selected station name (Kannada-first)
 *   • Up to 3 next trains (tap to switch the coach strip below)
 *   • Coach strip for the currently selected train
 *   • Big yellow "Help Me / ಕನ್ನಡದಲ್ಲಿ ಕೇಳಿ" button → speaks announcement
 */
class PlatformActivity : AppCompatActivity() {

    private lateinit var b: ActivityPlatformBinding
    private val vm: PlatformViewModel by viewModels()
    private lateinit var tts: TtsHelper
    private var ttsLocale: Locale? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPlatformBinding.inflate(layoutInflater)
        setContentView(b.root)

        val stationId = intent.getStringExtra(EXTRA_STATION_ID)
        if (stationId.isNullOrBlank()) {
            Toast.makeText(this, R.string.err_no_station, Toast.LENGTH_LONG).show()
            finish(); return
        }

        // ---- TTS setup ----
        tts = TtsHelper(this) { ok, locale ->
            ttsLocale = locale
            if (!ok) {
                Toast.makeText(this, R.string.err_tts_unavailable, Toast.LENGTH_LONG).show()
            } else if (locale?.language != "kn") {
                // Inform user we couldn't find Kannada — they can install it from Settings.
                Toast.makeText(this, R.string.warn_no_kannada, Toast.LENGTH_LONG).show()
            }
        }

        // ---- Train list ----
        val trainAdapter = TrainAdapter { vm.selectTrain(it) }
        b.rvTrains.layoutManager = LinearLayoutManager(this)
        b.rvTrains.adapter = trainAdapter

        // ---- Help-Me button ----
        b.btnHelp.setOnClickListener { announceCurrent() }

        // ---- Observe state ----
        vm.station.observe(this) { st ->
            st?.let {
                b.tvStationKn.text = it.nameKn
                b.tvStationEn.text = it.nameEn
            }
        }
        vm.nextTrains.observe(this) { trains ->
            trainAdapter.submitList(trains)
            if (trains.isEmpty()) {
                b.tvEmpty.visibility = android.view.View.VISIBLE
            } else {
                b.tvEmpty.visibility = android.view.View.GONE
            }
        }
        vm.selectedTrain.observe(this) { t ->
            t?.let {
                b.coachStrip.setCoaches(it.coaches)
                b.tvSelectedTrain.text = getString(
                    R.string.fmt_selected_train, it.nameKn, it.platform
                )
                trainAdapter.setSelected(it.trainNo)
            }
        }

        vm.load(stationId)
    }

    /**
     * Build a Kannada announcement for the currently-selected train and speak it.
     * Falls back to an English version if no Kannada voice is available.
     */
    private fun announceCurrent() {
        val t: Train = vm.selectedTrain.value ?: run {
            Toast.makeText(this, R.string.err_no_train, Toast.LENGTH_SHORT).show()
            return
        }
        val text = if (ttsLocale?.language == "kn") {
            // ಗಮನಿಸಿ! ರೈಲು <name> ಪ್ಲಾಟ್‌ಫಾರ್ಮ್ <n> ಗೆ ಬರಲಿದೆ. ಸಮಯ <hh:mm>.
            getString(R.string.tts_kn, t.nameKn, t.platform, t.departure)
        } else {
            getString(R.string.tts_en, t.nameEn, t.platform, t.departure)
        }
        tts.speak(text)
    }

    override fun onPause() { super.onPause(); tts.stop() }
    override fun onDestroy() { tts.shutdown(); super.onDestroy() }

    companion object {
        const val EXTRA_STATION_ID = "extra_station_id"
    }
}
