package com.example.digitallearndiary.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

object UiEventApi {

    fun register(
        context: Context,
        onWifi: (() -> Unit)? = null,
        onMotion: (() -> Unit)? = null
    ): BroadcastReceiver {

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                val type = intent.getStringExtra("type")
                val source = intent.getStringExtra("source")

                // Wi-Fi açıldı
                if (type == "NETWORK_AVAILABLE" && source == "WIFI") {
                    onWifi?.invoke()
                }

                // Motion / hareket algılandı
                if (type == "FOCUS_LOST" && source == "MOTION") {
                    onMotion?.invoke()
                }
            }
        }


        context.registerReceiver(receiver, IntentFilter())

        return receiver
    }

    /**
     * UI event dinlemeyi durdurur.
     */
    fun unregister(context: Context, receiver: BroadcastReceiver) {
        context.unregisterReceiver(receiver)
    }
}
