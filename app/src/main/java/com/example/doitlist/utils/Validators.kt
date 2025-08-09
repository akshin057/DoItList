package com.example.doitlist.utils

fun checkEmail(email: String): Boolean =
    Regex("""^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.(ru|com)$""").matches(email)

fun checkPassword(pass: String): Boolean =
    pass.length >= 8 &&
            pass.any(Char::isUpperCase) &&
            pass.any(Char::isLowerCase) &&
            pass.any(Char::isDigit)
