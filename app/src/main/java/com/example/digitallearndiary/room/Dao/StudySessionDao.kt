package com.example.digitallearndiary.room.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.digitallearndiary.room.Tables.StudySession
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {

    @Upsert
    suspend fun upsert(item : StudySession)

    @Delete
    suspend fun delete(item : StudySession)

    @Query("SELECT * FROM studysession WHERE courseId = :courseId ORDER BY startTime DESC")
    fun getSessionByCourseId(courseId: Int): Flow<List<StudySession>>

    @Query("SELECT SUM(totalTime) FROM studysession WHERE courseId = :courseId")
    fun getTotalStudyTimeForCourse(courseId: Int): Flow<Int?>
}