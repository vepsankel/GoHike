package com.example.gohike.connection.register

import com.example.gohike.connection.ConnectionThreadEvent
import com.example.gohike.connection.ConnectionThreadEventType
import com.google.firebase.auth.FirebaseUser

class RegistrationSuccessEvent(var user : FirebaseUser) : ConnectionThreadEvent(ConnectionThreadEventType.REGISTRATION_SUCCESS) {

}