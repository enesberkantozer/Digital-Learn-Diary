package com.example.digitallearndiary.connectivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import com.example.digitallearndiary.receiver.ConnectivityBroadcastReceiver

class UsbReceiver(
    private val onEvent: (ConnectivityEvent) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val now = System.currentTimeMillis()

        when (intent.action) {

            UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                onEvent(
                    ConnectivityEvent(
                        type = "USB_ATTACHED",
                        source = "USB",
                        timestamp = now
                    )
                )


                val broadcastIntent =
                    Intent(context, ConnectivityBroadcastReceiver::class.java)
                broadcastIntent.putExtra("type", "USB_ATTACHED")
                broadcastIntent.putExtra("source", "USB")
                context.sendBroadcast(broadcastIntent)
            }

            UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                onEvent(
                    ConnectivityEvent(
                        type = "USB_DETACHED",
                        source = "USB",
                        timestamp = now
                    )
                )


                val broadcastIntent =
                    Intent(context, ConnectivityBroadcastReceiver::class.java)
                broadcastIntent.putExtra("type", "USB_DETACHED")
                broadcastIntent.putExtra("source", "USB")
                context.sendBroadcast(broadcastIntent)
            }
        }
    }
}
