package com.example.digitallearndiary.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitallearndiary.room.AppDatabase
import com.example.digitallearndiary.room.Tables.Course
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CourseViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).courseDao()
    val courses: Flow<List<Course>> = dao.getAllCourse()

    fun delete(course: Course) {
        viewModelScope.launch {
            dao.delete(course)
        }
    }

    fun upsert(course: Course) {
        viewModelScope.launch {
            dao.upsert(course)
        }
    }

    fun getWithId(id: String?) : Flow<Course>? {
        if (id == null) return null
        return dao.getCourseById(id)
    }
}