package com.example.digitallearndiary.ui.pages.courses

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.digitallearndiary.cloud.VisionService
import com.example.digitallearndiary.room.Tables.Course
import com.example.digitallearndiary.room.Tables.Note
import com.example.digitallearndiary.viewModels.NoteViewModel
import com.google.android.play.integrity.internal.f
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    course: Course, // Artık nesneyi direkt alıyoruz
    editingNote: Note? = null,
    onBack: () -> Unit,
    viewModel: NoteViewModel = viewModel()
) {
    // Nesneden renk ve isim değerlerini ayıklıyoruz
    val courseColor = Color(course.colorInt)
    val courseName = course.courseName

    var noteTitle by remember(editingNote) {
        mutableStateOf(editingNote?.title ?: "")
    }
    var noteContent by remember(editingNote) {
        mutableStateOf(editingNote?.content ?: "")
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            // 2. Geçici erişim izni alma (Gerekiyorsa bayraklarla yönetilir)
            // SAF'ta seçilen URI üzerinde okuma yetkisi otomatik verilir.

            scope.launch(Dispatchers.IO) { // Ağır işlemi IO thread'ine alıyoruz
                try {
                    // 3. VisionService içinde ContentResolver kullanarak işleme yapın
                    val resultText = VisionService.resmiAnalizEt(context, selectedUri)

                    withContext(Dispatchers.Main) {
                        noteContent += "\n$resultText"
                    }
                } catch (e: Exception) {
                    // Hata yönetimi (Okuma izni veya dosya bulunamama durumu)
                    e.printStackTrace()
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                        Text(
                            text = courseName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = courseColor,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = if (editingNote != null) "Düzenle" else "Yeni Not",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri", tint = courseColor)
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            if(noteTitle.isNotBlank() || noteContent.isNotBlank()) {
                                if (editingNote != null) {
                                    // GÜNCELLEME (Update)
                                    viewModel.upsertNote(
                                        editingNote.copy(
                                            title = noteTitle,
                                            content = noteContent,
                                            // createdTime'ı koruyabilir veya güncelleyebilirsiniz
                                        )
                                    )
                                } else {
                                    // YENİ KAYIT (Insert)
                                    viewModel.upsertNote(
                                        Note(
                                            title = noteTitle,
                                            content = noteContent,
                                            courseId = course.id,
                                            createdTime = System.currentTimeMillis()
                                        )
                                    )
                                }
                                onBack() // Kayıt sonrası geri dön
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = courseColor.copy(alpha = 0.1f),
                            contentColor = courseColor
                        ),
                        elevation = null,
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("KAYDET", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { galleryLauncher.launch(arrayOf("image/*")) },
                containerColor = courseColor,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.ImageSearch, contentDescription = null) },
                text = { Text("Resimden Aktar", fontWeight = FontWeight.Medium) },
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            TextField(
                value = noteTitle,
                onValueChange = { noteTitle = it },
                placeholder = {
                    Text("Not Başlığı",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color.Gray.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = courseColor,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 0.5.dp,
                color = Color.LightGray.copy(alpha = 0.3f)
            )

            TextField(
                value = noteContent,
                onValueChange = { noteContent = it },
                placeholder = { Text("Notunuzu buraya detaylandırın...", color = Color.Gray) },
                modifier = Modifier.fillMaxSize(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp)
            )
        }
    }
}