package com.example.digitallearndiary.firestore.repository

import com.example.digitallearndiary.room.Dao.NoteDao
import com.example.digitallearndiary.room.Tables.Note
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

class NoteRepository(
    private val noteDao: NoteDao,
    private val firestore: FirebaseFirestore
) {
    suspend fun syncNotes() {
        try {
            val localNotes = noteDao.getAllNote().first()
            val remoteSnapshot = firestore.collection("notes").get().await()
            val remoteNotesMap = remoteSnapshot.documents.associate { it.id to it.toObject(Note::class.java) }

            for (localNote in localNotes) {
                val remoteNote = remoteNotesMap[localNote.id]
                if (remoteNote == null || localNote != remoteNote) {
                    firestore.collection("notes").document(localNote.id).set(localNote, SetOptions.merge())
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }
}