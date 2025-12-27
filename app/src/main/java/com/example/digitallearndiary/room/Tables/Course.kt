package com.example.digitallearndiary.room.Tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val courseName : String,
    val totalTargetMinute : Int,
    val imageTextList : List<String> = emptyList()

)
