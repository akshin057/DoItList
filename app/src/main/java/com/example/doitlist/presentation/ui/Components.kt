package com.example.doitlist.presentation.ui

import android.graphics.Paint
import android.os.Build
import android.widget.EditText
import android.widget.NumberPicker
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.doitlist.R
import com.example.doitlist.domain.model.Project
import com.example.doitlist.domain.model.Routine
import com.example.doitlist.domain.model.Task
import com.example.doitlist.presentation.calendar.CalendarInput
import com.example.doitlist.presentation.ui.theme.BackColor
import com.example.doitlist.presentation.ui.theme.BorderColor
import com.example.doitlist.presentation.ui.theme.FabBackColor
import com.example.doitlist.presentation.ui.theme.GreenNeonColor
import com.example.doitlist.presentation.ui.theme.NeonColor
import com.example.doitlist.presentation.ui.theme.RedNeonColor
import com.example.doitlist.presentation.ui.theme.TextColor
import com.example.doitlist.utils.Recurrence
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Duration.Companion.hours

val textStyle = TextStyle(
    fontSize = 18.sp,
    fontWeight = FontWeight.Medium,
    fontFamily = FontFamily.Serif,
    letterSpacing = 0.02.em,
    lineHeight = 24.sp,
)

@Composable
fun getTextFieldColors() = TextFieldDefaults.colors(
    errorTextColor = Color.Red,
    focusedTextColor = TextColor,
    focusedContainerColor = BackColor,
    unfocusedContainerColor = BackColor,
    unfocusedTextColor = TextColor,
    focusedIndicatorColor = TextColor,
    unfocusedIndicatorColor = TextColor
)

@Composable
fun GlowingCard(
    modifier: Modifier = Modifier,
    glowingColor: Color = NeonColor,
    containerColor: Color = BackColor,
    borderColor: Color = BorderColor,
    cornerRadius: Dp = 12.dp,
    glowingRadius: Dp = 20.dp,
    borderWidth: Dp = 1.5.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.drawBehind {
            val size = this.size
            drawContext.canvas.nativeCanvas.apply {
                drawRoundRect(
                    0f, 0f,
                    size.width, size.height,
                    cornerRadius.toPx(), cornerRadius.toPx(),
                    Paint().apply {
                        color = containerColor.toArgb()
                        setShadowLayer(
                            glowingRadius.toPx(),
                            0f, 4f,
                            glowingColor.toArgb()
                        )
                    }
                )
            }
            if (borderColor.alpha != 0f && borderWidth.value > 0f) {
                drawContext.canvas.nativeCanvas.drawRoundRect(
                    0f, 0f, size.width, size.height,
                    cornerRadius.toPx(), cornerRadius.toPx(),
                    Paint().apply {
                        color = borderColor.toArgb()
                        style = Paint.Style.STROKE
                        strokeWidth = borderWidth.toPx()
                        isAntiAlias = true
                    }
                )
            }
        }
    ) {
        content()
    }
}

@Composable
fun GlowingField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    supporting: String? = null
) {
    val tfColors: TextFieldColors = TextFieldDefaults.colors(
        unfocusedContainerColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,

        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        focusedTextColor = TextColor,
        unfocusedTextColor = TextColor,
        cursorColor = NeonColor,
        errorCursorColor = Color.Red,
        focusedLabelColor = if (isError) Color.Red else TextColor,
        unfocusedLabelColor = if (isError) Color.Red.copy(alpha = 0.7f) else TextColor.copy(alpha = 0.7f),
        errorLabelColor = Color.Red
    )

    GlowingCard(
        modifier = Modifier
            .height(60.dp)
            .width(300.dp),
        glowingColor = if (isError) Color.Red else NeonColor,
        containerColor = BackColor,
        borderColor = if (isError) Color.Red else BorderColor
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            modifier = Modifier.fillMaxSize(),
            textStyle = TextStyle(color = TextColor, fontSize = 18.sp),

            label = {
                Text(
                    text = label,
                    color = if (isError) Color.Red else TextColor,
                    fontSize = 16.sp
                )
            },

            colors = tfColors,
            isError = isError,
            supportingText = supporting
                ?.let { { Text(it, color = Color.Red, fontSize = 12.sp) } }
        )
    }
}

@Composable
fun ProjectSwipeItem(
    project: Project,
    onDelete: (Project) -> Unit,
    onClick: () -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { newValue ->
            if (newValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete(project)
            }
            true
        }
    )

    SwipeToDismissBox(
        state = swipeState,
        modifier = Modifier.fillMaxSize(),
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = "Удалить задачу",
                    tint = TextColor,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    ) {
        ProjectListItem(project, onClick = onClick)
    }

}

@Composable
fun MenuAction(
    @DrawableRes iconRes: Int,
    label: String,
    width: Dp = 100.dp,
    height: Dp = 120.dp,
    size: Dp = 95.dp,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(width = width, height = height)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = label,
            modifier = Modifier.size(size)
        )
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 18.sp, color = Color.White)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSwipeItem(
    task: Task,
    onDelete: (Task) -> Unit,
    onComplete: (Task) -> Unit,
    onClick: () -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { newValue ->
            if (newValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete(task)
            } else if (newValue == SwipeToDismissBoxValue.StartToEnd) {
                onComplete(task)
            }
            true
        }
    )

    SwipeToDismissBox(
        state = swipeState,
        modifier = Modifier
            .fillMaxSize(),
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Выполнить задачу",
                    tint = TextColor,
                    modifier = Modifier.size(40.dp)
                )

                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = "Удалить задачу",
                    tint = TextColor,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    ) {
        TaskListItem(task, onClick = onClick)
    }
}

