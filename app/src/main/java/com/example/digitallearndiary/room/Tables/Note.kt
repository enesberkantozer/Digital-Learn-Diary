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
data class Note(
    @PrimaryKey
    val id : String = UUID.randomUUID().toString(),
    val courseId : String = "",
    val title : String = "",
    val content : String = "",
    val createdTime : Long = 0
)
