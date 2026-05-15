package com.nammaplatform.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nammaplatform.app.data.StationRepository
import com.nammaplatform.app.model.Station
import kotlinx.coroutines.launch

/**
 * Drives the station-picker screen. Exposes [stations] as LiveData so
 * the Activity can observe it; UI never touches the repository directly.
 */
class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = StationRepository.get(app)

    private val _stations = MutableLiveData<List<Station>>()
    val stations: LiveData<List<Station>> = _stations

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init { load() }

    fun load() {
        viewModelScope.launch {
            try {
                _stations.value = repo.getStations()
            } catch (t: Throwable) {
                _error.value = t.message ?: "Failed to load stations"
            }
        }
    }
}
