package com.example.gohike.connection.profile

import com.example.gohike.data.profile.Profile

interface ProfileHandle {
    fun onProfileRequest(event : ProfileRequestEvent) {}
    fun onProfileRetrieveSuccess(event : ProfileRetrievalSuccessEvent) {}
    fun onProfileRetrieveFailure(event : ProfileRetrievalFailureEvent) {}
    fun onProfileSave(event : ProfileSaveEvent) {}
}