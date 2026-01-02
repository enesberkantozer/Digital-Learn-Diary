package com.example.digitallearndiary.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.digitallearndiary.backgroundForegroundServices.FocusService
import com.example.digitallearndiary.basicData.AyarlarYoneticisi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val ayarlar = AyarlarYoneticisi(context)

            CoroutineScope(Dispatchers.IO).launch {
                // DataStore'dan bitiş zamanını oku (0L varsayılan değer)
                val endTime = ayarlar.oku("aktif_timer_bitis", 0L).first()

                if (endTime > System.currentTimeMillis()) {
                    // 1. Alarmı Yeniden Kur
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val alarmIntent = Intent(context, AlarmReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(
                        context, 0, alarmIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endTime, pendingIntent)
                    }

                    // 2. Sensörü (FocusService) Yeniden Başlat
                    val serviceIntent = Intent(context, FocusService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                }
            }
        }
    }
}