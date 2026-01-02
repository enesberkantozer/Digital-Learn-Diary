package com.example.digitallearndiary.firestore.repository

import android.util.Log
import com.example.digitallearndiary.basicData.AyarlarYoneticisi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SyncManager(
    private val courseRepository: CourseRepository,
    private val taskRepository: TaskRepository,
    private val studySessionRepository: StudySessionRepository,
    private val noteRepository: NoteRepository,
    private val ayarlarYoneticisi: AyarlarYoneticisi
) {
    suspend fun syncAll() = withContext(Dispatchers.IO) {
        val userMail = ayarlarYoneticisi.emailAkisi.first()

        if (userMail.isNullOrEmpty()) {
            Log.e("SYNC", "Kullanıcı maili bulunamadı, senkronizasyon iptal.")
            return@withContext
        }

        val courseJob = async { courseRepository.syncCourses(userMail) }
        val taskJob = async { taskRepository.syncTasks(userMail) }
        val sessionJob = async { studySessionRepository.syncSessions(userMail) }
        val noteJob = async { noteRepository.syncNotes(userMail) }

        courseJob.await()
        taskJob.await()
        sessionJob.await()
        noteJob.await()
    }
}