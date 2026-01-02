package com.example.digitallearndiary.firestore.repository

import com.example.digitallearndiary.room.Dao.TaskDao
import com.example.digitallearndiary.room.Tables.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

class TaskRepository(
    private val taskDao: TaskDao,
    private val firestore: FirebaseFirestore
) {
    suspend fun syncTasks(userMail: String) {
        try {
            val localTasks = taskDao.getAllTasks().first()
            val remoteSnapshot = firestore.collection("users")
                .document(userMail).collection("tasks").get().await()
            val remoteTasksMap = remoteSnapshot.documents.associate { it.id to it.toObject(Task::class.java) }

            for (localTask in localTasks) {
                val remoteTask = remoteTasksMap[localTask.id]
                if (remoteTask == null || localTask != remoteTask) {
                    firestore.collection("tasks").document(localTask.id).set(localTask, SetOptions.merge())
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }
}