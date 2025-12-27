package com.example.digitallearndiary.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.digitallearndiary.room.Dao.ConnectivityDao
import com.example.digitallearndiary.room.Dao.CourseDao
import com.example.digitallearndiary.room.Dao.SensorDao
import com.example.digitallearndiary.room.Dao.StudySessionDao
import com.example.digitallearndiary.room.Dao.TaskDao
import com.example.digitallearndiary.room.Tables.Connectivity
import com.example.digitallearndiary.room.Tables.Course
import com.example.digitallearndiary.room.Tables.Sensor
import com.example.digitallearndiary.room.Tables.StudySession
import com.example.digitallearndiary.room.Tables.Task

@Database(
    entities = [
        Course::class,
        StudySession::class,
        Task::class,
        Connectivity::class,
        Sensor::class
    ],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun courseDao(): CourseDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun taskDao(): TaskDao
    abstract fun connectivityDao(): ConnectivityDao
    abstract fun sensorDao(): SensorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "digital_learn_diary_db"
                ).fallbackToDestructiveMigration().build()

                INSTANCE = instance
                instance
            }
        }
    }
}