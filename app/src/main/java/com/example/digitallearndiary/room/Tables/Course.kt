package com.example.digitallearndiary.room.Tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Course(
    @PrimaryKey
    val id : String = UUID.randomUUID().toString(),
    val courseName : String = "",
    val colorInt: Int = 0,
    val createdTime : Long =0

)
