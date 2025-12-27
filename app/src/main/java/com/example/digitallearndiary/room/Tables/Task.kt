package com.example.digitallearndiary.room.Tables

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity (

    foreignKeys = [
        ForeignKey(
            entity = Course::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Task(
    @PrimaryKey
    val id : Int=0,
    val courseId : Int,
    val taskDesc : String,
    val isCompleted : Boolean

)

