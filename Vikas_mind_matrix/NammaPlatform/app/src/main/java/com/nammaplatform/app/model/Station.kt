package com.nammaplatform.app.model

/**
 * Railway station with its upcoming trains.
 */
data class Station(
    val id: String,
    val nameEn: String,
    val nameKn: String,
    val trains: List<Train>
)
