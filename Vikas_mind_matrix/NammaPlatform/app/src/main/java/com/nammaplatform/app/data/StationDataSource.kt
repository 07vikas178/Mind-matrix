package com.nammaplatform.app.data

import com.nammaplatform.app.model.Station

/**
 * Abstraction over wherever station data comes from.
 *
 * Keeping this as an interface (Dependency-Inversion) means the
 * repository is free of any I/O concern — we can swap the JSON
 * loader out for a network or Room source without touching the
 * ViewModels that depend on the repository.
 */
interface StationDataSource {
    /** Loads ALL stations bundled with the app. Suspend = off the main thread. */
    suspend fun loadStations(): List<Station>
}
