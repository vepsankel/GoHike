package com.example.gohike.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.example.gohike.data.login.LoginRepository

import com.example.gohike.R
import com.example.gohike.connection.ConnectionThread
import com.example.gohike.connection.login.LoginFailureEvent
import com.example.gohike.connection.login.LoginHandle
import com.example.gohike.connection.login.LoginRequestEvent
import com.example.gohike.connection.login.LoginSuccessEvent
import com.example.gohike.connection.register.RegistrationFailureEvent
import com.example.gohike.connection.register.RegistrationHandle
import com.example.gohike.connection.register.RegistrationRequestEvent
import com.example.gohike.connection.register.RegistrationSuccessEvent
import com.example.gohike.ui.login.result.LoginResult

class LoginViewModel() : ViewModel(), LoginHandle, RegistrationHandle {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    init {
        ConnectionThread.addLoginHandler(this)
        ConnectionThread.addRegistrationHandler(this)
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    fun requestRegistration(email: String, password: String) {
        ConnectionThread.addEvent(RegistrationRequestEvent(email, password))
    }

    fun requestLogin(username: String, password: String) {
        ConnectionThread.addEvent(LoginRequestEvent(username, password))
    }

    override fun onLoginRequest(event: LoginRequestEvent) {
        // Do nothing
    }

    override fun onLoginSuccess(event: LoginSuccessEvent) {
        _loginResult.postValue(LoginResult(success = LoggedInUserView(displayName = event.user.toString()), error = null))
    }

    override fun onLoginFailure(event: LoginFailureEvent) {
        _loginResult.postValue(LoginResult(success = null, error = R.string.login_failed))
    }

    override fun onRegistrationRequest(event: RegistrationRequestEvent) {
        // Do nothing
    }

    override fun onRegistrationSuccess(event: RegistrationSuccessEvent) {
        _loginResult.postValue(LoginResult(success = LoggedInUserView(displayName = event.user.toString()), error = null))
    }

    override fun onRegistrationFailure(event: RegistrationFailureEvent) {
        _loginResult.postValue(LoginResult(success = null, error = R.string.registration_failed))
    }
}