package com.example.gohike.connection.profile

import com.example.gohike.connection.ConnectionThreadEvent
import com.example.gohike.connection.ConnectionThreadEventType
import com.example.gohike.data.profile.Profile

class ProfileRetrievalSuccessEvent(var profile : Profile) : ConnectionThreadEvent(
    ConnectionThreadEventType.PROFILE_RETRIEVAL_SUCCESS) {
}