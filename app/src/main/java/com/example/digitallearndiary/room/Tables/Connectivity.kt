package com.example.digitallearndiary.room.Tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Connectivity(
    @PrimaryKey
    val id : String= UUID.randomUUID().toString(),
    val type : String,
    val source : String,
    val timestamp : Long
)