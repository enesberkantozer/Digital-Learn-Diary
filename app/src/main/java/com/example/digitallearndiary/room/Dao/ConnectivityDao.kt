package com.example.digitallearndiary.room.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.digitallearndiary.room.Tables.Connectivity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConnectivityDao {

    @Upsert
    suspend fun upsert(item: Connectivity)

    @Delete
    suspend fun delete(item: Connectivity)

    @Query("SELECT * FROM connectivity WHERE source = :sourceType ORDER BY timestamp DESC")
    suspend fun getWithSource(sourceType : String ) : Flow<List<Connectivity>>

    @Query("SELECT * FROM connectivity WHERE type = :type ORDER BY timestamp DESC")
    suspend fun getWithType(type : String ) : Flow<List<Connectivity>>

}