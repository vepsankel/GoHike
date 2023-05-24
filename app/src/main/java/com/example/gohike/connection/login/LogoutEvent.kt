package com.example.gohike.connection.login

import com.example.gohike.connection.ConnectionThreadEvent
import com.example.gohike.connection.ConnectionThreadEventType

class LogoutEvent() : ConnectionThreadEvent(ConnectionThreadEventType.LOGOUT_REQUEST) {
}