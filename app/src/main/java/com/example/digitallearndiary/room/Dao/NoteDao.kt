package com.example.digitallearndiary.room.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.digitallearndiary.room.Tables.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Upsert
    suspend fun upsert(item: Note)

    @Delete
    suspend fun delete(item : Note)

    @Query("SELECT * FROM note")
    fun getAllNote(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE courseId = :courseId ORDER BY createdTime DESC")
    fun getSessionByCourseId(courseId: String): Flow<List<Note>>

}