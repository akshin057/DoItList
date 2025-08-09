package com.example.doitlist.utils

import androidx.compose.ui.graphics.Color
import com.example.doitlist.presentation.ui.theme.Amber200
import com.example.doitlist.presentation.ui.theme.Blue200
import com.example.doitlist.presentation.ui.theme.DeepPurple200
import com.example.doitlist.presentation.ui.theme.GreenNeonColor
import com.example.doitlist.presentation.ui.theme.Lime200
import com.example.doitlist.presentation.ui.theme.NeonColor
import com.example.doitlist.presentation.ui.theme.Orange50
import com.example.doitlist.presentation.ui.theme.Pink200
import com.example.doitlist.presentation.ui.theme.RedNeonColor
import com.example.doitlist.presentation.ui.theme.Teal200
import com.example.doitlist.presentation.ui.theme.TextColor

enum class AuthMode { Login, Register }

enum class Recurrence(val displayName: String, val id: Long, val color: Color) {
    Never("Не повторять", 1, TextColor),
    Daily("Каждый день", 2, NeonColor),
    Monday("Понедельник", 3, RedNeonColor),
    Monthly("Каждый месяц", 4, GreenNeonColor),
    Yearly("Каждый год", 5, Orange50),
    Tuesday("Вторник", 6, Teal200),
    Wednesday("Среда", 7, Lime200),
    Thursday("Четверг", 8, Blue200),
    Friday("Пятница", 9, Amber200),
    Saturday("Суббота", 10, DeepPurple200),
    Sunday("Воскресенье", 11, Pink200)
}