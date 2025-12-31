package com.example.digitallearndiary.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.digitallearndiary.notification.AppNotificationHelper

class ConnectivityBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val type = intent.getStringExtra("type")
        val source = intent.getStringExtra("source")

        Log.d("ConnectivityReceiver", "Event received: $type from $source")

        val message = when (type) {
            "NETWORK_AVAILABLE" -> "İnternet bağlantısı kuruldu ($source)"
            "NETWORK_LOST" -> "İnternet bağlantısı kesildi ($source)"
            "USB_ATTACHED" -> "USB cihaz takıldı"
            "USB_DETACHED" -> "USB cihaz çıkarıldı"
            "FOCUS_LOST" -> "Cihaz hareket algıladı"
            "NFC_READ" -> "NFC etiketi okundu"
            "TEST_EVENT" -> "Test bildirimi alındı"
            else -> "Bilinmeyen bir olay gerçekleşti"
        }

        AppNotificationHelper.show(
            context = context,
            title = "Bildirim",
            message = message
        )
    }
}
