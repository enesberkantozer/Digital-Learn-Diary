package com.example.digitallearndiary.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import android.app.NotificationManager
import com.example.digitallearndiary.backgroundForegroundServices.FocusService
import kotlin.jvm.java

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 1. Servisi durdur (Çünkü süre bitti)
        val serviceIntent = Intent(context, FocusService::class.java)
        context.stopService(serviceIntent)

        // 2. Başarı Bildirimi Gönder
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, "FocusServiceChannel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Tebrikler!")
            .setContentText("Odaklanma seansını başarıyla tamamladın.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(3, notification)
    }
}