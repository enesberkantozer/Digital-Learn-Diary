package com.example.digitallearndiary.backgroundForegroundServices

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

fun scheduleBackgroundSync(context: Context) {
    // Sadece internet bağlantısı varken çalışması için kısıtlama ekliyoruz
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    // 30 Dakikada bir çalışacak periyodik istek
    val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(30, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .build()

    // Görevi kuyruğa al
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "AutoFirebaseSync", // Görevin benzersiz adı
        ExistingPeriodicWorkPolicy.KEEP, // Eğer görev zaten varsa devam et, üzerine yazma
        syncRequest
    )
}
