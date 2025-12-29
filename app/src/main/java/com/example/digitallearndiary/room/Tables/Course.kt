package com.example.digitallearndiary.room.Tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Course(
    @PrimaryKey
    val id : String = UUID.randomUUID().toString(),
    val courseName : String,
    val totalTargetMinute : Int,
    val imageTextList : List<String> = emptyList()

)
