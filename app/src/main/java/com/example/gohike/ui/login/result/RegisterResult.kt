package com.example.gohike.ui.login.result

import com.example.gohike.ui.login.LoggedInUserView

data class RegisterResult(
    val success: LoggedInUserView? = null,
    val error: Int? = null
)
