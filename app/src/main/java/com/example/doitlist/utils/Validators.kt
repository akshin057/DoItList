package com.example.doitlist.utils

import kotlinx.datetime.*

fun checkEmail(email: String): Boolean =
    Regex("""^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.(ru|com)$""").matches(email)

fun checkPassword(pass: String): Boolean =
    pass.length >= 8 &&
            pass.any(Char::isUpperCase) &&
            pass.any(Char::isLowerCase) &&
            pass.any(Char::isDigit)

fun endOfToday(tz: TimeZone = TimeZone.currentSystemDefault()): Instant {
    val today = Clock.System.now().toLocalDateTime(tz).date
    return LocalDateTime(
        year = today.year,
        month = today.month,
        dayOfMonth = today.dayOfMonth,
        hour = 23,
        minute = 59
    ).toInstant(tz)
}