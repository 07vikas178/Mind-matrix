package com.nammaplatform.app.ui.platform

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nammaplatform.app.data.StationRepository
import com.nammaplatform.app.model.Station
import com.nammaplatform.app.model.Train
import kotlinx.coroutines.launch

/**
 * Drives the Platform screen.
 *
 * UI state exposed:
 *  • [station]      — for screen title (Kannada + English)
 *  • [nextTrains]   — capped at 3 (PDF requirement)
 *  • [selectedTrain] — which train's coach strip is currently shown
 */
class PlatformViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = StationRepository.get(app)

    private val _station = MutableLiveData<Station?>()
    val station: LiveData<Station?> = _station

    private val _nextTrains = MutableLiveData<List<Train>>()
    val nextTrains: LiveData<List<Train>> = _nextTrains

    private val _selectedTrain = MutableLiveData<Train?>()
    val selectedTrain: LiveData<Train?> = _selectedTrain

    fun load(stationId: String) {
        viewModelScope.launch {
            _station.value     = repo.getStation(stationId)
            val upcoming       = repo.nextTrains(stationId, limit = 3)
            _nextTrains.value  = upcoming
            // Default-select first train so the coach strip isn't empty.
            _selectedTrain.value = upcoming.firstOrNull()
        }
    }

    fun selectTrain(t: Train) { _selectedTrain.value = t }
}
