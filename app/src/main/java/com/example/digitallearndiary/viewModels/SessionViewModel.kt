package com.example.digitallearndiary.viewModels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitallearndiary.backgroundForegroundServices.FocusService
import com.example.digitallearndiary.basicData.AyarlarYoneticisi
import com.example.digitallearndiary.receiver.AlarmReceiver
import com.example.digitallearndiary.room.AppDatabase
import com.example.digitallearndiary.room.Tables.StudySession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SessionViewModel(application: Application) : AndroidViewModel(application) {
    private val ayarlar = AyarlarYoneticisi(application)
    var isTimerRunning by mutableStateOf(false)
    var endTimeMillis by mutableLongStateOf(0L)
    var initialDurationMillis by mutableLongStateOf(0L)
    var startTimeMillis by mutableLongStateOf(0L)

    // Duraklatıldığında kalan süreyi burada saklayacağız
    var pausedRemainingMillis by mutableLongStateOf(0L)

    init {
        // Uygulama açıldığında yarım kalan timer var mı kontrol et
        viewModelScope.launch {
            val kaydedilenBitis = ayarlar.oku("aktif_timer_bitis", 0L).first()
            val suAn = System.currentTimeMillis()

            if (kaydedilenBitis > suAn) {
                // Yarım kalan bir seans bulundu!
                endTimeMillis = kaydedilenBitis
                isTimerRunning = true

                // Başlangıç ve toplam süre bilgilerini de DataStore'dan çekmek istersen
                // onları da benzer şekilde kaydedip burada yükleyebilirsin.
            }
        }
    }

    private fun setAlarm(context: Context, triggerTimeMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        // PendingIntent: AlarmManager'ın gelecekte bu intent'i çalıştırmasını sağlar
        val pendingIntent = android.app.PendingIntent.getBroadcast(
            context,
            0,
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        // Tam vaktinde çalması için (Android 6+ için setExactAndAllowWhileIdle)
        alarmManager.setExactAndAllowWhileIdle(
            android.app.AlarmManager.RTC_WAKEUP,
            triggerTimeMillis,
            pendingIntent
        )
    }

    // --- DURAKLAT / DEVAM ET MANTIĞI ---
    fun toggleTimer(context: Context) {
        if (isTimerRunning) {
            // --- DURAKLATMA ---
            pausedRemainingMillis = (endTimeMillis - System.currentTimeMillis()).coerceAtLeast(0L)
            isTimerRunning = false

            // 1. Alarmı iptal et (Yoksa duraklatılmışken süre bitmiş gibi bildirim atar)
            cancelAlarm(context)

            // 2. Servisi durdur (Sensör dinlemeyi bırak)
            val intent = Intent(context, FocusService::class.java)
            context.stopService(intent)

        } else {
            // --- DEVAM ETME ---
            endTimeMillis = System.currentTimeMillis() + pausedRemainingMillis
            isTimerRunning = true

            // 1. Yeni bitiş zamanına göre alarmı tekrar kur
            setAlarm(context, endTimeMillis)

            // 2. Servisi tekrar başlat (Sensörleri tekrar dinle)
            val intent = Intent(context, FocusService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    // SessionViewModel içinde
    fun startTimer(h: Int, m: Int, s: Int, context: android.content.Context) {
        val durationMillis = ((h * 3600L) + (m * 60L) + s) * 1000L
        if (durationMillis <= 0) return

        startTimeMillis = System.currentTimeMillis()
        initialDurationMillis = durationMillis
        endTimeMillis = startTimeMillis + durationMillis
        isTimerRunning = true

        // DataStore'a kaydet (AyarlarYoneticisi kullanarak)
        viewModelScope.launch {
            val ayarlar = AyarlarYoneticisi(context)
            ayarlar.datastoreSave("aktif_timer_bitis", endTimeMillis)
        }

        setAlarm(context, endTimeMillis)

        // --- SERVİSİ BAŞLAT ---
        val intent = Intent(context, FocusService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopTimer(context: android.content.Context) {
        isTimerRunning = false
        endTimeMillis = 0L
        pausedRemainingMillis = 0L

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val intentalarm = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = android.app.PendingIntent.getBroadcast(
            context, 0, intentalarm,
            android.app.PendingIntent.FLAG_NO_CREATE or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let { alarmManager.cancel(it) }

        // --- SERVİSİ DURDUR ---
        val intentfocus = Intent(context, FocusService::class.java)
        context.stopService(intentfocus)
        clearTimerData()
    }

    private fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = android.app.PendingIntent.getBroadcast(
            context, 0, intent,
            android.app.PendingIntent.FLAG_NO_CREATE or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel() // PendingIntent'in kendisini de temizle
        }
    }

    // stopTimer içinde veya seans bittiğinde çağrılmalı
    fun clearTimerData() {
        viewModelScope.launch {
            ayarlar.datastoreSave("aktif_timer_bitis", 0L)
        }
    }






    private val dao = AppDatabase.getDatabase(application).studySessionDao()

    fun getSessionsByCourse(courseId: String): Flow<List<StudySession>> {
        return dao.getSessionByCourseId(courseId)
    }

    fun upsertSession(session: StudySession) {
        viewModelScope.launch {
            dao.upsert(session)
        }
    }

    fun deleteSession(session: StudySession) {
        viewModelScope.launch {
            dao.delete(session)
        }
    }
}