@Composable
fun RoutineSwipeItem(
    routine: Routine,
    onDelete: (Routine) -> Unit,
    onComplete: (Routine) -> Unit,
    unComplete: (Routine) -> Unit,
    onClick: () -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { newValue ->
            when (newValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete(routine)
                }

                SwipeToDismissBoxValue.StartToEnd -> {
                    if (routine.isCompleted) {
                        unComplete(routine)
                    } else {
                        onComplete(routine)
                    }
                }

                SwipeToDismissBoxValue.Settled -> false
            }
            true
        }
    )

    SwipeToDismissBox(
        state = swipeState,
        modifier = Modifier
            .fillMaxSize(),
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Выполнить задачу",
                    tint = TextColor,
                    modifier = Modifier.size(40.dp)
                )

                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = "Удалить задачу",
                    tint = TextColor,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    ) {
        RoutineListItem(routine, onClick = onClick)
    }
}

@Composable
fun RoutineListItem(routine: Routine, onClick: () -> Unit) {
    val isCompleted = routine.isCompleted
    val textSize =
        if (routine.name.length > 30) 20.sp else if (routine.name.length > 20) 24.sp else 28.sp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
            .clickable { onClick() }
    ) {
        GlowingCard(
            modifier = Modifier
                .matchParentSize(),
            glowingColor = if (isCompleted) GreenNeonColor else NeonColor,
            containerColor = BackColor,
            borderColor = BorderColor
        ) {
            Text(
                text = routine.name,
                color = TextColor,
                fontSize = textSize,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp),
                textAlign = TextAlign.Start,
                style = textStyle,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else null
            )
        }


    }
}

@Composable
fun TaskListItem(task: Task, onClick: () -> Unit) {
    val textSize =
        if (task.name.length > 30) 20.sp else if (task.name.length > 20) 24.sp else 28.sp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
            .clickable { onClick() }
    ) {
        GlowingCard(
            modifier = Modifier
                .matchParentSize(),
            glowingColor = if (task.importance == 3) RedNeonColor else if (task.importance == 2) GreenNeonColor else NeonColor,
            containerColor = BackColor,
            borderColor = BorderColor
        ) {
            Text(
                text = task.name,
                color = TextColor,
                fontSize = textSize,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp),
                textAlign = TextAlign.Start,
                style = textStyle
            )
        }


    }
}

@Composable
fun ProjectListItem(project: Project, onClick: () -> Unit) {
    val textSize =
        if (project.name.length > 30) 20.sp else if (project.name.length > 20) 24.sp else 28.sp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
            .clickable { onClick() }
    ) {
        GlowingCard(
            modifier = Modifier
                .matchParentSize(),
            glowingColor = GreenNeonColor,
            containerColor = BackColor,
            borderColor = BorderColor
        ) {
            Text(
                text = project.name,
                color = TextColor,
                fontSize = textSize,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp),
                textAlign = TextAlign.Start,
                style = textStyle
            )
        }


    }


}


@Composable
fun NeonFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fabSize: Dp = 64.dp,
    glowColor: Color = NeonColor,
    iconTint: Color = Color.White,
    backgroundColor: Color = FabBackColor,
    image: ImageVector = Icons.Default.Add
) {
    val iconSize = fabSize * 0.9f

    val iconSizePx = with(LocalDensity.current) { iconSize.toPx() }
    val glowRadius = iconSizePx / 2f

    val glowBrush = Brush.radialGradient(
        colors = listOf(glowColor.copy(alpha = 0.6f), Color.Transparent),
        center = Offset(glowRadius, glowRadius),
        radius = glowRadius
    )

    FloatingActionButton(
        onClick = onClick,
        containerColor = backgroundColor,
        contentColor = iconTint,
        modifier = modifier.size(fabSize),
        elevation = FloatingActionButtonDefaults.elevation(0.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(iconSize)
        ) {
            Box(
                modifier = Modifier
                    .size(iconSize)
                    .background(glowBrush, CircleShape)
            )
            Icon(
                imageVector = image,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize * 0.9f)
            )
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {

    val items = listOf(
        Screen.TaskScreen,
        Screen.ProjectScreen,
        Screen.CalendarScreen,
        Screen.ChatScreen
    )

    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    NavigationBar(containerColor = BackColor) {
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = screen.iconRes),
                        contentDescription = screen.label,
                        modifier = Modifier.size(48.dp),
                        tint = TextColor
                    )
                },
                label = { Text(screen.label.toString(), color = TextColor, fontSize = 14.sp) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeDialog(
    initialDay: Int,
    initialMonth: Int,
    initialYear: Int,
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (day: Int, month: Int, year: Int, hour: Int, minute: Int) -> Unit
) {
    var day by remember { mutableStateOf(initialDay) }
    var month by remember { mutableStateOf(initialMonth) }
    var year by remember { mutableStateOf(initialYear) }
    var hour by remember { mutableStateOf(initialHour) }
    var minute by remember { mutableStateOf(initialMinute) }

    val nowInstant = Clock.System.now()
    val selectedInstant = try {
        LocalDateTime(year, month, day, hour, minute)
            .toInstant(TimeZone.currentSystemDefault())
    } catch (_: Exception) {
        nowInstant - 1.hours
    }
    val isValid = selectedInstant >= nowInstant

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(4.dp, TextColor),
            colors = CardDefaults.cardColors(
                containerColor = BackColor,
                contentColor = TextColor
            )
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onDismiss) {
                    Icon(painterResource(R.drawable.cross), null, tint = TextColor)
                }
            }

            Column(Modifier.padding(16.dp)) {
                Text("Выберите дату", color = TextColor, fontSize = 20.sp, style = textStyle)
                Spacer(Modifier.height(16.dp))

                DateTimePickerWheel(
                    day = day, month = month, year = year,
                    hour = hour, minute = minute,
                    onDayChange = { day = it },
                    onMonthChange = { month = it },
                    onYearChange = { year = it },
                    onHourChange = { hour = it },
                    onMinuteChange = { minute = it },
                    modifier = Modifier.background(BackColor)
                )

                if (!isValid) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Нельзя выбрать время в прошлом",
                        color = RedNeonColor,
                        fontSize = 14.sp
                    )
                }

                Spacer(Modifier.height(24.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        if (isValid) {
                            onConfirm(day, month, year, hour, minute)
                            onDismiss()
                        }
                    }, enabled = isValid) {
                        Text("OK", color = if (isValid) TextColor else TextColor.copy(alpha = 0.3f))
                    }
                }
            }
        }
    }
}

