package com.example.digitallearndiary.firestore.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class SyncManager(
    private val courseRepository: CourseRepository,
    private val taskRepository: TaskRepository,
    private val studySessionRepository: StudySessionRepository,
    private val noteRepository: NoteRepository
) {
    suspend fun syncAll() = withContext(Dispatchers.IO) {
        val courseJob = async { courseRepository.syncCourses() }
        val taskJob = async { taskRepository.syncTasks() }
        val sessionJob = async { studySessionRepository.syncSessions() }
        val noteJob = async { noteRepository.syncNotes() }

        courseJob.await()
        taskJob.await()
        sessionJob.await()
        noteJob.await()
    }
}