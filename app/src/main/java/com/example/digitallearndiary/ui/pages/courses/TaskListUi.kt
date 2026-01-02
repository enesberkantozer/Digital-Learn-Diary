package com.example.digitallearndiary.ui.pages.courses

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.digitallearndiary.room.Tables.Course
import com.example.digitallearndiary.room.Tables.Task
import com.example.digitallearndiary.viewModels.TaskViewModel
import com.google.android.play.integrity.internal.ac

@Composable
fun TasksTab(
    course: Course,
    viewModel: TaskViewModel = viewModel()
) { // Parametre olarak artık nesnenin tamamı geliyor
    // Renk Int tipinde olduğu için tekrar Compose Color'a çeviriyoruz
    val themeColor = Color(course.colorInt)
    val courseName = course.courseName

    // Veritabanındaki görevleri kurs id'sine göre asenkron olarak dinliyoruz
    val tasks by viewModel.getTasksByCourse(course.id)!!.collectAsState(initial = emptyList())

    var showCompleted by remember { mutableStateOf(false) }
    var newTaskText by remember { mutableStateOf("") }
    var isAddingTask by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (showCompleted) "$courseName Tamamlananlar" else "$courseName Görevleri",
                style = MaterialTheme.typography.titleMedium,
                color = themeColor
            )
            IconButton(onClick = { showCompleted = !showCompleted }) {
                Icon(
                    imageVector = if (showCompleted) Icons.Default.List else Icons.Default.History,
                    contentDescription = "Geçmiş",
                    tint = themeColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!showCompleted) {
            if (isAddingTask) {
                OutlinedTextField(
                    value = newTaskText,
                    onValueChange = { newTaskText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Görev adını yazın...") },
                    trailingIcon = {
                        IconButton(onClick = {
                            if (newTaskText.isNotBlank()) {
                                // YENİ GÖREV EKLEME
                                viewModel.upsertTask(
                                    Task(
                                        taskDesc = newTaskText,
                                        isCompleted = false,
                                        courseId = course.id, // Kurs bağlantısı
                                        createdTime = System.currentTimeMillis()
                                    )
                                )
                                newTaskText = ""
                                isAddingTask = false
                            }
                        }) { Icon(Icons.Default.Check, null, tint = themeColor) }
                    },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = themeColor)
                )
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isAddingTask = true },
                    colors = CardDefaults.cardColors(containerColor = themeColor.copy(alpha = 0.1f))
                ) {
                    Text(
                        "+ Yeni görev eklemek için tıkla",
                        modifier = Modifier.padding(16.dp),
                        color = themeColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val activeTasks = tasks.filter { !it.isCompleted }

                if (activeTasks.isEmpty()) {
                    item { Text("Aktif görev yok.", color = Color.Gray) }
                }

                // BURASI: key kullanımı ve Wrapper çağrısı
                items(activeTasks, key = { it.id }) { task ->
                    SwipeableTaskItem(
                        task = task,
                        themeColor = themeColor,
                        onDelete = { viewModel.deleteTask(task) },
                        onCheckedChange = { isChecked ->
                            viewModel.upsertTask(task.copy(isCompleted = isChecked))
                        }
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val completedTasks = tasks.filter { it.isCompleted }

                if (completedTasks.isEmpty()) {
                    item { Text("Henüz tamamlanmış görev yok.", color = Color.Gray) }
                }

                // BURASI: key kullanımı ve Wrapper çağrısı
                items(completedTasks, key = { it.id }) { task ->
                    SwipeableTaskItem(
                        task = task,
                        themeColor = themeColor,
                        onDelete = { viewModel.deleteTask(task) },
                        onCheckedChange = { isChecked ->
                            viewModel.upsertTask(task.copy(isCompleted = isChecked))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTaskItem(
    task: Task,
    themeColor: Color,
    onDelete: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    // Diyalog görünürlüğünü kontrol eden state
    var showDialog by remember { mutableStateOf(false) }

    // Silme Onay Diyaloğu
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Görevi Sil") },
            text = { Text("\"${task.taskDesc}\" silinecek. Emin misiniz?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDialog = false
                }) {
                    Text("Sil", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                showDialog = true // Kaydırma bittiğinde diyaloğu aç
                false // Kartın hemen silinmesini engelle (onay bekliyoruz)
            } else {
                false
            }
        }
    )

    // Eğer diyalog kapatılırsa (İptal dendiğinde), kartı eski konumuna geri çek
    LaunchedEffect(showDialog) {
        if (!showDialog) {
            dismissState.reset()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
                Color.Red.copy(alpha = 0.8f) else Color.Transparent

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 4.dp)
                    .background(color, shape = MaterialTheme.shapes.medium),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Sil",
                    tint = Color.White,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    ) {
        TaskRow(task = task, themeColor = themeColor, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun TaskRow(task: Task, themeColor: Color, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            // Arka plan artık her zaman MaterialTheme yüzey rengi olacak
            containerColor = MaterialTheme.colorScheme.surface
        ),
        // Kenarlık artık her zaman görünecek
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = themeColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = task.taskDesc,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                color = if (task.isCompleted) Color.Gray else Color.Unspecified
            )
        }
    }
}