@Composable
fun TimeDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    var hour by remember { mutableStateOf(initialHour) }
    var minute by remember { mutableStateOf(initialMinute) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(4.dp, TextColor),
            colors = CardDefaults.cardColors(
                containerColor = BackColor,
                contentColor = TextColor
            )
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onDismiss) {
                    Icon(painterResource(R.drawable.cross), null, tint = TextColor)
                }
            }

            Column(Modifier.padding(16.dp)) {
                Text("Выберите время", color = TextColor, fontSize = 20.sp, style = textStyle)
                Spacer(Modifier.height(16.dp))

                TimePickerWheel(
                    hour = hour,
                    min = minute,
                    onHourChange = { hour = it },
                    onMinuteChange = { minute = it },
                    modifier = Modifier.background(BackColor)
                )

                Spacer(Modifier.height(24.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        onConfirm(hour, minute)
                        onDismiss()
                    }) {
                        Text(
                            "OK",
                            color = TextColor
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun TimePickerWheel(
    hour: Int,
    min: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row {
            NumberPickerWithColor(
                min = 0, max = 23, displayedValues = null,
                value = hour, onValueChange = onHourChange,
                size = DpSize(80.dp, 150.dp)
            )
            NumberPickerWithColor(
                min = 0, max = 59, displayedValues = null,
                value = min, onValueChange = onMinuteChange,
                size = DpSize(80.dp, 150.dp)
            )
        }
    }
}

@Composable
fun DateTimePickerWheel(
    day: Int,
    month: Int,
    year: Int,
    hour: Int,
    minute: Int,
    yearRange: IntRange = (2025..2050),
    onDayChange: (Int) -> Unit,
    onMonthChange: (Int) -> Unit,
    onYearChange: (Int) -> Unit,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row {
            NumberPickerWithColor(
                min = 1, max = 31, displayedValues = null,
                value = day, onValueChange = onDayChange,
                size = DpSize(80.dp, 150.dp)
            )
            NumberPickerWithColor(
                min = 1, max = 12,
                displayedValues = arrayOf(
                    "Янв", "Фев", "Мар", "Апр", "Май", "Июн",
                    "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"
                ),
                value = month, onValueChange = onMonthChange,
                size = DpSize(100.dp, 150.dp)
            )
            NumberPickerWithColor(
                min = yearRange.first, max = yearRange.last,
                displayedValues = null,
                value = year, onValueChange = onYearChange,
                size = DpSize(100.dp, 150.dp)
            )
        }
        Row {
            NumberPickerWithColor(
                min = 0, max = 23, displayedValues = null,
                value = hour, onValueChange = onHourChange,
                size = DpSize(80.dp, 150.dp)
            )
            NumberPickerWithColor(
                min = 0, max = 59, displayedValues = null,
                value = minute, onValueChange = onMinuteChange,
                size = DpSize(80.dp, 150.dp)
            )
        }
    }
}

@Composable
fun NumberPickerWithColor(
    min: Int, max: Int,
    displayedValues: Array<String>?,
    value: Int,
    onValueChange: (Int) -> Unit,
    size: DpSize
) {
    val ctx = LocalContext.current
    AndroidView(
        factory = {
            NumberPicker(ctx).apply {
                this.minValue = min
                this.maxValue = max
                this.wrapSelectorWheel = true

                this.setFormatter { v -> v.toString() }
                this.setOnValueChangedListener { _, _, new -> onValueChange(new) }
                displayedValues?.let { this.displayedValues = it }

                post {
                    val c = TextColor.toArgb()
                    for (i in 0 until childCount) {
                        (getChildAt(i) as? EditText)?.let { et ->
                            et.setTextColor(c)
                            et.setHintTextColor(c)
                        }
                    }
                }
            }
        },
        update = { np ->
            np.value = value
            np.post {
                val c = TextColor.toArgb()
                for (i in 0 until np.childCount) {
                    (np.getChildAt(i) as? EditText)?.let { et ->
                        et.setTextColor(c)
                        et.setHintTextColor(c)
                    }
                }
            }
        },
        modifier = Modifier.size(size.width, size.height)
    )
}

@Composable
fun ProjectBottomSheet(
    onDismiss: () -> Unit,
    onSave: (Project) -> Unit,
    project: Project?,
    navController: NavController? = null
) {
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedDay by rememberSaveable { mutableStateOf(0) }
    var selectedMonth by rememberSaveable { mutableStateOf(0) }
    var selectedYear by rememberSaveable { mutableStateOf(0) }
    var selectedHour by rememberSaveable { mutableStateOf(0) }
    var selectedMinute by rememberSaveable { mutableStateOf(0) }

    val scrollState = rememberScrollState()

    var showDateDialog by remember { mutableStateOf(false) }
    var dateConfirmed by rememberSaveable { mutableStateOf(false) }

    val dateText by remember(selectedDay, selectedMonth, selectedYear) {
        mutableStateOf(
            if (dateConfirmed)
                "%02d.%02d.%04d".format(selectedDay, selectedMonth, selectedYear)
            else ""
        )
    }

    LaunchedEffect(project) {
        if (project != null) {
            name = project.name
            description = project.description.orEmpty()

            project.endDate?.let { d ->
                selectedDay = d.dayOfMonth
                selectedMonth = d.monthNumber
                selectedYear = d.year
                dateConfirmed = true
            } ?: run {
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                selectedDay = today.dayOfMonth
                selectedMonth = today.monthNumber
                selectedYear = today.year
                dateConfirmed = false
            }

            selectedHour = 0
            selectedMinute = 0
        } else {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            name = ""
            description = ""
            selectedDay = today.dayOfMonth
            selectedMonth = today.monthNumber
            selectedYear = today.year
            selectedHour = 0
            selectedMinute = 0
            dateConfirmed = false
        }
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-30).dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = {
                onDismiss()
            }) {
                Icon(
                    painter = painterResource(R.drawable.cross),
                    contentDescription = null,
                    tint = TextColor,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .background(BackColor)
                .offset(y = (-30).dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название", color = TextColor, fontSize = 18.sp) },
                modifier = Modifier.fillMaxWidth(),
                colors = getTextFieldColors(),
                textStyle = textStyle
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание", color = TextColor, fontSize = 18.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                colors = getTextFieldColors(),
                maxLines = 4,
                textStyle = textStyle
            )
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.date),
                    modifier = Modifier
                        .size(48.dp)
                        .padding(top = 10.dp),
                    contentDescription = null,
                    tint = TextColor
                )

                GlowingField(
                    label = "Дата",
                    onChange = {},
                    value = dateText
                )
            }

            GlowingCard(
                modifier = Modifier
                    .height(60.dp)
                    .wrapContentWidth()
                    .widthIn(min = 100.dp)
                    .clickable { showDateDialog = true }
            ) {
                Text(
                    "Сделать до",
                    style = textStyle,
                    color = TextColor,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(20.dp)
                )
            }

            if (showDateDialog) {
                DateTimeDialog(
                    initialDay = selectedDay,
                    initialMonth = selectedMonth,
                    initialYear = selectedYear,
                    initialHour = selectedHour,
                    initialMinute = selectedMinute,
                    onDismiss = { showDateDialog = false },
                    onConfirm = { d, m, y, h, mi ->
                        selectedDay = d
                        selectedMonth = m
                        selectedYear = y
                        selectedHour = h
                        selectedMinute = mi
                        dateConfirmed = true
                        showDateDialog = false
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            if (navController != null && project != null && project.localId != null) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    GlowingCard(
                        modifier = Modifier
                            .height(60.dp)
                            .wrapContentWidth()
                            .widthIn(min = 100.dp)
                            .clickable {
                                navController.navigate(Screen.TaskForProjectScreen.route(projectId = project.localId))
                            },
                        glowingColor = GreenNeonColor
                    ) {
                        Text(
                            "Задачи по проекту",
                            style = textStyle,
                            color = TextColor,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                    }

                    GlowingCard(
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(60.dp)
                            .clickable {
                                val endDate = if (dateConfirmed)
                                    LocalDate(selectedYear, selectedMonth, selectedDay)
                                else null

                                val today = Clock.System.now()
                                    .toLocalDateTime(TimeZone.currentSystemDefault())
                                    .date

                                val result = (project ?: Project(
                                    remoteId = null,
                                    localId = null,
                                    name = name,
                                    description = description,
                                    startDate = today,
                                    endDate = endDate,
                                )).copy(
                                    name = name,
                                    description = description,
                                    endDate = endDate,
                                )

                                onSave(result)
                                onDismiss()
                            }
                    ) {
                        Text(
                            "Сохранить",
                            style = textStyle,
                            color = TextColor,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    GlowingCard(
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(60.dp)
                            .clickable {
                                val endDate = if (dateConfirmed)
                                    LocalDate(selectedYear, selectedMonth, selectedDay)
                                else null

                                val today = Clock.System.now()
                                    .toLocalDateTime(TimeZone.currentSystemDefault())
                                    .date

                                val result = (project ?: Project(
                                    remoteId = null,
                                    localId = null,
                                    name = name,
                                    description = description,
                                    startDate = today,
                                    endDate = endDate,
                                )).copy(
                                    name = name,
                                    description = description,
                                    endDate = endDate,
                                )

                                onSave(result)
                                onDismiss()
                            }
                    ) {
                        Text(
                            "Сохранить",
                            style = textStyle,
                            color = TextColor,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
            }

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskBottomSheet(
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit,
    task: Task?,
    projects: List<Project>?,
    date: LocalDate? = null,
    currentProject: Project? = null
) {
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var importance by rememberSaveable { mutableIntStateOf(0) }
    var selectedDay by rememberSaveable { mutableStateOf(0) }
    var selectedMonth by rememberSaveable { mutableStateOf(0) }
    var selectedYear by rememberSaveable { mutableStateOf(0) }
    var selectedHour by rememberSaveable { mutableStateOf(0) }
    var selectedMinute by rememberSaveable { mutableStateOf(0) }
    var recurrenceId by rememberSaveable {
        mutableLongStateOf(task?.recurrenceId ?: Recurrence.Never.id)
    }
    var recurrenceName by rememberSaveable {
        mutableStateOf(task?.let {
            Recurrence.entries.first { r -> r.id == it.recurrenceId }.displayName
        } ?: Recurrence.Never.displayName)
    }
    var projectId by rememberSaveable { mutableStateOf<Long?>(null) }
    var projectName by remember { mutableStateOf("Проект") }

    var showProjectsDropdown by remember { mutableStateOf(false) }

    var showImportanceDropdown by remember { mutableStateOf(false) }
    val labelColor = when (importance) {
        3 -> RedNeonColor
        2 -> GreenNeonColor
        1 -> NeonColor
        else -> TextColor
    }
    var showDateDialog by remember { mutableStateOf(false) }
    var dateConfirmed by rememberSaveable { mutableStateOf(false) }

    var showRecurrenceDropdown by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val dateText by remember(selectedDay, selectedMonth, selectedYear) {
        mutableStateOf(
            if (dateConfirmed)
                "%02d.%02d.%04d".format(selectedDay, selectedMonth, selectedYear)
            else ""
        )
    }
    val timeText by remember(selectedHour, selectedMinute) {
        mutableStateOf(
            if (dateConfirmed)
                "%02d:%02d".format(selectedHour, selectedMinute)
            else ""
        )
    }

    LaunchedEffect(task) {
        if (task != null) {
            name = task.name
            description = task.description.toString()
            importance = task.importance

            task.endDate?.toLocalDateTime(TimeZone.currentSystemDefault())?.let { dt ->
                selectedDay = dt.dayOfMonth
                selectedMonth = dt.monthNumber
                selectedYear = dt.year
                selectedHour = dt.hour
                selectedMinute = dt.minute
                dateConfirmed = true
            } ?: run {
                val now = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                selectedDay = now.dayOfMonth
                selectedMonth = now.monthNumber
                selectedYear = now.year
                selectedHour = now.hour
                selectedMinute = now.minute
                dateConfirmed = false
            }
        } else {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            name = ""
            description = ""
            importance = 0

            if (date != null) {
                selectedDay = date.dayOfMonth
                selectedMonth = date.monthNumber
                selectedYear = date.year
            } else {
                selectedDay = now.dayOfMonth
                selectedMonth = now.monthNumber
                selectedYear = now.year
            }

            selectedHour = now.hour
            selectedMinute = now.minute
            dateConfirmed = date != null
            recurrenceId = Recurrence.Never.id
            recurrenceName = Recurrence.Never.displayName
        }

        if (currentProject != null) {
            projectId = currentProject.localId
            projectName = currentProject.name
        }
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-30).dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = {
                onDismiss()
            }) {
                Icon(
                    painter = painterResource(R.drawable.cross),
                    contentDescription = null,
                    tint = TextColor,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .background(BackColor)
                .offset(y = (-30).dp)
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название", color = TextColor, fontSize = 18.sp) },
                modifier = Modifier.fillMaxWidth(),
                colors = getTextFieldColors(),
                textStyle = textStyle
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание", color = TextColor, fontSize = 18.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                colors = getTextFieldColors(),
                maxLines = 4,
                textStyle = textStyle
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.date),
                    modifier = Modifier
                        .size(48.dp)
                        .padding(top = 10.dp),
                    contentDescription = null,
                    tint = TextColor
                )

                GlowingField(
                    label = "Дата",
                    onChange = {},
                    value = dateText
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.clock),
                    modifier = Modifier
                        .size(48.dp)
                        .padding(top = 10.dp),
                    contentDescription = null,
                    tint = TextColor
                )

                GlowingField(
                    label = "Время",
                    onChange = {},
                    value = timeText
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column {
                    GlowingCard(
                        modifier = Modifier
                            .height(60.dp)
                            .wrapContentWidth()
                            .widthIn(min = 100.dp)
                            .clickable {
                                showProjectsDropdown = true
                            }
                    ) {
                        Text(
                            projectName,
                            style = textStyle,
                            color = TextColor,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showProjectsDropdown,
                        onDismissRequest = { showProjectsDropdown = false },
                        modifier = Modifier.background(BackColor)
                    ) {
                        println(projects)
                        projects?.forEach { project ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        project.name,
                                        style = textStyle,
                                        color = TextColor,
                                        fontSize = 20.sp
                                    )
                                },
                                onClick = {
                                    projectId = project.localId
                                    projectName = project.name
                                    showProjectsDropdown = false
                                }
                            )
                        }
                    }
                }

                Column {
                    GlowingCard(
                        modifier = Modifier
                            .height(60.dp)
                            .wrapContentWidth()
                            .widthIn(min = 100.dp)
                            .clickable { showImportanceDropdown = true }
                    ) {
                        Text(
                            "Приоритет",
                            style = textStyle,
                            color = labelColor,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showImportanceDropdown,
                        onDismissRequest = { showImportanceDropdown = false },
                        modifier = Modifier.background(BackColor)
                    ) {

                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Высокий",
                                    style = textStyle,
                                    color = RedNeonColor,
                                    fontSize = 20.sp
                                )
                            },
                            onClick = {
                                importance = 3
                                showImportanceDropdown = false
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Средний",
                                    style = textStyle,
                                    color = GreenNeonColor,
                                    fontSize = 20.sp
                                )
                            },
                            onClick = {
                                importance = 2
                                showImportanceDropdown = false
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Низкий",
                                    style = textStyle,
                                    color = NeonColor,
                                    fontSize = 20.sp
                                )
                            },
                            onClick = {
                                importance = 1
                                showImportanceDropdown = false
                            }
                        )
                    }

                }

                Column {
                    GlowingCard(
                        modifier = Modifier
                            .height(60.dp)
                            .wrapContentWidth()
                            .widthIn(min = 100.dp)
                            .clickable { showDateDialog = true }
                    ) {
                        Text(
                            "Сделать до",
                            style = textStyle,
                            color = TextColor,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                    }

                    if (showDateDialog) {
                        DateTimeDialog(
                            initialDay = selectedDay,
                            initialMonth = selectedMonth,
                            initialYear = selectedYear,
                            initialHour = selectedHour,
                            initialMinute = selectedMinute,
                            onDismiss = { showDateDialog = false },
                            onConfirm = { d, m, y, h, mi ->
                                selectedDay = d
                                selectedMonth = m
                                selectedYear = y
                                selectedHour = h
                                selectedMinute = mi
                                dateConfirmed = true
                                showDateDialog = false
                            }
                        )
                    }
                }

                Column {
                    GlowingCard(
                        modifier = Modifier
                            .height(60.dp)
                            .wrapContentWidth()
                            .widthIn(min = 100.dp)
                            .clickable { showRecurrenceDropdown = true }
                    ) {
                        Text(
                            recurrenceName,
                            style = textStyle,
                            color = labelColor,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showRecurrenceDropdown,
                        onDismissRequest = { showRecurrenceDropdown = false },
                        modifier = Modifier.background(BackColor)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    Recurrence.Never.displayName,
                                    style = textStyle,
                                    color = Recurrence.Never.color,
                                    fontSize = 20.sp
                                )
                            },
                            onClick = {
                                recurrenceId = Recurrence.Never.id
                                recurrenceName = Recurrence.Never.displayName
                                showRecurrenceDropdown = false
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    Recurrence.Daily.displayName,
                                    style = textStyle,
                                    color = Recurrence.Daily.color,
                                    fontSize = 20.sp
                                )
                            },
                            onClick = {
                                recurrenceId = Recurrence.Daily.id
                                recurrenceName = Recurrence.Daily.displayName
                                showRecurrenceDropdown = false
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    Recurrence.Monday.displayName,
                                    style = textStyle,
                                    color = Recurrence.Monday.color,
                                    fontSize = 20.sp
                                )
                            },
                            onClick = {
                                recurrenceId = Recurrence.Monday.id
                                recurrenceName = Recurrence.Monday.displayName
                                showRecurrenceDropdown = false
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    Recurrence.Tuesday.displayName,
                                    style = textStyle,
                                    color = Recurrence.Tuesday.color,
                                    fontSize = 20.sp
                                )
                            },
                            onClick = {
                                recurrenceId = Recurrence.Tuesday.id
                                recurrenceName = Recurrence.Tuesday.displayName
                                showRecurrenceDropdown = false
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    Recurrence.Wednesday.displayName,
                                    style = textStyle,
                                    color = Recurrence.Wednesday.color,
                                    fontSize = 20.sp
                                )
                            },
                            onClick = {
                                recurrenceId = Recurrence.Wednesday.id
                                recurrenceName = Recurrence.Wednesday.displayName
                                showRecurrenceDropdown = false
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    Recurrence.Thursday.displayName,
                                    style = textStyle,
                                    color = Recurrence.Thursday.color,
                                    fontSize = 20.sp
                                )
                            },
                            onClick = {
                                recurrenceId = Recurrence.Thursday.id
                                recurrenceName = Recurrence.Thursday.displayName
                                showRecurrenceDropdown = false
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    Recurrence.Friday.displayName,
                                    style = textStyle,
                                    color = Recurrence.Friday.color,
                                    fontSize = 20.sp
                                )
                            },
                            onClick = {
                                recurrenceId = Recurrence.Friday.id
                                recurrenceName = Recurrence.Friday.displayName
                                showRecurrenceDropdown = false
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    Recurrence.Saturday.displayName,
                                    style = textStyle,
                                    color = Recurrence.Saturday.color,
                                    fontSize = 20.sp
                                )
                            },
                            onClick = {
                                recurrenceId = Recurrence.Saturday.id
                                recurrenceName = Recurrence.Saturday.displayName
                                showRecurrenceDropdown = false
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    Recurrence.Sunday.displayName,
                                    style = textStyle,
                                    color = Recurrence.Sunday.color,
                                    fontSize = 20.sp
                                )
                            },
                            onClick = {
                                recurrenceId = Recurrence.Sunday.id
                                recurrenceName = Recurrence.Sunday.displayName
                                showRecurrenceDropdown = false
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    Recurrence.Monthly.displayName,
                                    style = textStyle,
                                    color = Recurrence.Monthly.color,
                                    fontSize = 20.sp
                                )
                            },
                            onClick = {
                                recurrenceId = Recurrence.Monthly.id
                                recurrenceName = Recurrence.Monthly.displayName
                                showRecurrenceDropdown = false
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    Recurrence.Yearly.displayName,
                                    style = textStyle,
                                    color = Recurrence.Yearly.color,
                                    fontSize = 20.sp
                                )
                            },
                            onClick = {
                                recurrenceId = Recurrence.Yearly.id
                                recurrenceName = Recurrence.Yearly.displayName
                                showRecurrenceDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                GlowingCard(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(60.dp)
                        .clickable {
                            val tz = TimeZone.currentSystemDefault()

                            val endOfTodayInstant = run {
                                val today = Clock.System.now().toLocalDateTime(tz).date
                                LocalDateTime(
                                    year = today.year,
                                    month = today.month,
                                    dayOfMonth = today.dayOfMonth,
                                    hour = 23,
                                    minute = 59
                                ).toInstant(tz)
                            }

                            val endDateInstant = if (dateConfirmed) {
                                LocalDateTime(
                                    year = selectedYear,
                                    month = Month.entries[selectedMonth - 1],
                                    dayOfMonth = selectedDay,
                                    hour = selectedHour,
                                    minute = selectedMinute
                                ).toInstant(TimeZone.currentSystemDefault())
                            } else endOfTodayInstant

                            val result = (task ?: Task(
                                remoteId = null,
                                localId = null,
                                name = name,
                                description = description,
                                projectId = projectId,
                                recurrenceId = recurrenceId,
                                isDone = false,
                                importance = importance,
                                startDate = Clock.System.now(),
                                endDate = endDateInstant,
                                completedAt = null,
                                reminderTime = null,
                                updatedAt = Clock.System.now()
                            )).copy(
                                name = name,
                                description = description,
                                importance = importance,
                                endDate = endDateInstant,
                                recurrenceId = recurrenceId,
                                updatedAt = Clock.System.now()
                            )
                            onSave(result)
                            onDismiss()
                        }
                ) {
                    Text(
                        "Сохранить",
                        style = textStyle,
                        color = TextColor,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        }
    }

}

