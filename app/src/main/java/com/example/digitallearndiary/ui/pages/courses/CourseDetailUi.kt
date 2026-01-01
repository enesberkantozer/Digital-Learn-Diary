package com.example.digitallearndiary.ui.pages.courses

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.digitallearndiary.room.Tables.Course

@Composable
fun CourseDetailScreen(
    course: Course, // Artık direkt nesne geliyor
    navController: NavHostController,
    onBack: () -> Unit
) {
    // Nesneden renk ve isim değerlerini alıyoruz
    val courseColor = Color(course.colorInt)
    val courseName = course.courseName

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Notlar", "Seanslar", "Görevler")

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri", tint = courseColor)
                    }
                    Text(
                        text = courseName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = courseColor
                    )
                }

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    contentColor = courseColor,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = courseColor
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    color = if (selectedTabIndex == index) courseColor else Color.Gray
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTabIndex) {
                0 -> NotesTab(
                    course = course,
                    onAddNoteClick = { navController.navigate("add_note/${course.id}") },
                    onNoteClick = { note ->
                        // Notun ID'sini navigasyonla gönderiyoruz
                        navController.navigate("add_note/${course.id}?noteId=${note.id}")
                    }
                )
                1 -> SessionsTab(course = course) // Artık nesnenin tamamı gidiyor
                2 -> TasksTab(course = course)   // Artık nesnenin tamamı gidiyor
            }
        }
    }
}