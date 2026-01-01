package com.example.digitallearndiary.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitallearndiary.room.AppDatabase
import com.example.digitallearndiary.room.Tables.StudySession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SessionViewModel(application: Application) : AndroidViewModel(application) {
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