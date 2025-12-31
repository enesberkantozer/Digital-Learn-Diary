package com.example.digitallearndiary.firestore

import android.content.Context
import com.example.digitallearndiary.firestore.repository.CourseRepository
import com.example.digitallearndiary.firestore.repository.NoteRepository
import com.example.digitallearndiary.firestore.repository.StudySessionRepository
import com.example.digitallearndiary.firestore.repository.SyncManager
import com.example.digitallearndiary.firestore.repository.TaskRepository
import com.example.digitallearndiary.firestore.viewmodel.MainViewModelFactory
import com.example.digitallearndiary.room.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseContainer(private val context: Context) {

    private val database by lazy { AppDatabase.getDatabase(context) }
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    private val courseRepository by lazy {
        CourseRepository(database.courseDao(), firestore)
    }
    private val taskRepository by lazy {
        TaskRepository(database.taskDao(), firestore)
    }
    private val studySessionRepository by lazy {
        StudySessionRepository(database.studySessionDao(), firestore)
    }
    private val noteRepository by lazy {
        NoteRepository(database.noteDao(), firestore)
    }

    val syncManager by lazy {
        SyncManager(
            courseRepository,
            taskRepository,
            studySessionRepository,
            noteRepository
        )
    }

    val mainViewModelFactory by lazy {
        MainViewModelFactory(syncManager)
    }

}