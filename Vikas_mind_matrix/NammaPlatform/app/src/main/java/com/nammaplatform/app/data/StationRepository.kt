package com.nammaplatform.app.data

import com.nammaplatform.app.model.Station
import com.nammaplatform.app.model.Train

/**
 * Single point of truth for station/train data. Caches the loaded
 * list so JSON parsing only happens once per process lifetime.
 *
 * Business rule from the PDF: only the **next 3 trains** are shown.
 * That rule lives here (in the data layer) so every UI that asks
 * the repository gets a consistent answer.
 */
class StationRepository(
    private val source: StationDataSource
) {

    @Volatile private var cache: List<Station>? = null

    suspend fun getStations(): List<Station> {
        cache?.let { return it }
        val loaded = source.loadStations()
        cache = loaded
        return loaded
    }

    suspend fun getStation(id: String): Station? =
        getStations().firstOrNull { it.id == id }

    /**
     * Returns the next [limit] trains for [stationId] **after** the
     * current device time — sorted by departure time ascending.
     *
     * @param now  "HH:mm" current time (24-h). Default = system clock.
     */
    suspend fun nextTrains(
        stationId: String,
        limit: Int = 3,
        now: String = currentHHmm()
    ): List<Train> {
        val station = getStation(stationId) ?: return emptyList()
        return station.trains
            .filter { it.departure >= now }       // upcoming only
            .sortedBy { it.departure }
            .take(limit)
            .ifEmpty {
                // Past midnight / no more trains today → show first 3 of next day
                station.trains.sortedBy { it.departure }.take(limit)
            }
    }

    private fun currentHHmm(): String {
        val cal = java.util.Calendar.getInstance()
        val h = cal.get(java.util.Calendar.HOUR_OF_DAY)
        val m = cal.get(java.util.Calendar.MINUTE)
        return "%02d:%02d".format(h, m)
    }

    companion object {
        // Simple hand-rolled service locator. For a bigger app this would
        // be Hilt / Koin; here it keeps the demo dependency-free.
        @Volatile private var INSTANCE: StationRepository? = null

        fun get(appContext: android.content.Context): StationRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: StationRepository(JsonStationDataSource(appContext))
                    .also { INSTANCE = it }
            }
    }
}
