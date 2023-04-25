package com.example.gohike.connection.profile

import com.example.gohike.connection.ConnectionThreadEvent
import com.example.gohike.connection.ConnectionThreadEventType

class ProfileRetrievalFailureEvent(var error : String) : ConnectionThreadEvent(
    ConnectionThreadEventType.PROFILE_RETRIEVAL_FAILURE) {
}