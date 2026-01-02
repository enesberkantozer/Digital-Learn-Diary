package com.example.digitallearndiary.backgroundForegroundServices

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.digitallearndiary.sensor.MotionSensorManager

class FocusService : Service() {

    private var motionSensorManager: MotionSensorManager? = null
    private var connectivityManager: ConnectivityManager? = null
    private val CHANNEL_ID = "FocusServiceChannel"

    // UiEventApi'deki IntentFilter ile AYNI olmalı
    private val UI_EVENT_ACTION = "com.example.digitallearndiary.UI_EVENT"

    // Wi-Fi durumunu izleyen callback
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            // Wi-Fi açıldığında veya bir ağa bağlandığında çalışır
            sendFocusBroadcast("NETWORK_AVAILABLE", "WIFI")
            showNotification("Bağlantı Algılandı!", "Wi-Fi açıldı, odağını bozma.")
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // 1. Sensör Yönetimi
        motionSensorManager = MotionSensorManager(this) { event ->
            // Hareket algılandığında broadcast gönder
            sendFocusBroadcast("FOCUS_LOST", "MOTION")
            showNotification("Odaklanma Bozuldu!", "Hareket algılandı, telefonunu bırak.")
        }

        // 2. Wi-Fi Yönetimi
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Odaklanma Seansı Aktif")
            .setContentText("Hareket ve Wi-Fi izleniyor...")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .build()

        startForeground(1, notification)

        // İzlemeleri Başlat
        motionSensorManager?.start()

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)

        return START_STICKY
    }

    // Ortak Broadcast Gönderme Fonksiyonu
    private fun sendFocusBroadcast(type: String, source: String) {
        val intent = Intent(UI_EVENT_ACTION).apply {
            putExtra("type", type)
            putExtra("source", source)
            // Eğer UI bu mesajları "Internal" (uygulama içi) bekliyorsa:
            setPackage(packageName)
        }
        sendBroadcast(intent)
    }

    // Ortak Bildirim Gösterme Fonksiyonu
    private fun showNotification(title: String, text: String) {
        val alert = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(2, alert) // ID: 2 (Üst üste binmemesi için sabit ID)
    }

    override fun onDestroy() {
        // İzlemeleri Durdur (Pil tasarrufu için kritik!)
        motionSensorManager?.stop()
        try {
            connectivityManager?.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            // Callback zaten kayıtlı değilse hata vermemesi için
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Focus Service", NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}