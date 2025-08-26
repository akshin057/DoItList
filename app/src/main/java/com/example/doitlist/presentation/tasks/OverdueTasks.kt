package com.example.doitlist.presentation.tasks

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.doitlist.R
import com.example.doitlist.presentation.ui.theme.BackColor
import com.example.doitlist.presentation.ui.theme.TextColor
import androidx.compose.runtime.getValue
import com.example.doitlist.presentation.ui.TaskSwipeItem
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.runtime.rememberCoroutineScope
import com.example.doitlist.presentation.ui.Screen
import com.example.doitlist.presentation.ui.SectionHeader
import kotlinx.coroutines.launch


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OverdueTasksScreen(navController: NavController, vm: TasksViewModel) {

    val scope = rememberCoroutineScope()
    var snackbarHostState = remember { SnackbarHostState() }

    val sections by vm.overdueSections.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackColor,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {

                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.List,
                        tint = TextColor,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                    )
                }

                IconButton(onClick = {


                }) {
                    Icon(
                        painter = painterResource(R.drawable.bin),
                        tint = TextColor,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(25.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = {
                        navController.navigate(Screen.TaskScreen.route)
                    },
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        tint = TextColor,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp)
                    )
                }

            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            sections.forEach { section ->
                stickyHeader {
                    SectionHeader(date = section.date, onClick = {
                        vm.onRescheduleTasks(section.tasks)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Перенесено задач: ${section.tasks.size}"
                            )
                        }
                    })
                }
                items(
                    items = section.tasks,
                    key = { it.localId ?: it.hashCode().toLong() }
                ) { task ->
                    TaskSwipeItem(
                        task = task,
                        onDelete = { vm.onDeleteTask(it) },
                        onComplete = { vm.onCompleteTask(it) },
                        onClick = {

                        }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

        }
    }
}

