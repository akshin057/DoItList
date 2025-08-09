package com.example.doitlist.presentation.login

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.doitlist.utils.UiState
import kotlinx.coroutines.launch


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}

@Composable
fun LoginScreen(
    modifier: Modifier,
    onSuccess: () -> Unit,
    onSwitch: () -> Unit,                  // ← переход на экран регистрации
    viewModel: LoginViewModel = hiltViewModel()
) {
    var login by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isPassVisible by rememberSaveable { mutableStateOf(false) }

    val snackBar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val uiState = viewModel.uiState

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) onSuccess()
        if (uiState is UiState.Error) {
            scope.launch {
                snackBar.showSnackbar(
                    message = uiState.throwable.localizedMessage ?: "Неправильный логин или пароль",
                    withDismissAction = true
                )
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
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("С возвращением в Do It", color = Color.White, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(80.dp))
            GlowingCard(
                modifier = Modifier
                    .height(60.dp)
                    .width(300.dp),
                glowingColor = NeonColor,
                containerColor = BackColor,
                cornerRadius = 12.dp,
                borderColor = BorderColor,
            ) {
                OutlinedTextField(
                    value = login,
                    onValueChange = { login = it },
                    label = { Text("Логин", color = Color.White, fontSize = 16.sp) },
                    singleLine = true,
                    textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
                    modifier = Modifier.fillMaxSize(),
                    colors = tfColors,
                    shape = shape
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            GlowingCard(
                modifier = Modifier
                    .height(60.dp)
                    .width(300.dp),
                glowingColor = NeonColor,
                containerColor = BackColor,
                cornerRadius = 12.dp,
                borderColor = BorderColor,
            ) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Пароль", color = Color.White, fontSize = 16.sp) },
                    singleLine = true,
                    textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
                    modifier = Modifier.fillMaxSize(),
                    visualTransformation = if (isPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon = if (isPassVisible)
                            Icons.Default.VisibilityOff
                        else
                            Icons.Default.Visibility
                        IconButton(onClick = { isPassVisible = !isPassVisible }) {
                            Icon(icon, null, tint = Color.White)
                        }
                    },
                    colors = tfColors,
                    shape = shape
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            Box(
                modifier = Modifier.clickable(
                    onClick = onSwitch
                )
            ) {
                Text(
                    "Еще нет аккаунта",
                    color = TextColor,
                    fontSize = 18.sp,
                    modifier = Modifier.offset(x = -68.dp)
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
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
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(enabled = uiState !is UiState.Loading) {
                            println(login)
                            println(password)
                            viewModel.onLogin(login, password)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    when (uiState) {
                        is UiState.Loading -> CircularProgressIndicator(
                            color = NeonColor,
                            strokeWidth = 2.dp
                        )
                        is UiState.Error -> Text("Ошибка входа", color = Color.Red)
                        else -> Text("Войти", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                "Авторизоваться через",
                color = TextColor,
                fontSize = 18.sp,
            )
            Spacer(modifier = Modifier.height(40.dp))
            Row(horizontalArrangement = Arrangement.Center) {
                IconButton(
                    onClick = {},
                    modifier = Modifier.size(60.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.vk_image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                IconButton(
                    onClick = {}, modifier = Modifier.size(50.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.google_image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                IconButton(
                    onClick = {}, modifier = Modifier.size(50.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.apple_image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        SnackbarHost(hostState = snackBar, modifier = Modifier.align(Alignment.BottomCenter))
    }
}






