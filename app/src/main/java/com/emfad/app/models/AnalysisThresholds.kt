package com.emfad.app.models

import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

data class AnalysisThresholds(
    val magneticFieldMetalThreshold: Float = 50f,
    val electricFieldCavityThreshold: Float = 30f,
    val confidenceThreshold: Float = 0.7f,
    val trendWindowSize: Int = 5
) {
    companion object {
        val MAGNETIC_FIELD_THRESHOLD_KEY = floatPreferencesKey("magnetic_field_threshold")
        val ELECTRIC_FIELD_THRESHOLD_KEY = floatPreferencesKey("electric_field_threshold")
        val CONFIDENCE_THRESHOLD_KEY = floatPreferencesKey("confidence_threshold")
        val TREND_WINDOW_SIZE_KEY = intPreferencesKey("trend_window_size")
    }
}
