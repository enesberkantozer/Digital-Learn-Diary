package com.example.digitallearndiary.room.Tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Connectivity(
    @PrimaryKey(autoGenerate = true)
    val id : Int=0,
    val type : String,
    val source : String,
    val timestamp : Long
)