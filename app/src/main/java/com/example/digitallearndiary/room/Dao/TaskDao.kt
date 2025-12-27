package com.example.digitallearndiary.room.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.digitallearndiary.room.Tables.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Upsert
    suspend fun upsert(item : Task)

    @Delete
    suspend fun delete(item : Task)

    @Query("SELECT * FROM task WHERE courseId = :courseId ORDER BY id DESC")
    fun getTaskByCourseId(courseId: Int): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE courseId = :courseId AND isCompleted = 0")
    fun getPendingTask(courseId: Int): Flow<List<Task>>

    @Query("DELETE FROM task WHERE courseId = :courseId AND isCompleted = 1")
    suspend fun deleteCompletedTask(courseId: Int)
}