package com.example.doitlist.presentation.usersettings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.shape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doitlist.R
import com.example.doitlist.presentation.ui.GlowingCard
import com.example.doitlist.presentation.ui.GlowingField
import com.example.doitlist.presentation.ui.LocalDrawerActions
import com.example.doitlist.presentation.ui.NeonFab
import com.example.doitlist.presentation.ui.Screen
import com.example.doitlist.presentation.ui.theme.BackColor
import com.example.doitlist.presentation.ui.theme.BorderColor
import com.example.doitlist.presentation.ui.theme.NeonColor
import com.example.doitlist.presentation.ui.theme.TextColor
import com.example.doitlist.utils.UserUiState
import com.example.doitlist.utils.checkEmail
import com.example.doitlist.utils.checkPassword
import kotlinx.coroutines.launch

@Composable
fun UserSettingsScreen(navController: NavController, vm: UserSettingsViewModel) {

    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    val state = vm.uiState

    var login by rememberSaveable { mutableStateOf("") }
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passShown by rememberSaveable { mutableStateOf(false) }

    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassErr by remember { mutableStateOf<String?>(null) }

    var loginErr by remember { mutableStateOf<String?>(null) }
    var emailErr by remember { mutableStateOf<String?>(null) }
    var passErr by remember { mutableStateOf<String?>(null) }

    var originalUser by remember { mutableStateOf<com.example.doitlist.data.remote.dto.UserDTO?>(null) }

    val tfColors = TextFieldDefaults.colors(
        unfocusedContainerColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        cursorColor = NeonColor
    )

    LaunchedEffect(state) {
        when (state) {
            is UserUiState.Success -> {
                val u = state.user
                originalUser = u
                login = u.login
                email = u.email
                fullName = listOfNotNull(u.surname, u.name, u.lastName)
                    .filter { it.isNotBlank() }
                    .joinToString(" ")

            }

            is UserUiState.Error -> {
                snackbarHostState.showSnackbar(state.message ?: "Ошибка загрузки профиля")
            }

            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val drawer = LocalDrawerActions.current
                IconButton(onClick = {
                    drawer.open()
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.List,
                        tint = TextColor,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
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
        floatingActionButton = {
            NeonFab(
                onClick = {
                    loginErr = null; emailErr = null; passErr = null; confirmPassErr = null

                    val original = originalUser
                    if (original == null) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Данные пользователя ещё не загружены")
                        }
                        return@NeonFab
                    }

                    val emailOk = checkEmail(email)
                    if (!emailOk) emailErr = "Некорректный e-mail"

                    val passwordEntered = password.isNotBlank() || confirmPassword.isNotBlank()
                    val passOk = if (passwordEntered) checkPassword(password) else true
                    if (passwordEntered && !passOk) passErr = "Слишком простой пароль"

                    val match = if (passwordEntered) password == confirmPassword else true
                    if (!match) {
                        passErr = passErr ?: "Пароли не совпадают"
                        confirmPassErr = "Пароли не совпадают"
                    }

                    val parts = fullName.trim().split(Regex("\\s+"))
                    val newSurname = parts.getOrNull(0).orEmpty()
                    val newName = parts.getOrNull(1).orEmpty()
                    val newLastName = parts.getOrNull(2).orEmpty()

                    val emailChanged = email != original.email
                    val loginChanged = login != original.login
                    val nameChanged =
                        newName != (original.name ?: "") ||
                                newSurname != (original.surname ?: "") ||
                                newLastName != (original.lastName ?: "")
                    val passwordChanged = passwordEntered && passOk && match

                    val hasChanges = emailChanged || loginChanged || nameChanged || passwordChanged

                    if (!hasChanges) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Нет изменений")
                        }
                        return@NeonFab
                    }

                    if (!emailOk || !match || (passwordEntered && !passOk)) {
                        return@NeonFab
                    }

                    if (loginChanged) vm.changeLogin(login)
                    if (emailChanged) vm.changeEmail(email)
                    if (nameChanged) vm.changeName(
                        newSurname = newSurname,
                        newName = newName,
                        newLastName = newLastName
                    )
                    if (passwordChanged) vm.changePassword(password)
                },
                image = Icons.Default.Edit
            )

        },
        containerColor = BackColor,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = TextColor,
                modifier = Modifier.size(140.dp)
            )

            Text(
                "Настройки пользователя",
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(250.dp)
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                thickness = 3.dp,
                color = TextColor
            )

            Spacer(Modifier.height(20.dp))

            Column {

                GlowingField(
                    value = login,
                    onChange = { login = it; loginErr = null },
                    label = "Логин",
                    isError = loginErr != null,
                    supporting = loginErr
                )

                Spacer(Modifier.height(20.dp))
                GlowingField(
                    value = fullName,
                    onChange = { fullName = it },
                    label = "ФИО",
                )

                Spacer(Modifier.height(20.dp))
                GlowingField(
                    value = email,
                    onChange = { email = it; emailErr = null },
                    label = "E-mail",
                    isError = emailErr != null,
                    supporting = emailErr
                )

                Spacer(Modifier.height(20.dp))

                GlowingCard(
                    modifier = Modifier
                        .height(60.dp)
                        .width(300.dp),
                    glowingColor = NeonColor,
                    containerColor = BackColor,
                    cornerRadius = 12.dp,
                    borderColor = if (passErr == null) BorderColor else Color.Red
                ) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; passErr = null },
                        label = { Text("Пароль", color = Color.White, fontSize = 16.sp) },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
                        modifier = Modifier.fillMaxSize(),
                        visualTransformation =
                            if (passShown) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon =
                                if (passShown) Icons.Default.VisibilityOff else Icons.Default.Visibility
                            IconButton({ passShown = !passShown }) {
                                Icon(icon, null, tint = Color.White)
                            }
                        },
                        colors = tfColors,
                        shape = shape,
                        isError = passErr != null,
                        supportingText = { passErr?.let { Text(it, color = Color.Red, fontSize = 12.sp) } }
                    )
                }

                Spacer(Modifier.height(20.dp))

                GlowingCard(
                    modifier = Modifier
                        .height(60.dp)
                        .width(300.dp),
                    glowingColor = NeonColor,
                    containerColor = BackColor,
                    cornerRadius = 12.dp,
                    borderColor = if (passErr == null) BorderColor else Color.Red
                ) {
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; confirmPassErr = null; passErr = null },
                        label = { Text("Повторить пароль", color = Color.White, fontSize = 16.sp) },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
                        modifier = Modifier.fillMaxSize(),
                        visualTransformation =
                            if (passShown) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon =
                                if (passShown) Icons.Default.VisibilityOff else Icons.Default.Visibility
                            IconButton({ passShown = !passShown }) {
                                Icon(icon, null, tint = Color.White)
                            }
                        },
                        colors = tfColors,
                        shape = shape,
                        isError = confirmPassErr != null,
                        supportingText = { confirmPassErr?.let { Text(it, color = Color.Red, fontSize = 12.sp) } }
                    )
                }
            }
        }
    }
}