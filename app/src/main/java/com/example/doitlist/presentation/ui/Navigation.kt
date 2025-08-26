package com.example.doitlist.presentation.ui

import android.R.attr.type
import android.graphics.Paint
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemColors
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.doitlist.R
import com.example.doitlist.SplashScreen
import com.example.doitlist.presentation.calendar.CalendarScreen
import com.example.doitlist.presentation.projects.ProjectViewModel
import com.example.doitlist.presentation.projects.ProjectsScreen
import com.example.doitlist.presentation.routine.RoutineScreen
import com.example.doitlist.presentation.routine.RoutinesViewModel
import com.example.doitlist.presentation.tasks.OverdueTasksScreen
import com.example.doitlist.presentation.tasks.TasksScreen
import com.example.doitlist.presentation.tasks.TasksViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.doitlist.presentation.projects.TaskForProjectScreen
import com.example.doitlist.presentation.ui.theme.BackColor
import com.example.doitlist.presentation.ui.theme.FabBackColor
import com.example.doitlist.presentation.ui.theme.TextColor
import com.example.doitlist.presentation.usersettings.UserSettingsScreen
import com.example.doitlist.presentation.usersettings.UserSettingsViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation() {
    val navController = rememberNavController()

    val projectViewModel: ProjectViewModel = hiltViewModel()
    val taskViewModel: TasksViewModel = hiltViewModel()
    val routineViewModel: RoutinesViewModel = hiltViewModel()
    val userSettingsViewModel: UserSettingsViewModel = hiltViewModel()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val actions = remember {
        object : DrawerActions {
            override fun open() {
                scope.launch { drawerState.open() }
            }

            override fun close() {
                scope.launch { drawerState.close() }
            }
        }
    }

    val backstack by navController.currentBackStackEntryAsState()
    val currentRoute = backstack?.destination?.route

    CompositionLocalProvider(LocalDrawerActions provides actions) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = BackColor
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(30.dp)
                    ) {
                        Image(
                            painterResource(R.drawable.user_logo),
                            null,
                            modifier = Modifier.size(60.dp)
                        )
                        Text(
                            "Логин",
                            fontSize = 24.sp,
                            color = White,
                            modifier = Modifier.offset(y = 15.dp)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        DrawerItem(
                            label = "Настройки",
                            selected = currentRoute == Screen.TaskScreen.route,
                            icon = { Icon(Icons.Default.Settings, null, tint = TextColor) }
                        ) {

                        }
                        DrawerItem(
                            label = "Настройки пользователя",
                            selected = currentRoute == Screen.TaskScreen.route,
                            icon = { Icon(Icons.Default.Person, null, tint = TextColor) }
                        ) {
                            navController.navigate(Screen.UserSettingsScreen.route)
                            actions.close()
                        }
                        DrawerItem(
                            label = "FAQ",
                            selected = currentRoute == Screen.TaskScreen.route,
                            icon = { Icon(Icons.Default.Info, null, tint = TextColor) }
                        ) {

                        }
                        DrawerItem(
                            label = "Выйти",
                            selected = currentRoute == Screen.TaskScreen.route,
                            icon = {
                                Icon(
                                    Icons.AutoMirrored.Default.Logout,
                                    null,
                                    tint = TextColor
                                )
                            }
                        ) {

                        }

                    }

                }
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.TaskScreen.route
            ) {
                composable(Screen.SplashScreen.route) { SplashScreen(navController) }
                composable(Screen.TaskScreen.route) {
                    TasksScreen(navController, vm = taskViewModel, projectVm = projectViewModel)
                }
                composable(Screen.ProjectScreen.route) {
                    ProjectsScreen(navController, vm = projectViewModel, taskVm = taskViewModel)
                }
                composable(Screen.RoutineScreen.route) {
                    RoutineScreen(navController, routineViewModel)
                }
                composable(Screen.CalendarScreen.route) {
                    CalendarScreen(navController, taskViewModel, projectViewModel)
                }
                composable(Screen.OverdueTasksScreen.route) {
                    OverdueTasksScreen(navController, taskViewModel)
                }
                composable(Screen.UserSettingsScreen.route) {
                    UserSettingsScreen(navController, userSettingsViewModel)
                }
                composable(
                    route = Screen.TaskForProjectScreen.route,
                    arguments = listOf(navArgument("projectId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val projectId = backStackEntry.arguments!!.getLong("projectId")
                    TaskForProjectScreen(
                        navController = navController,
                        vm = taskViewModel,
                        projectVm = projectViewModel,
                        projectId = projectId
                    )
                }
            }
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
    object RoutineScreen : Screen("routine_screen", R.drawable.routine)
    object OverdueTasksScreen : Screen("overdue_task_screen", R.drawable.todo)
    object UserSettingsScreen : Screen("user_settings_screen", R.drawable.user_logo)
    object TaskForProjectScreen : Screen(
        route = "project_tasks/{projectId}",
        iconRes = R.drawable.folder_filled
    ) {
        fun route(projectId: Long) = "project_tasks/$projectId"
    }
}

@Composable
fun DrawerItem(
    label: String,
    selected: Boolean,
    icon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    GlowingCard {
        NavigationDrawerItem(
            label = {
                Text(
                    label,
                    color = White,
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )
            },
            selected = selected,
            onClick = onClick,
            icon = icon,
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = BackColor,
                selectedContainerColor = FabBackColor
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }


}

interface DrawerActions {
    fun open();
    fun close()
}

val LocalDrawerActions = staticCompositionLocalOf<DrawerActions> {
    error("DrawerActions not provided")
}