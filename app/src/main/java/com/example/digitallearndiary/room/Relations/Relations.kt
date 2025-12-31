package com.example.digitallearndiary.room.Relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.digitallearndiary.room.Tables.Course
import com.example.digitallearndiary.room.Tables.Note
import com.example.digitallearndiary.room.Tables.StudySession
import com.example.digitallearndiary.room.Tables.Task

data class CourseAndSession(

    @Embedded val course : Course,

    @Relation(
        parentColumn = "id",
        entityColumn = "courseId"
    )

    val studySessions : List<StudySession>
)

data class CourseAndTask(

    @Embedded val course : Course,

    @Relation(
        parentColumn = "id",
        entityColumn = "courseId"
    )

    val tasks : List<Task>
)

data class CourseAndNote(

    @Embedded val course : Course,

    @Relation(
        parentColumn = "id",
        entityColumn = "courseId"
    )

    val notes : List<Note>
)