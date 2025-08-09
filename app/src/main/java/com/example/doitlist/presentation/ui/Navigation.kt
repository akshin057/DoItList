package com.example.doitlist.presentation.ui

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doitlist.R
import com.example.doitlist.SplashScreen
import com.example.doitlist.presentation.calendar.CalendarScreen
import com.example.doitlist.presentation.projects.ProjectViewModel
import com.example.doitlist.presentation.projects.ProjectsScreen
import com.example.doitlist.presentation.routine.RoutineScreen
import com.example.doitlist.presentation.routine.RoutinesViewModel
import com.example.doitlist.presentation.tasks.TasksScreen
import com.example.doitlist.presentation.tasks.TasksViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()

    val projectViewModel: ProjectViewModel = hiltViewModel()
    val taskViewModel: TasksViewModel = hiltViewModel()
    val routineViewModel: RoutinesViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route
    ) {

        composable(route = Screen.SplashScreen.route) {
            SplashScreen(navController)
        }

        composable(route = Screen.TaskScreen.route) {
            TasksScreen(navController, vm = taskViewModel, projectVm = projectViewModel)
        }

        composable(route = Screen.ProjectScreen.route) {
            ProjectsScreen(navController, vm = projectViewModel, taskVm = taskViewModel)
        }

        composable(route = Screen.RoutineScreen.route) {
            RoutineScreen(navController, routineViewModel)
        }

        composable(route = Screen.CalendarScreen.route) {
            CalendarScreen(navController, taskViewModel)
        }
    }

}

sealed class Screen(
    val route: String,
    @DrawableRes val iconRes: Int,
    val label: String? = null
) {
    object SplashScreen : Screen("splash_screen", R.drawable.ic_launcher_background)
    object TaskScreen : Screen("task_screen", iconRes = R.drawable.home, "Задачи")
    object ProjectScreen : Screen("project_screen", iconRes = R.drawable.folder_filled, "Проекты")
    object CalendarScreen : Screen("calendar_screen", iconRes = R.drawable.calendar, "Календарь")
    object ChatScreen : Screen("chat_screen", iconRes = R.drawable.comment, "ИИ")
    object RoutineScreen : Screen("routine_screen", R.drawable.ic_launcher_background)
}