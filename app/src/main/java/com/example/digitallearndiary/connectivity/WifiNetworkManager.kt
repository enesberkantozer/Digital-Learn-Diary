package com.example.digitallearndiary.connectivity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.digitallearndiary.receiver.ConnectivityBroadcastReceiver

class WifiNetworkManager(
    private val context: Context,
    private val onEvent: (ConnectivityEvent) -> Unit
) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            val caps = connectivityManager.getNetworkCapabilities(network)
            val now = System.currentTimeMillis()

            when {
                caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> {
                    onEvent(ConnectivityEvent("NETWORK_AVAILABLE", "WIFI", now))

                    val intent = Intent(context, ConnectivityBroadcastReceiver::class.java)
                    intent.putExtra("type", "NETWORK_AVAILABLE")
                    intent.putExtra("source", "WIFI")
                    context.sendBroadcast(intent)
                }

                caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> {
                    onEvent(ConnectivityEvent("NETWORK_AVAILABLE", "CELLULAR", now))

                    val intent = Intent(context, ConnectivityBroadcastReceiver::class.java)
                    intent.putExtra("type", "NETWORK_AVAILABLE")
                    intent.putExtra("source", "CELLULAR")
                    context.sendBroadcast(intent)
                }
            }
        }

        override fun onLost(network: Network) {
            val caps = connectivityManager.getNetworkCapabilities(network)
            val now = System.currentTimeMillis()

            when {
                caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> {
                    onEvent(ConnectivityEvent("NETWORK_LOST", "WIFI", now))

                    val intent = Intent(context, ConnectivityBroadcastReceiver::class.java)
                    intent.putExtra("type", "NETWORK_LOST")
                    intent.putExtra("source", "WIFI")
                    context.sendBroadcast(intent)
                }

                caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> {
                    onEvent(ConnectivityEvent("NETWORK_LOST", "CELLULAR", now))

                    val intent = Intent(context, ConnectivityBroadcastReceiver::class.java)
                    intent.putExtra("type", "NETWORK_LOST")
                    intent.putExtra("source", "CELLULAR")
                    context.sendBroadcast(intent)
                }
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
