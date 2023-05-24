package com.example.gohike.connection.login

import com.example.gohike.connection.ConnectionThreadEvent
import com.example.gohike.connection.ConnectionThreadEventType
import com.google.firebase.auth.FirebaseUser

class LoginSuccessEvent(var user : FirebaseUser) : ConnectionThreadEvent(ConnectionThreadEventType.LOGIN_SUCCESS) {
}