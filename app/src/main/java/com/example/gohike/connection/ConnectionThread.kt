package com.example.gohike.connection

import android.content.Context
import android.util.Log
import com.example.gohike.connection.login.LoginFailureEvent
import com.example.gohike.connection.login.LoginHandle
import com.example.gohike.connection.login.LoginRequestEvent
import com.example.gohike.connection.login.LoginSuccessEvent
import com.example.gohike.connection.login.LogoutEvent
import com.example.gohike.connection.profile.ProfileHandle
import com.example.gohike.connection.profile.ProfileRequestEvent
import com.example.gohike.connection.profile.ProfileRetrievalFailureEvent
import com.example.gohike.connection.profile.ProfileRetrievalSuccessEvent
import com.example.gohike.connection.profile.ProfileSaveEvent
import com.example.gohike.connection.register.RegistrationFailureEvent
import com.example.gohike.connection.register.RegistrationHandle
import com.example.gohike.connection.register.RegistrationRequestEvent
import com.example.gohike.connection.register.RegistrationSuccessEvent
import com.example.gohike.connection.route.RouteHandle
import com.example.gohike.connection.route.RouteRequestEvent
import com.example.gohike.connection.route.RouteRetrievalFailureEvent
import com.example.gohike.connection.route.RouteRetrievalSuccessEvent
import com.example.gohike.connection.route.RouteSaveEvent
import com.example.gohike.connection.route.RoutesRequestEvent
import com.example.gohike.connection.route.RoutesRetrievalFailureEvent
import com.example.gohike.connection.route.RoutesRetrievalSuccessEvent
import com.example.gohike.data.login.LoginDataSource
import com.example.gohike.data.login.LoginRepository
import com.example.gohike.data.notification.NotificationSource
import com.example.gohike.data.profile.ProfileDataSource
import com.example.gohike.data.profile.ProfileRepository
import com.example.gohike.data.route.RouteDataSource
import com.example.gohike.data.route.RouteRepository
import com.example.gohike.ui.login.LoginViewModel
import java.util.Collections
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue
import kotlin.reflect.KFunction0

object ConnectionThread {
    private var getContext : (KFunction0<Context>)? = null
    var queue : BlockingQueue<ConnectionThreadEvent> = LinkedBlockingQueue()
    private var isRunning : Boolean = false
    private const val TAG : String = "ConnectionThread"

    // handlers for log in
    private var loginHandlers : MutableSet<LoginHandle> = Collections.newSetFromMap(ConcurrentHashMap())
    private var registrationHandlers : MutableSet<RegistrationHandle> = Collections.newSetFromMap(ConcurrentHashMap())
    private var profileHandlers : MutableSet<ProfileHandle> = Collections.newSetFromMap(ConcurrentHashMap())
    private var routeHandlers : MutableSet<RouteHandle> = Collections.newSetFromMap(ConcurrentHashMap())

    // the only class that initializes the data source
    private var loginDataSource : LoginDataSource = LoginDataSource()
    private var loginRepository : LoginRepository = LoginRepository(loginDataSource)
    private var profileDataSource : ProfileDataSource = ProfileDataSource()
    private var profileRepository : ProfileRepository = ProfileRepository(profileDataSource)
    private var routeDataSource : RouteDataSource = RouteDataSource()
    private var routeRepository : RouteRepository = RouteRepository(routeDataSource)

    private var notificationSource : NotificationSource = NotificationSource()

    // view Model
    private var loginViewModel : LoginViewModel? = null

    private fun initialize() {
        isRunning = true
        //loginHandlers.clear()
        //registrationHandlers.clear()
        Log.v(TAG, "ConnectionThread started")
    }

