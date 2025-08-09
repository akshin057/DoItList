package com.example.doitlist

import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.doitlist.presentation.login.LoginScreen
import com.example.doitlist.presentation.register.RegisterScreen
import com.example.doitlist.presentation.ui.Navigation
import com.example.doitlist.presentation.ui.Screen
import com.example.doitlist.presentation.ui.theme.BackColor
import com.example.doitlist.utils.AuthMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Navigation()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SplashScreen(navController: NavController) {

    var authMode by remember { mutableStateOf(AuthMode.Login) }

    val scale = remember {
        Animatable(0f)
    }

    val offsetY = remember {
        Animatable(0f)
    }

    val density = LocalDensity.current
    val screenHeight = LocalConfiguration.current.screenHeightDp

    var showLogin by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 3.0f,
            animationSpec = tween(
                durationMillis = 2000,
                easing = {
                    OvershootInterpolator(2f).getInterpolation(it)
                }
            )
        )

        offsetY.animateTo(
            targetValue = with(density) { -(screenHeight / 9.5f).dp.toPx() },
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )

        delay(1500)
        showLogin = !showLogin
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(BackColor)
    ) {
        Image(
            painter = painterResource(R.drawable.start_image),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .scale(scale.value)
                .offset { IntOffset(0, offsetY.value.roundToInt()) }
        )

        AnimatedVisibility(
            visible = showLogin,
            enter = slideInVertically { it } + fadeIn(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-60).dp)
                    .background(BackColor)
            ) {
                AnimatedContent(
                    targetState = authMode,
                    transitionSpec = {
                        slideInVertically { it } + fadeIn() with
                                slideOutVertically { -it } + fadeOut()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { mode ->
                    when (mode) {
                        AuthMode.Login ->
                            LoginScreen(
                                onSwitch = { authMode = AuthMode.Register },
                                onSuccess = { navController.navigate(Screen.TaskScreen.route) },
                                modifier = Modifier.fillMaxWidth()
                            )

                        AuthMode.Register ->
                            RegisterScreen(
                                onSwitch = { authMode = AuthMode.Login },
                                onSuccess = { navController.navigate(Screen.TaskScreen.route) },
                                modifier = Modifier.fillMaxWidth()
                            )
                    }
                }
            }
        }
    }
}