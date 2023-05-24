package com.example.gohike.data.login

import android.util.Log
import com.example.gohike.connection.ConnectionThread
import com.example.gohike.connection.login.LoginFailureEvent
import com.example.gohike.connection.login.LoginHandle
import com.example.gohike.connection.login.LoginRequestEvent
import com.example.gohike.connection.login.LoginSuccessEvent
import com.example.gohike.connection.register.RegistrationFailureEvent
import com.example.gohike.connection.register.RegistrationHandle
import com.example.gohike.connection.register.RegistrationRequestEvent
import com.example.gohike.connection.register.RegistrationSuccessEvent
import com.example.gohike.data.login.model.LoggedInUser
import com.google.firebase.auth.FirebaseUser


/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) : LoginHandle, RegistrationHandle {
    private val TAG = "LoginRepository"

    // in-memory cache of the loggedInUser object
    var user: FirebaseUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null

        Log.v(TAG, "In Login Repository init 1...")

        ConnectionThread.addLoginHandler(this)
        ConnectionThread.addRegistrationHandler(this)

        Log.v(TAG, "In Login Repository init 2...")
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    override fun onLoginRequest(event: LoginRequestEvent) {


        user?.let {
            // User is already authenticated
            Log.v(TAG, "User is already loged in")
            ConnectionThread.addEvent(LoginSuccessEvent(it))
            return@onLoginRequest
        }

        Log.v(TAG, "Log in attempt...")
        // If user is not authenticated then request login
        // from the data source
        val email = event.email
        val password = event.password
        if (email != null && password != null)
            dataSource.login(email, password)
    }

    override fun onLoginSuccess(event: LoginSuccessEvent) {
        user = event.user
    }

    override fun onRegistrationRequest(event: RegistrationRequestEvent) {
        user?.let {
            // User is already anthenticated
            Log.v(TAG, "User cannot be registered because user is loged in")
            ConnectionThread.addEvent(RegistrationSuccessEvent(it))
            return@onRegistrationRequest
        }

        Log.v(TAG, "Registration attempt...")
        // If user is not registered then request registration
        // from the data source
        dataSource.register(event)
    }

    override fun onRegistrationSuccess(event: RegistrationSuccessEvent) {
        user = event.user
    }
}