    fun run(getContext: KFunction0<Context>) {
        if (isRunning) {
            Log.w(TAG, "ConnectionThread is already running")
        } else {
            initialize()
        }

        this.getContext = getContext
        routeDataSource.getContext = this.getContext
        notificationSource.getContext = this.getContext

        while (isRunning) {
            val event : ConnectionThreadEvent = queue.take()

            Log.v(TAG, "Event taken $event")

            when (event.getEventType()) {
                ConnectionThreadEventType.LOGIN_REQUEST,
                ConnectionThreadEventType.LOGIN_SUCCESS,
                ConnectionThreadEventType.LOGIN_FAILURE,
                ConnectionThreadEventType.LOGOUT_REQUEST -> handleLoginEvent(event)

                ConnectionThreadEventType.REGISTRATION_REQUEST,
                ConnectionThreadEventType.REGISTRATION_SUCCESS,
                ConnectionThreadEventType.REGISTRATION_FAILURE -> handleRegisterEvent(event)

                ConnectionThreadEventType.PROFILE_REQUEST,
                ConnectionThreadEventType.PROFILE_RETRIEVAL_SUCCESS,
                ConnectionThreadEventType.PROFILE_RETRIEVAL_FAILURE,
                ConnectionThreadEventType.PROFILE_SAVE -> handleProfileEvent(event)

                ConnectionThreadEventType.ROUTES_REQUEST,
                ConnectionThreadEventType.ROUTES_RETRIEVAL_SUCCESS,
                ConnectionThreadEventType.ROUTES_RETRIEVAL_FAILURE,
                ConnectionThreadEventType.ROUTE_REQUEST,
                ConnectionThreadEventType.ROUTE_RETRIEVAL_SUCCESS,
                ConnectionThreadEventType.ROUTE_RETRIEVAL_FAILURE,
                ConnectionThreadEventType.ROUTE_SAVE -> handleRouteEvent(event)

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

    fun addProfileHandler (handler : ProfileHandle) {
        profileHandlers.add(handler)
        Log.v(TAG, "Profile handle added $handler")
    }

    fun addRouteHandler (handler : RouteHandle) {
        routeHandlers.add(handler)
        Log.v(TAG, "Route handle added $handler")
    }

    fun handleLoginEvent (event : ConnectionThreadEvent) {
        Log.v(TAG, "Login event")
        for (loginHandle in loginHandlers) {
            Log.v(TAG, "Send event to handler $loginHandle")
            when (event.getEventType()) {
                ConnectionThreadEventType.LOGIN_SUCCESS -> loginHandle.onLoginSuccess(event as LoginSuccessEvent)
                ConnectionThreadEventType.LOGIN_FAILURE -> loginHandle.onLoginFailure(event as LoginFailureEvent)
                ConnectionThreadEventType.LOGIN_REQUEST -> loginHandle.onLoginRequest(event as LoginRequestEvent)
                ConnectionThreadEventType.LOGOUT_REQUEST -> loginHandle.onLogout(event as LogoutEvent)
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

    fun handleProfileEvent (event : ConnectionThreadEvent) {
        Log.v(TAG, "Profile event")
        for (profileHandle in profileHandlers) {
            when (event.getEventType()) {
                ConnectionThreadEventType.PROFILE_REQUEST -> profileHandle.onProfileRequest(event as ProfileRequestEvent)
                ConnectionThreadEventType.PROFILE_RETRIEVAL_SUCCESS -> profileHandle.onProfileRetrieveSuccess(event as ProfileRetrievalSuccessEvent)
                ConnectionThreadEventType.PROFILE_RETRIEVAL_FAILURE -> profileHandle.onProfileRetrieveFailure(event as ProfileRetrievalFailureEvent)
                ConnectionThreadEventType.PROFILE_SAVE -> profileHandle.onProfileSave(event as ProfileSaveEvent)
                else -> Log.v(TAG, "Profile event illegal state modification")
            }
        }
    }

    fun handleRouteEvent (event : ConnectionThreadEvent) {
        Log.v(TAG, "ROute event")
        for (routeHandle in routeHandlers) {
            when (event.getEventType()) {
                ConnectionThreadEventType.ROUTES_REQUEST -> routeHandle.onRoutesRequest(event as RoutesRequestEvent)
                ConnectionThreadEventType.ROUTES_RETRIEVAL_SUCCESS -> routeHandle.onRoutesRetrievalSuccess(event as RoutesRetrievalSuccessEvent)
                ConnectionThreadEventType.ROUTES_RETRIEVAL_FAILURE -> routeHandle.onRoutesRetrievalFailure(event as RoutesRetrievalFailureEvent)
                ConnectionThreadEventType.ROUTE_REQUEST -> routeHandle.onRouteRequest(event as RouteRequestEvent)
                ConnectionThreadEventType.ROUTE_RETRIEVAL_SUCCESS -> routeHandle.onRouteRetrievalSuccess(event as RouteRetrievalSuccessEvent)
                ConnectionThreadEventType.ROUTE_RETRIEVAL_FAILURE -> routeHandle.onRouteRetrievalFailure(event as RouteRetrievalFailureEvent)
                ConnectionThreadEventType.ROUTE_SAVE -> routeHandle.onRouteSave(event as RouteSaveEvent)
                else -> Log.v(TAG, "Route event illegal state modification")
            }
        }
    }

    fun setLoginViewModel (model : LoginViewModel) {
        loginViewModel = model
    }
}