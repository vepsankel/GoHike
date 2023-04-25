package com.example.gohike.connection.register

interface RegistrationHandle {
    fun onRegistrationFailure(event: RegistrationFailureEvent) {}
    fun onRegistrationSuccess(event: RegistrationSuccessEvent) {}
    fun onRegistrationRequest(event: RegistrationRequestEvent) {}
}