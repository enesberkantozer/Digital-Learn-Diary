package com.example.digitallearndiary.ui.pages.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.digitallearndiary.room.Tables.Course
import com.example.digitallearndiary.viewModels.CourseViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@Composable
fun CourseListScreen(viewModel: CourseViewModel, onCourseClick: (String) -> Unit) {
    val courseList by viewModel.courses.collectAsState(initial = emptyList())

    var isAddingNewCourse by remember { mutableStateOf(false) }
    var editingCourseId by remember { mutableStateOf<String?>(null) }

    var courseNameInput by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color.Blue) }

    val resetForm = {
        courseNameInput = ""
        selectedColor = Color.Blue
        isAddingNewCourse = false
        editingCourseId = null
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var courseToDelete by remember { mutableStateOf<Course?>(null) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Dersi Sil") },
            text = { Text("Bu dersi silmek istediğinize emin misiniz? Bu işlem geri alınamaz.") },
            confirmButton = {
                TextButton(onClick = {
                    courseToDelete?.let { course ->
                        viewModel.delete(course)
                    }
                    showDeleteDialog = false
                }) { Text("Sil", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("İptal") }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                resetForm()
                isAddingNewCourse = true
            }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Derslerim", style = MaterialTheme.typography.headlineMedium)
            }

            if (isAddingNewCourse || editingCourseId != null) {
                item {
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (editingCourseId != null) "Dersi Düzenle" else "Yeni Ders Ekle",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(selectedColor))
                                Spacer(modifier = Modifier.width(12.dp))
                                TextField(
                                    value = courseNameInput,
                                    onValueChange = { courseNameInput = it },
                                    placeholder = { Text("Ders İsmi") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }

                            Text(
                                "Renk Seçimi",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red)
                                        )
                                    )
                                    .pointerInput(Unit) {
                                        detectTapGestures { offset ->
                                            val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                                            selectedColor = Color.hsv(fraction * 360f, 0.8f, 0.9f)
                                        }
                                    }
                                    .pointerInput(Unit) {
                                        detectDragGestures { change, _ ->
                                            val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                                            selectedColor = Color.hsv(fraction * 360f, 0.8f, 0.9f)
                                        }
                                    }
                            )

                            Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { resetForm() }) { Text("İptal") }
                                val scope = rememberCoroutineScope()
                                Button(onClick = {
                                    scope.launch {
                                        if (courseNameInput.isNotEmpty()) {

                                            if (editingCourseId != null) {
                                                // Artık suspend fonksiyon olan firstOrNull() burada çalışabilir
                                                viewModel.getWithId(editingCourseId)?.firstOrNull()
                                                    ?.let { oldCourse ->
                                                        val updatedCourse = oldCourse.copy(
                                                            courseName = courseNameInput,
                                                            colorInt = selectedColor.toArgb(),
                                                            createdTime = System.currentTimeMillis()
                                                        )
                                                        viewModel.upsert(updatedCourse)
                                                    }
                                            } else {
                                                // Yeni kayıt kısmı
                                                val newCourse = Course(
                                                    courseName = courseNameInput,
                                                    colorInt = selectedColor.toArgb(),
                                                    createdTime = System.currentTimeMillis()
                                                )
                                                viewModel.upsert(newCourse)
                                            }
                                            resetForm()
                                        }
                                    }
                                }) {
                                    Text(if (editingCourseId != null) "Güncelle" else "Ekle")
                                }
                            }
                        }
                    }
                }
            }

            items(
                items = courseList,
                key = { it.id }
            ) { course ->
                val color = Color(course.colorInt)
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { value ->
                        if (value == SwipeToDismissBoxValue.EndToStart) {
                            courseToDelete = course
                            showDeleteDialog = true
                            false
                        } else false
                    }
                )

                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromStartToEnd = false,
                    backgroundContent = {
                        val alpha by remember(dismissState) {
                            derivedStateOf {
                                if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                    (dismissState.progress * 2.5f).coerceIn(0f, 1f)
                                } else 0f
                            }
                        }

                        Box(
                            Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Red.copy(alpha = alpha * 0.8f))
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Sil",
                                tint = Color.White.copy(alpha = alpha)
                            )
                        }
                    }
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCourseClick(course.id) },
                        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
                    ) {
                        ListItem(
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            leadingContent = { Box(modifier = Modifier.width(6.dp).height(35.dp).background(color)) },
                            headlineContent = { Text(course.courseName, fontWeight = FontWeight.Bold) },
                            trailingContent = {
                                IconButton(onClick = {
                                    courseNameInput = course.courseName
                                    selectedColor = color
                                    editingCourseId = course.id
                                    isAddingNewCourse = false
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Düzenle", tint = Color.Gray)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}