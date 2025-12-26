package com.example.digitallearndiary.connectivity

data class ConnectivityEvent(
    val type: String,      // NETWORK_AVAILABLE, NETWORK_LOST
    val source: String,    // WIFI, CELLULAR
    val timestamp: Long
)
