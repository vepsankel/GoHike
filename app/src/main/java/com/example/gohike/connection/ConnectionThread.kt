package com.example.gohike.connection

import android.util.Log
import com.example.gohike.connection.login.LoginFailureEvent
import com.example.gohike.connection.login.LoginHandle
import com.example.gohike.connection.login.LoginRequestEvent
import com.example.gohike.connection.login.LoginSuccessEvent
import com.example.gohike.connection.register.RegistrationFailureEvent
import com.example.gohike.connection.register.RegistrationHandle
import com.example.gohike.connection.register.RegistrationRequestEvent
import com.example.gohike.connection.register.RegistrationSuccessEvent
import com.example.gohike.data.login.LoginDataSource
import com.example.gohike.data.login.LoginRepository
import com.example.gohike.ui.login.LoginViewModel
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

object ConnectionThread {
    var queue : BlockingQueue<ConnectionThreadEvent> = LinkedBlockingQueue()
    private var isRunning : Boolean = false
    private const val TAG : String = "ConnectionThread"

    // handlers for log in
    private var loginHandlers : HashSet<LoginHandle> = HashSet()
    private var registrationHandlers : HashSet<RegistrationHandle> = HashSet()

    // the only class that initializes the data source
    private var loginDataSource : LoginDataSource = LoginDataSource()
    private var loginRepository : LoginRepository = LoginRepository(loginDataSource)

    // view Model
    private var loginViewModel : LoginViewModel? = null

    private fun initialize() {
        isRunning = true
        //loginHandlers.clear()
        //registrationHandlers.clear()
        Log.v(TAG, "ConnectionThread started")
    }

    fun run() {
        if (isRunning) {
            Log.w(TAG, "ConnectionThread is already running")
        } else {
            initialize()
        }

        while (isRunning) {
            val event : ConnectionThreadEvent = queue.take()

            Log.v(TAG, "Event taken $event")

            when (event.getEventType()) {
                ConnectionThreadEventType.LOGIN_REQUEST,
                ConnectionThreadEventType.LOGIN_SUCCESS,
                ConnectionThreadEventType.LOGIN_FAILURE -> handleLoginEvent(event)

                ConnectionThreadEventType.REGISTRATION_REQUEST,
                ConnectionThreadEventType.REGISTRATION_SUCCESS,
                ConnectionThreadEventType.REGISTRATION_FAILURE -> handleRegisterEvent(event)

                else -> {Log.w(TAG, "Illegal event type")}
            }

        }
    }

    fun addEvent (event : ConnectionThreadEvent) {
        queue.add(event)
        Log.v(TAG, "Event added")
    }

    fun removeLoginHandler (handler : LoginHandle) {
        loginHandlers.remove(handler)
        Log.v(TAG, "Login handle removed")
    }

    fun addLoginHandler (handler : LoginHandle) {
        loginHandlers.add(handler)
        Log.v(TAG, "Login handle added $handler")
    }

    fun addRegistrationHandler (handler : RegistrationHandle) {
        registrationHandlers.add(handler)
        Log.v(TAG, "Registration handle added $handler")
    }

    fun handleLoginEvent (event : ConnectionThreadEvent) {
        Log.v(TAG, "Login event")
        for (loginHandle in loginHandlers) {
            Log.v(TAG, "Send event to handler $loginHandle")
            when (event.getEventType()) {
                ConnectionThreadEventType.LOGIN_SUCCESS -> loginHandle.onLoginSuccess(event as LoginSuccessEvent)
                ConnectionThreadEventType.LOGIN_FAILURE -> loginHandle.onLoginFailure(event as LoginFailureEvent)
                ConnectionThreadEventType.LOGIN_REQUEST -> loginHandle.onLoginRequest(event as LoginRequestEvent)
                else -> Log.w(TAG, "LogIn event illegal state modification")
            }
        }
    }

    fun handleRegisterEvent (event : ConnectionThreadEvent) {
        Log.v(TAG, "Registration event")
        for (registrationHandle in registrationHandlers) {
            when (event.getEventType()) {
                ConnectionThreadEventType.REGISTRATION_SUCCESS -> registrationHandle.onRegistrationSuccess(event as RegistrationSuccessEvent)
                ConnectionThreadEventType.REGISTRATION_FAILURE -> registrationHandle.onRegistrationFailure(event as RegistrationFailureEvent)
                ConnectionThreadEventType.REGISTRATION_REQUEST -> registrationHandle.onRegistrationRequest(event as RegistrationRequestEvent)
                else -> Log.v(TAG, "Register event illegal state modification")
            }
        }
    }

    fun setLoginViewModel (model : LoginViewModel) {
        loginViewModel = model
    }
}