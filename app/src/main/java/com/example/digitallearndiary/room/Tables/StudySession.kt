package com.example.digitallearndiary.room.Tables

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Course::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StudySession(
    @PrimaryKey
    val id : String = UUID.randomUUID().toString(),
    val courseId : String,
    val startTime : Long,
    val endTime : Long,
    val totalTime : Int
)
