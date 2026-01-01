package com.example.digitallearndiary.ui.pages.courses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NotesTab(courseName: String, themeColor: Color, onAddNoteClick: () -> Unit) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNoteClick,
                containerColor = themeColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Not Ekle")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text(
                text = "$courseName Notları",
                style = MaterialTheme.typography.titleMedium,
                color = themeColor
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Bu derse ait henüz bir not bulunmuyor.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}