@Composable
fun RoutineBottomSheet(
    onDismiss: () -> Unit,
    onSave: (Routine) -> Unit,
    routine: Routine?
) {
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedHour by rememberSaveable { mutableStateOf(0) }
    var selectedMinute by rememberSaveable { mutableStateOf(0) }

    val scrollState = rememberScrollState()

    var showTimeDialog by remember { mutableStateOf(false) }
    var timeConfirmed by rememberSaveable { mutableStateOf(false) }

    val timeText by remember(selectedHour, selectedMinute) {
        mutableStateOf(
            if (timeConfirmed)
                "%02d:%02d".format(selectedHour, selectedMinute)
            else ""
        )
    }

    LaunchedEffect(routine) {
        if (routine != null) {
            name = routine.name
            description = routine.description.orEmpty()

            routine.reminderTime?.let { time ->
                selectedHour = time.hour
                selectedMinute = time.minute
                timeConfirmed = true
            } ?: run {
                selectedHour = 0
                selectedMinute = 0
                timeConfirmed = false
            }
        } else {
            name = ""
            description = ""
            selectedHour = 0
            selectedMinute = 0
            timeConfirmed = false
        }
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-30).dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = {
                onDismiss()
            }) {
                Icon(
                    painter = painterResource(R.drawable.cross),
                    contentDescription = null,
                    tint = TextColor,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .background(BackColor)
                .offset(y = (-30).dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название", color = TextColor, fontSize = 18.sp) },
                modifier = Modifier.fillMaxWidth(),
                colors = getTextFieldColors(),
                textStyle = textStyle
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание", color = TextColor, fontSize = 18.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                colors = getTextFieldColors(),
                maxLines = 4,
                textStyle = textStyle
            )
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.date),
                    modifier = Modifier
                        .size(48.dp)
                        .padding(top = 10.dp),
                    contentDescription = null,
                    tint = TextColor
                )

                GlowingField(
                    label = "Время напоминания",
                    onChange = {},
                    value = timeText
                )
            }

            GlowingCard(
                modifier = Modifier
                    .height(60.dp)
                    .wrapContentWidth()
                    .widthIn(min = 100.dp)
                    .clickable { showTimeDialog = true }
            ) {
                Text(
                    "Напоминать в",
                    style = textStyle,
                    color = TextColor,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(20.dp)
                )
            }

            if (showTimeDialog) {
                TimeDialog(
                    initialHour = selectedHour,
                    initialMinute = selectedMinute,
                    onDismiss = { showTimeDialog = false },
                    onConfirm = { h, mi ->
                        selectedHour = h
                        selectedMinute = mi
                        timeConfirmed = true
                        showTimeDialog = false
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                GlowingCard(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(60.dp)
                        .clickable {
                            val reminderTime = if (timeConfirmed)
                                LocalTime(selectedHour, selectedMinute)
                            else null

                            val today = Clock.System.now()
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .date

                            val result = (routine ?: Routine(
                                remoteId = null,
                                localId = null,
                                name = name,
                                description = description,
                                startDate = today,
                                reminderTime = reminderTime
                            )).copy(
                                name = name,
                                description = description,
                                reminderTime = reminderTime
                            )

                            onSave(result)
                            onDismiss()
                        }
                ) {
                    Text(
                        "Сохранить",
                        style = textStyle,
                        color = TextColor,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    strokeWidth: Float = 15F,
    onDayClick: (Int) -> Unit,
    calendarInput: List<CalendarInput>,
    weeks: Int,
    firstWeekdayOffset: Int
) {
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var clickAnimationOffset by remember { mutableStateOf(Offset.Zero) }
    var animationRadius by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(weeks, firstWeekdayOffset, calendarInput.size) {
                    detectTapGestures { offset ->
                        val col = (offset.x / size.width * 7).toInt().coerceIn(0, 6)
                        val row = (offset.y / size.height * weeks).toInt().coerceIn(0, weeks - 1)
                        val index = row * 7 + col
                        val day = index - firstWeekdayOffset + 1
                        if (day in 1..calendarInput.size) {
                            onDayClick(day)
                            clickAnimationOffset = offset
                            scope.launch {
                                animate(
                                    0f,
                                    225f,
                                    animationSpec = tween(300)
                                ) { v, _ -> animationRadius = v }
                            }
                        }
                    }
                }
        ) {
            val canvasHeight = size.height
            val canvasWidth = size.width
            canvasSize = Size(canvasWidth, canvasHeight)
            val xSteps = canvasWidth / 7
            val ySteps = canvasHeight / weeks

            val col = (clickAnimationOffset.x / canvasSize.width * 7).toInt() + 1
            val row = (clickAnimationOffset.y / canvasSize.height * weeks).toInt() + 1

            val path = Path().apply {
                moveTo((col - 1) * xSteps, (row - 1) * ySteps)
                lineTo(col * xSteps, (row - 1) * ySteps)
                lineTo(col * xSteps, row * ySteps)
                lineTo((col - 1) * xSteps, row * ySteps)
                close()
            }

            clipPath(path) {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(TextColor.copy(0.8f), TextColor.copy(0.2f)),
                        center = clickAnimationOffset,
                        radius = animationRadius + 0.1f
                    ),
                    radius = animationRadius + 0.1f,
                    center = clickAnimationOffset
                )
            }

            drawRoundRect(
                TextColor,
                cornerRadius = CornerRadius(25F, 25F),
                style = Stroke(width = strokeWidth)
            )

            for (i in 1 until weeks) {
                drawLine(
                    color = TextColor,
                    start = Offset(0f, ySteps * i),
                    end = Offset(canvasWidth, ySteps * i),
                    strokeWidth = strokeWidth
                )
            }

            for (i in 1 until 7) {
                drawLine(
                    color = TextColor,
                    start = Offset(xSteps * i, 0f),
                    end = Offset(xSteps * i, canvasHeight),
                    strokeWidth = strokeWidth
                )
            }

            val textHeight = 17.dp.toPx()
            for (day in 1..calendarInput.size) {
                val idx = (day - 1) + firstWeekdayOffset
                val textPositionX = xSteps * (idx % 7) + strokeWidth
                val textPositionY = (idx / 7) * ySteps + textHeight + strokeWidth / 2

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        "$day",
                        textPositionX,
                        textPositionY,
                        Paint().apply {
                            textSize = textHeight
                            color = TextColor.toArgb()
                            isFakeBoldText = true
                        }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SectionHeader(date: LocalDate, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = formatDateHeader(date),
            color = TextColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )

        GlowingCard(
            modifier = Modifier
                .height(55.dp)
                .wrapContentWidth()
                .widthIn(min = 100.dp)
                .offset(y = (-10).dp)
                .clickable { onClick() },
            glowingColor = RedNeonColor
        ) {
            Text(
                "Перенести",
                style = textStyle,
                color = TextColor,
                fontSize = 15.sp,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateHeader(date: LocalDate): String {
    val tz = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(tz).date
    val yesterday = today.minus(DatePeriod(days = 1))
    return when (date) {
        today -> "Сегодня"
        yesterday -> "Вчера"
        else -> date.toJavaLocalDate()
            .format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault()))
    }
}

