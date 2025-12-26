package com.example.digitallearndiary.model


data class SensorEvent(
    val type: String,      // FOCUS_LOST, NETWORK_LOST
    val source: String,    // MOTION, WIFI
    val timestamp: Long
)
