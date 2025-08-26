package com.example.doitlist.presentation.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.shape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doitlist.R
import com.example.doitlist.presentation.ui.theme.BackColor
import com.example.doitlist.presentation.ui.theme.BorderColor
import com.example.doitlist.presentation.ui.GlowingCard
import com.example.doitlist.presentation.ui.theme.NeonColor
import com.example.doitlist.presentation.ui.theme.TextColor
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.doitlist.presentation.ui.GlowingField
import com.example.doitlist.utils.UiState
import com.example.doitlist.utils.checkEmail
import com.example.doitlist.utils.checkPassword


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}

@Composable
fun RegisterScreen(
    onSwitch: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    var login by rememberSaveable { mutableStateOf("") }
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passShown by rememberSaveable { mutableStateOf(false) }

    var loginErr by remember { mutableStateOf<String?>(null) }
    var emailErr by remember { mutableStateOf<String?>(null) }
    var passErr by remember { mutableStateOf<String?>(null) }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val uiState = viewModel.uiState

    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> onSuccess()
            is UiState.Error -> {
                val errorMessage = uiState.throwable.localizedMessage ?: "Ошибка регистрации"

                when {
                    "login" in errorMessage.lowercase() ->
                        snackbar.showSnackbar("Логин уже занят")
                            .also { loginErr = "Логин уже занят" }

                    "email" in errorMessage.lowercase() ->
                        snackbar.showSnackbar("E-mail уже занят")
                            .also { emailErr = "E-mail уже занят" }

                    "пароль" in errorMessage.lowercase() || "password" in errorMessage.lowercase() ->
                        snackbar.showSnackbar("Некорректный пароль")
                            .also { passErr = "Некорректный пароль" }

                    else -> snackbar.showSnackbar(errorMessage)
                }
            }

            else -> {

            }
        }
    }

    val tfColors = TextFieldDefaults.colors(
        unfocusedContainerColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        cursorColor = NeonColor
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(BackColor),
        contentAlignment = Alignment.Center
    ) {

        Column(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text("Добро пожаловать в Do It", color = Color.White, fontSize = 20.sp)
            Spacer(Modifier.height(40.dp))

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
                    isError = passErr != null
                )
            }
            if (passErr != null)
                Text(passErr!!, color = Color.Red, fontSize = 12.sp)

            Spacer(Modifier.height(20.dp))
            Text(
                "Уже есть аккаунт",
                color = TextColor,
                fontSize = 18.sp,
                modifier = Modifier.clickable(onClick = onSwitch)
            )

            /* -------- кнопка -------- */
            Spacer(Modifier.height(30.dp))
            GlowingCard(
                modifier = Modifier
                    .height(60.dp)
                    .width(270.dp),
                glowingColor = NeonColor,
                containerColor = BackColor,
                cornerRadius = 12.dp,
                borderColor = BorderColor
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .clickable(enabled = uiState !is UiState.Loading) {
                            val emOk = checkEmail(email).also {
                                emailErr = if (it) null else "Невалидный e-mail"
                            }
                            val pwOk = checkPassword(password).also {
                                passErr = if (it) null else
                                    "Мин. 8 симв., A-z + a-z + цифра"
                            }

                            if (emOk && pwOk) {
                                val parts = fullName.trim().split(Regex("\\s+"))
                                val sur = parts.getOrNull(0).orEmpty()
                                val nm = parts.getOrNull(1).orEmpty()
                                val lst = parts.getOrNull(2).orEmpty()

                                viewModel.onRegister(
                                    surname = sur,
                                    name = nm,
                                    lastName = lst,
                                    email = email.trim(),
                                    login = login,
                                    password = password
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    when (uiState) {
                        is UiState.Loading -> CircularProgressIndicator(
                            color = NeonColor, strokeWidth = 2.dp
                        )

                        else -> Text("Регистрация", color = Color.White, fontSize = 18.sp)
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.size(60.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.vk_image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(Modifier.width(20.dp))
                IconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.size(50.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.google_image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(Modifier.width(20.dp))
                IconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.size(50.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.apple_image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }


        }

        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}





