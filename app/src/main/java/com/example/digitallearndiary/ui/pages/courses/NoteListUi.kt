package com.example.digitallearndiary.ui.pages.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.digitallearndiary.room.Tables.Course
import com.example.digitallearndiary.room.Tables.Note
import com.example.digitallearndiary.viewModels.NoteViewModel

@Composable
fun NotesTab(
    course: Course,
    onAddNoteClick: () -> Unit,
    onNoteClick: (Note) -> Unit, // Düzenleme için nota tıklama callback'i
    viewModel: NoteViewModel = viewModel()
) {
    val themeColor = Color(course.colorInt)
    val courseName = course.courseName

    // Notları kurs ID'sine göre dinliyoruz
    val notes by viewModel.getNotesByCourse(course.id)!!.collectAsState(initial = emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNoteClick,
                containerColor = themeColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Not Ekle")
            }
        },
        // Arka planı ana sayfayla uyumlu yapmak için transparent kullanabiliriz
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "$courseName Notları",
                style = MaterialTheme.typography.titleMedium,
                color = themeColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (notes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Bu derse ait henüz bir not bulunmuyor.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp) // FAB'ın üstünü kapatmaması için
                ) {
                    items(notes, key = { it.id }) { note ->
                        SwipeableNoteItem(
                            note = note,
                            onDelete = { viewModel.deleteNote(note) }, // Room'dan silme
                            onClick = { onNoteClick(note) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoteItemCard(note: Note, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.title.ifBlank { "Başlıksız Not" },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (note.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableNoteItem(
    note: Note,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    // Onay Diyaloğu
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Notu Sil") },
            text = { Text("\"${note.title.ifBlank { "Başlıksız Not" }}\" silinecek. Emin misiniz?") },
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
                showDialog = true // Silmek yerine diyaloğu göster
                false // Kartın hemen kaybolmasını engelle (onay bekliyoruz)
            } else {
                false
            }
        }
    )

    // Kart geri çekildiğinde (İptal dendiğinde) kartı eski konumuna getir
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
        NoteItemCard(note = note, onClick = onClick)
    }
}