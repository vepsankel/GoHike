package com.example.gohike.connection.register

import com.example.gohike.connection.ConnectionThreadEvent
import com.example.gohike.connection.ConnectionThreadEventType

class RegistrationRequestEvent (var email : String, var password : String) : ConnectionThreadEvent(ConnectionThreadEventType.REGISTRATION_REQUEST) {
}