package com.example.digitallearndiary.ui.pages.courses

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.digitallearndiary.room.Tables.Course
import com.example.digitallearndiary.room.Tables.StudySession
import com.example.digitallearndiary.viewModels.SessionViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun SessionsTab(
    course: Course,
    viewModel: SessionViewModel = viewModel()
) {
    val themeColor = Color(course.colorInt)
    val courseName = course.courseName

    val sessions by viewModel.getSessionsByCourse(course.id).collectAsState(initial = emptyList())

    // UI & Timer State'leri
    var showHistory by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var isRunning by remember { mutableStateOf(false) }
    var totalSeconds by remember { mutableLongStateOf(25 * 60L) }
    var initialSelectedSeconds by remember { mutableLongStateOf(25 * 60L) }
    var startTimeMillis by remember { mutableLongStateOf(0L) }

    // Picker State'leri
    var selectedHour by remember { mutableIntStateOf(0) }
    var selectedMinute by remember { mutableIntStateOf(25) }
    var selectedSecond by remember { mutableIntStateOf(0) }

    // Kayıt Fonksiyonu (Hem buton hem otomatik bitiş için)
    val saveSession = {
        val elapsedSeconds = initialSelectedSeconds - totalSeconds
        if (elapsedSeconds > 0) {
            val session = StudySession(
                courseId = course.id,
                startTime = startTimeMillis,
                endTime = System.currentTimeMillis(),
                hour = (elapsedSeconds / 3600).toInt(),
                min = ((elapsedSeconds % 3600) / 60).toInt(),
                sec = (elapsedSeconds % 60).toInt()
            )
            viewModel.upsertSession(session)
        }
        // State'leri sıfırla
        isRunning = false
        totalSeconds = initialSelectedSeconds
    }

    // Timer Döngüsü ve Otomatik Bitiş Kontrolü
    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (totalSeconds > 0) {
                delay(1000L)
                totalSeconds -= 1
            }
            // --- SÜRE BİTTİĞİNDE BURASI ÇALIŞIR ---
            if (isRunning) { // Çift kontrol: Kullanıcı o an manuel durdurmamışsa
                saveSession()
            }
        }
    }

    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    val timerText = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Üst Bar (Başlık ve Geçmiş Butonu)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (showHistory) "$courseName Geçmişi" else "$courseName Seansı",
                style = MaterialTheme.typography.titleMedium, color = themeColor
            )
            IconButton(onClick = { showHistory = !showHistory }) {
                Icon(
                    imageVector = if (showHistory) Icons.Default.Timer else Icons.Default.History,
                    contentDescription = null, tint = themeColor
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (!showHistory) {
            // TIMER GÖRÜNÜMÜ
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(250.dp)
                    .clip(CircleShape)
                    .then(if (!isRunning) Modifier.clickable { showTimePicker = true } else Modifier)
                    .border(4.dp, themeColor.copy(alpha = 0.3f), CircleShape)
            ) {
                CircularProgressIndicator(
                    progress = { if (initialSelectedSeconds > 0) totalSeconds.toFloat() / initialSelectedSeconds else 0f },
                    modifier = Modifier.fillMaxSize(),
                    color = themeColor,
                    strokeWidth = 8.dp,
                    trackColor = themeColor.copy(alpha = 0.1f)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timerText,
                        style = MaterialTheme.typography.displayMedium,
                        color = themeColor,
                        fontWeight = FontWeight.Bold
                    )
                    if (!isRunning) {
                        Text(text = "Süreyi Ayarla", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Buton Grubu
            if (!isRunning && totalSeconds == initialSelectedSeconds) {
                Button(
                    onClick = {
                        isRunning = true
                        startTimeMillis = System.currentTimeMillis()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text("Odaklanmayı Başlat")
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    Button(
                        onClick = { isRunning = !isRunning },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isRunning) Color.Gray else themeColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isRunning) "Duraklat" else "Devam Et")
                    }

                    Button(
                        onClick = { saveSession() }, // Manuel Bitir
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Bitir", color = Color.White)
                    }
                }
            }
        } else {
            // GEÇMİŞ LİSTESİ
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(sessions, key = { it.id }) { session ->
                    SwipeableSessionItem(session, themeColor) { viewModel.deleteSession(session) }
                }
            }
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val total = (selectedHour * 3600L) + (selectedMinute * 60L) + selectedSecond
                    totalSeconds = total
                    initialSelectedSeconds = total
                    showTimePicker = false
                }) { Text("Tamam", color = themeColor) }
            },
            text = {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    PickerWheel(selectedHour, 0..23, "sa") { selectedHour = it }
                    PickerWheel(selectedMinute, 0..59, "dk") { selectedMinute = it }
                    PickerWheel(selectedSecond, 0..59, "sn") { selectedSecond = it }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableSessionItem(
    session: StudySession,
    themeColor: Color,
    onDelete: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    val dateFormatter = remember {
        SimpleDateFormat("dd MMMM yyyy, HH:mm", java.util.Locale("tr", "TR"))
    }
    val sessionDate = remember(session.startTime) {
        dateFormatter.format(Date(session.startTime))
    }

    // Süre Yazısı (Örn: 1 sa 25 dk 10 sn)
    val durationText = buildString {
        if (session.hour > 0) append("${session.hour} sa ")
        if (session.min > 0) append("${session.min} dk ")
        if (session.sec > 0 || (session.hour == 0 && session.min == 0)) append("${session.sec} sn")
    }.trim()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Seansı Sil") },
            text = { Text("Bu çalışma kaydını silmek istediğinize emin misiniz?") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDialog = false }) {
                    Text("Sil", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("İptal") }
            }
        )
    }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                showDialog = true
                false
            } else false
        }
    )

    // Dialog kapandığında kartın geri kaymasını sağlar
    LaunchedEffect(showDialog) {
        if (!showDialog && dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
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
                    .background(color, MaterialTheme.shapes.medium),
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
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = themeColor.copy(alpha = 0.05f)),
            border = BorderStroke(1.dp, themeColor.copy(alpha = 0.1f))
        ) {
            ListItem(
                headlineContent = {
                    Text(text = durationText, fontWeight = FontWeight.Bold)
                },
                supportingContent = {
                    Text(text = sessionDate, style = MaterialTheme.typography.bodySmall)
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = themeColor,
                        modifier = Modifier.size(24.dp)
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }
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