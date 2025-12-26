package com.example.digitallearndiary.connectivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager

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
            }

            UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                onEvent(
                    ConnectivityEvent(
                        type = "USB_DETACHED",
                        source = "USB",
                        timestamp = now
                    )
                )
            }
        }
    }
}
