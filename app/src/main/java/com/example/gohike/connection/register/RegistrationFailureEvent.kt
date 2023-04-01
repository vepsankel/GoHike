package com.example.gohike.connection.register

import com.example.gohike.connection.ConnectionThreadEvent
import com.example.gohike.connection.ConnectionThreadEventType

class RegistrationFailureEvent : ConnectionThreadEvent(ConnectionThreadEventType.REGISTRATION_FAILURE) {
}