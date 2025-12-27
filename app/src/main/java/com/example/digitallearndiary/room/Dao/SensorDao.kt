package com.example.digitallearndiary.room.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.digitallearndiary.room.Tables.Sensor
import kotlinx.coroutines.flow.Flow

@Dao
interface SensorDao {
    @Upsert
    suspend fun upsert(item: Sensor)

    @Delete
    suspend fun delete(item: Sensor)

    @Query("SELECT * FROM sensor WHERE source = :sourceType ORDER BY timestamp DESC")
    suspend fun getWithSource(sourceType : String ) : Flow<List<Sensor>>

    @Query("SELECT * FROM sensor WHERE type = :type ORDER BY timestamp DESC")
    suspend fun getWithType(type : String ) : Flow<List<Sensor>>
}