package com.example.digitallearndiary.firestore.repository

import com.example.digitallearndiary.room.Dao.CourseDao
import com.example.digitallearndiary.room.Tables.Course
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

class CourseRepository (
    private val courseDao: CourseDao,
    private val firestore: FirebaseFirestore
) {
    suspend fun syncCourses() {
        try {
            val localCourses = courseDao.getAllCourse().first()
            val remoteSnapshot = firestore.collection("courses").get().await()
            val remoteCoursesMap = remoteSnapshot.documents.associate { it.id to it.toObject(Course::class.java) }

            for (localCourse in localCourses) {
                val remoteCourse = remoteCoursesMap[localCourse.id]
                if (remoteCourse == null || localCourse != remoteCourse) {
                    firestore.collection("courses").document(localCourse.id).set(localCourse, SetOptions.merge())
                }
            }

        } catch (e: Exception) { e.printStackTrace() }
    }
}