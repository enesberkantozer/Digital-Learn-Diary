package com.example.digitallearndiary.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitallearndiary.room.AppDatabase
import com.example.digitallearndiary.room.Tables.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TaskViewModel(application: Application): AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).taskDao()

    fun getTasksByCourse(courseId: String): Flow<List<Task>> {
        return dao.getTaskByCourseId(courseId)
    }

    fun upsertTask(task: Task) {
        viewModelScope.launch {
            dao.upsert(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.delete(task)
        }
    }

}