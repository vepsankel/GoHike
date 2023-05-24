package com.example.gohike.connection.profile

import com.example.gohike.connection.ConnectionThreadEvent
import com.example.gohike.connection.ConnectionThreadEventType

class ProfileRequestEvent() : ConnectionThreadEvent(ConnectionThreadEventType.PROFILE_REQUEST) {
}