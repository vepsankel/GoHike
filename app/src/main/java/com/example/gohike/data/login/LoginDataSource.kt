package com.example.gohike.data.login

import android.content.ContentValues.TAG
import android.util.Log
import com.example.gohike.connection.ConnectionThread
import com.example.gohike.connection.login.LoginFailureEvent
import com.example.gohike.connection.login.LoginHandle
import com.example.gohike.connection.login.LoginRequestEvent
import com.example.gohike.connection.login.LoginSuccessEvent
import com.example.gohike.connection.register.RegistrationFailureEvent
import com.example.gohike.connection.register.RegistrationRequestEvent
import com.example.gohike.connection.register.RegistrationSuccessEvent
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    // Firebase Authentification
    private var auth: FirebaseAuth = Firebase.auth

    fun logout() {
        // TODO: revoke authentication
    }

    fun login(email : String, password : String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    var loginSuccessEvent : LoginSuccessEvent = LoginSuccessEvent(auth.currentUser!!)
                    ConnectionThread.addEvent(loginSuccessEvent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    var loginFailureEvent : LoginFailureEvent = LoginFailureEvent(auth.currentUser!!)
                    ConnectionThread.addEvent(loginFailureEvent)
                }
            }
    }

    fun register(event: RegistrationRequestEvent) {
        auth.createUserWithEmailAndPassword(event.email, event.password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    ConnectionThread.addEvent(RegistrationSuccessEvent(user = auth.currentUser!!))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    ConnectionThread.addEvent(RegistrationFailureEvent())
                }
            }
    }
}