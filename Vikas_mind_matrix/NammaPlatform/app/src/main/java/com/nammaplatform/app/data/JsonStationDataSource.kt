package com.nammaplatform.app.data

import android.content.Context
import com.nammaplatform.app.model.Coach
import com.nammaplatform.app.model.Station
import com.nammaplatform.app.model.Train
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Reads station / train / coach data from `assets/stations.json`.
 *
 * The PDF requires a *local JSON file* — this class is the single
 * place the file is parsed, isolating I/O from the rest of the app.
 *
 * We intentionally use the built-in `org.json` parser instead of a
 * third-party library (Gson / Moshi) to keep the APK small for
 * rural devices.
 */
class JsonStationDataSource(
    private val appContext: Context,
    private val fileName: String = "stations.json"
) : StationDataSource {

    override suspend fun loadStations(): List<Station> = withContext(Dispatchers.IO) {
        val raw = appContext.assets.open(fileName)
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }

        val root = JSONObject(raw)
        val arr  = root.getJSONArray("stations")

        List(arr.length()) { i -> parseStation(arr.getJSONObject(i)) }
    }

    private fun parseStation(j: JSONObject): Station {
        val trainsArr = j.getJSONArray("trains")
        val trains = List(trainsArr.length()) { i ->
            parseTrain(trainsArr.getJSONObject(i))
        }
        return Station(
            id     = j.getString("id"),
            nameEn = j.getString("nameEn"),
            nameKn = j.getString("nameKn"),
            trains = trains
        )
    }

    private fun parseTrain(j: JSONObject): Train {
        val coachesArr = j.getJSONArray("coaches")
        val coaches = List(coachesArr.length()) { i ->
            Coach.of(coachesArr.getString(i))
        }
        return Train(
            trainNo   = j.getString("trainNo"),
            nameEn    = j.getString("nameEn"),
            nameKn    = j.getString("nameKn"),
            platform  = j.getInt("platform"),
            departure = j.getString("departure"),
            coaches   = coaches
        )
    }
}
