package com.emfad.app.Models

data class MeasurementData(
    val id: String,
    val timestamp: Long,
    val frequency: Double,
    val mode: MeasurementMode,
    val orientation: MeasurementOrientation,
    val value: Double,
    val latitude: Double?,
    val longitude: Double?
)

enum class MeasurementMode {
    A, B, A_B, B_A
}

enum class MeasurementOrientation {
    HORIZONTAL, VERTICAL
}

data class MeasurementProfile(
    val id: String,
    val name: String,
    val measurements: List<MeasurementData>,
    val profileLength: Double,
    val distance: Double
)

data class MeasurementSession(
    val id: String,
    val name: String,
    val profiles: List<MeasurementProfile>,
    val startTime: Long,
    val endTime: Long?,
    val settings: MeasurementSettings
)

data class MeasurementSettings(
    val frequency: Double,
    val mode: MeasurementMode,
    val orientation: MeasurementOrientation,
    val autoInterval: Double?,
    val filterLevel: Int,
    val gain: Double,
    val offset: Double
) 