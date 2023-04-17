package com.example.gohike.data.profile

import com.example.gohike.connection.login.LoginFailureEvent
import com.example.gohike.connection.login.LoginHandle
import com.example.gohike.connection.login.LoginRequestEvent
import com.example.gohike.connection.login.LoginSuccessEvent
import com.example.gohike.connection.register.RegistrationFailureEvent
import com.example.gohike.connection.register.RegistrationHandle
import com.example.gohike.connection.register.RegistrationRequestEvent
import com.example.gohike.connection.register.RegistrationSuccessEvent

class ProfileRepository(val dataSource: ProfileDataSource) : LoginHandle, RegistrationHandle {
    override fun onLoginSuccess(event: LoginSuccessEvent) {
        dataSource.loadProfileData()
    }

    override fun onRegistrationSuccess(event: RegistrationSuccessEvent) {
        dataSource.loadProfileData()
    }

    // UNUSED
    override fun onLoginRequest(event: LoginRequestEvent) {}
    override fun onLoginFailure(event: LoginFailureEvent) {}
    override fun onRegistrationFailure(event: RegistrationFailureEvent) {}
    override fun onRegistrationRequest(event: RegistrationRequestEvent) {}

}