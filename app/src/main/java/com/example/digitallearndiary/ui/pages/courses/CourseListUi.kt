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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CourseListScreen(onCourseClick: (String, Color) -> Unit) {
    val mockCourses = remember {
        mutableStateListOf(
            "Mobil Programlama" to Color.Blue,
            "Veri Yapıları" to Color.Green
        )
    }

    var isAddingNewCourse by remember { mutableStateOf(false) }
    var editingCourseIndex by remember { mutableStateOf<Int?>(null) }

    var courseNameInput by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color.Blue) }

    val resetForm = {
        courseNameInput = ""
        selectedColor = Color.Blue
        isAddingNewCourse = false
        editingCourseIndex = null
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var indexToDelete by remember { mutableStateOf<Int?>(null) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Dersi Sil") },
            text = { Text("Bu dersi silmek istediğinize emin misiniz? Bu işlem geri alınamaz.") },
            confirmButton = {
                TextButton(onClick = {
                    indexToDelete?.let { mockCourses.removeAt(it) }
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

            if (isAddingNewCourse || editingCourseIndex != null) {
                item {
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (editingCourseIndex != null) "Dersi Düzenle" else "Yeni Ders Ekle",
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
                                Button(onClick = {
                                    if (courseNameInput.isNotEmpty()) {
                                        val index = editingCourseIndex
                                        if (index != null) {
                                            mockCourses[index] = courseNameInput to selectedColor
                                        } else {
                                            mockCourses.add(0, courseNameInput to selectedColor)
                                        }
                                        resetForm()
                                    }
                                }) {
                                    Text(if (editingCourseIndex != null) "Güncelle" else "Ekle")
                                }
                            }
                        }
                    }
                }
            }

            itemsIndexed(
                items = mockCourses,
                key = { _, item -> item.first + item.hashCode() }
            ) { index, (name, color) ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { value ->
                        if (value == SwipeToDismissBoxValue.EndToStart) {
                            indexToDelete = index
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
                            .clickable { onCourseClick(name, color) },
                        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
                    ) {
                        ListItem(
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            leadingContent = { Box(modifier = Modifier.width(6.dp).height(35.dp).background(color)) },
                            headlineContent = { Text(name, fontWeight = FontWeight.Bold) },
                            trailingContent = {
                                IconButton(onClick = {
                                    courseNameInput = name
                                    selectedColor = color
                                    editingCourseIndex = index
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