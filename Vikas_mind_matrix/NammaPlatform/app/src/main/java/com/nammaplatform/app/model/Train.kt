package com.nammaplatform.app.model

/**
 * A single coach in a train's rake.
 *
 * @param code  raw code from JSON (e.g. "S1", "GEN", "ENGINE")
 * @param type  resolved [CoachType] (used for colour / icon on the strip)
 */
data class Coach(
    val code: String,
    val type: CoachType
) {
    companion object {
        fun of(code: String): Coach = Coach(code, CoachType.fromCode(code))
    }
}

/**
 * One arriving / departing train at a station.
 */
data class Train(
    val trainNo: String,
    val nameEn: String,
    val nameKn: String,
    val platform: Int,
    val departure: String,         // "HH:mm" (24-h)
    val coaches: List<Coach>
)
