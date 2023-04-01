package com.example.gohike.connection

open class ConnectionThreadEvent(
    private var eventType: ConnectionThreadEventType,
) {

    fun getEventType() : ConnectionThreadEventType {
        return eventType
    }
}