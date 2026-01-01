package com.example.digitallearndiary.ui

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.digitallearndiary.ui.pages.AuthProfileScreen
import com.example.digitallearndiary.ui.pages.courses.AddNoteScreen
import com.example.digitallearndiary.ui.pages.courses.CourseDetailScreen
import com.example.digitallearndiary.ui.pages.courses.CourseListScreen


@Composable
fun MainStudyApp() {
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
                0 -> CourseNavigationWrapper()
                1 -> AuthProfileScreen()
            }
        }
    }
}

@Composable
fun CourseNavigationWrapper() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            CourseListScreen(onCourseClick = { courseName, courseColor ->
                val colorValue = courseColor.toArgb().toLong()
                navController.navigate("detail/$courseName/$colorValue")
            })
        }
        composable(
            route = "detail/{courseName}/{courseColor}",
            arguments = listOf(
                navArgument("courseName") { type = NavType.StringType },
                navArgument("courseColor") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("courseName") ?: ""
            val colorVal = backStackEntry.arguments?.getLong("courseColor") ?: 0L

            CourseDetailScreen(
                courseName = name,
                courseColor = Color(colorVal.toInt()),
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "add_note/{courseName}/{courseColor}",
            arguments = listOf(
                navArgument("courseName") { type = NavType.StringType },
                navArgument("courseColor") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("courseName") ?: ""
            val colorVal = backStackEntry.arguments?.getLong("courseColor") ?: 0L

            AddNoteScreen(
                courseName = name,
                courseColor = Color(colorVal),
                onBack = { navController.popBackStack() }
            )
        }
    }
}

