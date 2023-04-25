package com.example.gohike.data.profile

import com.example.gohike.connection.ConnectionThread
import com.example.gohike.connection.login.LoginFailureEvent
import com.example.gohike.connection.login.LoginHandle
import com.example.gohike.connection.login.LoginSuccessEvent
import com.example.gohike.connection.profile.ProfileHandle
import com.example.gohike.connection.profile.ProfileRequestEvent
import com.example.gohike.connection.profile.ProfileSaveEvent
import com.example.gohike.connection.register.RegistrationHandle
import com.example.gohike.connection.register.RegistrationSuccessEvent

class ProfileRepository(val dataSource: ProfileDataSource) : LoginHandle, RegistrationHandle, ProfileHandle {

    init {
        ConnectionThread.addLoginHandler(this)
        ConnectionThread.addRegistrationHandler(this)
        ConnectionThread.addProfileHandler(this)
        dataSource.firebaseUser = null
    }

    override fun onProfileSave(event: ProfileSaveEvent) {
        dataSource.saveProfileData(event.profile)
    }
    override fun onLoginSuccess(event: LoginSuccessEvent) {
        dataSource.firebaseUser = event.user
        dataSource.loadProfileData()
    }

    override fun onLoginFailure(event: LoginFailureEvent) {
        dataSource.firebaseUser = null
    }

    override fun onRegistrationSuccess(event: RegistrationSuccessEvent) {
        dataSource.firebaseUser = event.user
        dataSource.loadProfileData()
    }

    override fun onProfileRequest(event: ProfileRequestEvent) {
        dataSource.loadProfileData()
    }
}