package com.example.digitallearndiary.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitallearndiary.room.AppDatabase
import com.example.digitallearndiary.room.Tables.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NoteViewModel(Application: Application) : AndroidViewModel(Application) {
    private val dao = AppDatabase.getDatabase(Application).noteDao()

    fun getNotesByCourse(courseId: String): Flow<List<Note>> {
        return dao.getNotesByCourseId(courseId)
    }

    fun getNoteById(noteId: String): Flow<Note>? {
        return dao.getNoteById(noteId)
    }


    fun upsertNote(note: Note) {
        viewModelScope.launch {
            dao.upsert(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            dao.delete(note)
        }
    }
}