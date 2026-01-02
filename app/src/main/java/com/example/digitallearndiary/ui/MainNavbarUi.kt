package com.example.digitallearndiary.ui

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.digitallearndiary.ui.pages.AuthProfileScreen
import com.example.digitallearndiary.ui.pages.courses.AddNoteScreen
import com.example.digitallearndiary.ui.pages.courses.CourseDetailScreen
import com.example.digitallearndiary.ui.pages.courses.CourseListScreen
import com.example.digitallearndiary.viewModels.CourseViewModel
import com.example.digitallearndiary.viewModels.NoteViewModel


@Composable
fun MainStudyApp() {
    // Android 13+ Bildirim İzni İsteme
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            // Kullanıcı izin vermezse uyarı gösterilebilir
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }


    val courseViewModel: CourseViewModel = viewModel()
    val noteViewModel: NoteViewModel = viewModel()

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.List, "Dersler") },
                    label = { Text("Dersler") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Login, "Giriş") },
                    label = { Text("Profil") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> CourseNavigationWrapper(courseViewModel, noteViewModel)
                1 -> AuthProfileScreen()
            }
        }
    }
}

@Composable
fun CourseNavigationWrapper(courseViewModel: CourseViewModel, noteViewModel: NoteViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            CourseListScreen(
                viewModel = courseViewModel,
                onCourseClick = { courseId ->
                    navController.navigate("detail/$courseId")
                }
            )
        }

        composable(
            route = "detail/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("courseId") ?: ""

            // ÖNEMLİ KISIM: ViewModel'deki listeden bu ID'ye sahip nesneyi buluyoruz
            // collectAsState ile listeyi alıyoruz
            val courses by courseViewModel.courses.collectAsState(initial = emptyList())
            val selectedCourse = courses.find { it.id == id }

            // Eğer ders bulunduysa ekranı çiz ve nesneyi gönder
            selectedCourse?.let { course ->
                CourseDetailScreen(
                    course = course, // Artık parametre olarak nesne gidiyor!
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = "add_note/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("courseId") ?: ""
            val courses by courseViewModel.courses.collectAsState(initial = emptyList())
            val selectedCourse = courses.find { it.id == id }

            selectedCourse?.let { course ->
                AddNoteScreen(
                    course = course, // Burada da nesne gidiyor
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = "add_note/{courseId}?noteId={noteId}",
            arguments = listOf(
                navArgument("courseId") { type = NavType.StringType },
                navArgument("noteId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            val noteId = backStackEntry.arguments?.getString("noteId")

            // courseId ile ilgili kurs nesnesini al
            val course by courseViewModel.getWithId(courseId)!!.collectAsState(initial = null)

            // Eğer noteId varsa veritabanından o notu bul
            val editingNote by noteViewModel.getNoteById(noteId!!)!!.collectAsState(initial = null)

            if (course != null) {
                AddNoteScreen(
                    course = course!!,
                    editingNote = editingNote,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

