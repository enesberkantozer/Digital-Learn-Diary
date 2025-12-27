package com.example.digitallearndiary.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

class WifiNetworkManager(
    context: Context,
    private val onEvent: (ConnectivityEvent) -> Unit
) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            val caps = connectivityManager.getNetworkCapabilities(network)
            val now = System.currentTimeMillis()

            when {
                caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ->
                    onEvent(ConnectivityEvent("NETWORK_AVAILABLE", "WIFI", now))

                caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true ->
                    onEvent(ConnectivityEvent("NETWORK_AVAILABLE", "CELLULAR", now))
            }
        }

        override fun onLost(network: Network) {
            val caps = connectivityManager.getNetworkCapabilities(network)
            val now = System.currentTimeMillis()

            when {
                caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ->
                    onEvent(ConnectivityEvent("NETWORK_LOST", "WIFI", now))

                caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true ->
                    onEvent(ConnectivityEvent("NETWORK_LOST", "CELLULAR", now))
            }
        }
    }

    fun startListening() {
        val request = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    fun stopListening() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
