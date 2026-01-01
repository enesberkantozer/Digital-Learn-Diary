package com.example.digitallearndiary.ui.pages.courses

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SessionsTab(courseName: String, themeColor: Color) {
    var showHistory by remember { mutableStateOf(false) }
    var remainingTime by remember { mutableStateOf("00:00:00") }

    var showTimePicker by remember { mutableStateOf(false) }

    var selectedHour by remember { mutableIntStateOf(0) }
    var selectedMinute by remember { mutableIntStateOf(25) }
    var selectedSecond by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$courseName Seansı",
                style = MaterialTheme.typography.titleMedium,
                color = themeColor
            )
            IconButton(onClick = { showHistory = !showHistory }) {
                Icon(
                    imageVector = if (showHistory) Icons.Default.Timer else Icons.Default.History,
                    contentDescription = "Geçmiş",
                    tint = themeColor
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (!showHistory) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(250.dp)
                    .clip(CircleShape)
                    .clickable { showTimePicker = true }
                    .border(4.dp, themeColor.copy(alpha = 0.3f), CircleShape)
            ) {
                CircularProgressIndicator(
                    progress = 0.0f,
                    modifier = Modifier.fillMaxSize(),
                    color = themeColor,
                    strokeWidth = 8.dp
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = remainingTime,
                        style = MaterialTheme.typography.displayMedium,
                        color = themeColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Süreyi Ayarla",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {  },
                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text("Odaklanmayı Başlat")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Text("Son Çalışmalar", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(5) { index ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = themeColor.copy(alpha = 0.05f))
                    ) {
                        ListItem(
                            headlineContent = { Text("Seans #${5 - index}") },
                            supportingContent = { Text("Süre: 25dk • 29 Aralık") },
                            leadingContent = { Icon(
                                imageVector = Icons.Default.CheckCircle,
                                tint = themeColor,
                                modifier = Modifier.size(16.dp),
                                contentDescription = ""
                            ) }
                        )
                    }
                }
            }
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = {
                Text("Süre Ayarla", style = MaterialTheme.typography.titleMedium, color = themeColor)
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PickerWheel(value = selectedHour, range = 0..23, label = "sa", onValueChange = { selectedHour = it })
                    PickerWheel(value = selectedMinute, range = 0..59, label = "dk", onValueChange = { selectedMinute = it })
                    PickerWheel(value = selectedSecond, range = 0..59, label = "sn", onValueChange = { selectedSecond = it })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    remainingTime = String.format("%02d:%02d:%02d", selectedHour, selectedMinute, selectedSecond)
                    showTimePicker = false
                }) {
                    Text("Tamam", color = themeColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("İptal", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun PickerWheel(
    value: Int,
    range: IntRange,
    label: String,
    onValueChange: (Int) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.BLACK

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        androidx.compose.ui.viewinterop.AndroidView(
            factory = { context ->
                android.widget.NumberPicker(context).apply {
                    minValue = range.first
                    maxValue = range.last
                    this.value = value

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        this.textColor = textColor
                    }

                    setOnValueChangedListener { _, _, newVal ->
                        onValueChange(newVal)
                    }
                }
            },
            update = { picker ->
                picker.value = value
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    picker.textColor = textColor
                }
            },
            modifier = Modifier.width(45.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isDark) Color.LightGray else Color.Gray
        )
    }
}