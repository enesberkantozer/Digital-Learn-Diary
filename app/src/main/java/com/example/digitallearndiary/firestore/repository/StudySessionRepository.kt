package com.example.digitallearndiary.firestore.repository

import com.example.digitallearndiary.room.Dao.StudySessionDao
import com.example.digitallearndiary.room.Tables.StudySession
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

class StudySessionRepository(
    private val studySessionDao: StudySessionDao,
    private val firestore: FirebaseFirestore
) {
    suspend fun syncSessions() {
        try {
            val localSessions = studySessionDao.getAllSessions().first()
            val remoteSnapshot = firestore.collection("studySessions").get().await()
            val remoteSessionsMap = remoteSnapshot.documents.associate { it.id to it.toObject(
                StudySession::class.java) }

            for (localSession in localSessions) {
                val remoteSession = remoteSessionsMap[localSession.id]
                if (remoteSession == null || localSession != remoteSession) {
                    firestore.collection("studySessions").document(localSession.id).set(localSession, SetOptions.merge())
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }
}