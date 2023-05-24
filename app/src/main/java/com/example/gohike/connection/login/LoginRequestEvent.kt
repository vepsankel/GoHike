package com.example.gohike.connection.login

import com.example.gohike.connection.ConnectionThreadEvent
import com.example.gohike.connection.ConnectionThreadEventType
import com.google.firebase.auth.FirebaseUser

class LoginRequestEvent(var email : String?, var password : String?) : ConnectionThreadEvent(
    ConnectionThreadEventType.LOGIN_REQUEST
) {
}