package com.example.digitallearndiary.room.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.digitallearndiary.room.Relations.CourseAndSession
import com.example.digitallearndiary.room.Relations.CourseAndTask
import com.example.digitallearndiary.room.Tables.Course
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Upsert
    suspend fun upsert(item : Course)

    @Delete
    suspend fun delete(item : Course)

    @Query("SELECT * FROM course ORDER BY id DESC")
    fun getAllCourse(): Flow<List<Course>>

    @Query("SELECT * FROM course WHERE id = :courseId")
    suspend fun getCourseById(courseId: Int): Course?

    @Transaction
    @Query("SELECT * FROM course WHERE id = :courseId")
    fun getCourseWithSession(courseId: Int): Flow<CourseAndSession>

    @Transaction
    @Query("SELECT * FROM course WHERE id = :courseId")
    fun getCourseWithTask(courseId: Int): Flow<CourseAndTask>

}