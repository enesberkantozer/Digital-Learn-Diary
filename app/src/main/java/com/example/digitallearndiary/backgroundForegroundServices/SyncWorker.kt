package com.example.digitallearndiary.backgroundForegroundServices

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.digitallearndiary.basicData.AyarlarYoneticisi
import com.example.digitallearndiary.firestore.repository.CourseRepository
import com.example.digitallearndiary.firestore.repository.NoteRepository
import com.example.digitallearndiary.firestore.repository.StudySessionRepository
import com.example.digitallearndiary.firestore.repository.SyncManager
import com.example.digitallearndiary.firestore.repository.TaskRepository
import com.example.digitallearndiary.room.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore

class SyncWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 1. Veritabanı ve Firestore Instance'larını Hazırla
            val database = AppDatabase.getDatabase(applicationContext)
            val firestore = FirebaseFirestore.getInstance()

            // 2. Ayarlar Yoneticisi
            val ayarlarYoneticisi = AyarlarYoneticisi(applicationContext)

            // 3. Repositoryleri İstenen Referanslarla (Dao + Firestore) Oluştur
            val courseRepo = CourseRepository(database.courseDao(), firestore)
            val taskRepo = TaskRepository(database.taskDao(), firestore)
            val sessionRepo = StudySessionRepository(database.studySessionDao(), firestore)
            val noteRepo = NoteRepository(database.noteDao(), firestore)

            // 4. SyncManager'ı Başlat
            val syncManager = SyncManager(
                courseRepository = courseRepo,
                taskRepository = taskRepo,
                studySessionRepository = sessionRepo,
                noteRepository = noteRepo,
                ayarlarYoneticisi = ayarlarYoneticisi
            )

            // 5. Merkezi Senkronizasyonu Çalıştır
            syncManager.syncAll()

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry() // Hata oluşursa (örn: internet kesintisi) sistem tekrar deneyecektir
        }
    }
}