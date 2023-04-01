package com.example.gohike.connection.login

interface LoginHandle {
    fun onLoginRequest(event : LoginRequestEvent)
    fun onLoginSuccess(event : LoginSuccessEvent)
    fun onLoginFailure(event : LoginFailureEvent)
}