package com.example.digitallearndiary.ui.pages.courses

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun TasksTab(courseName: String, themeColor: Color) {
    val tasks = remember {
        mutableStateListOf(
            TaskItem("Vize projesini bitir", false),
            TaskItem("Haftalık okumaları yap", false),
            TaskItem("Ödev 1 teslimi", true)
        )
    }

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
                                tasks.add(0, TaskItem(newTaskText, false))
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
                val activeTasks = tasks.filter { !it.isDone }
                items(activeTasks) { task ->
                    TaskRow(task = task, themeColor = themeColor, onCheckedChange = {
                        val index = tasks.indexOf(task)
                        if (index != -1) tasks[index] = task.copy(isDone = it)
                    })
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val completedTasks = tasks.filter { it.isDone }
                if (completedTasks.isEmpty()) {
                    item { Text("Henüz tamamlanmış görev yok.", color = Color.Gray) }
                }
                items(completedTasks) { task ->
                    TaskRow(task = task, themeColor = themeColor, onCheckedChange = {
                        val index = tasks.indexOf(task)
                        if (index != -1) tasks[index] = task.copy(isDone = it)
                    })
                }
            }
        }
    }
}

@Composable
fun TaskRow(task: TaskItem, themeColor: Color, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isDone) Color.Transparent else MaterialTheme.colorScheme.surface
        ),
        border = if (task.isDone) null else BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = themeColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                color = if (task.isDone) Color.Gray else Color.Unspecified
            )
        }
    }
}

data class TaskItem(val title: String, val isDone: Boolean)