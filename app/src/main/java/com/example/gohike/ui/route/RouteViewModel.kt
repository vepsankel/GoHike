package com.example.gohike.ui.route

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gohike.connection.ConnectionThread
import com.example.gohike.connection.login.LoginHandle
import com.example.gohike.connection.login.LoginRequestEvent
import com.example.gohike.connection.login.LoginSuccessEvent
import com.example.gohike.connection.login.LogoutEvent
import com.example.gohike.connection.route.RouteHandle
import com.example.gohike.connection.route.RouteRequestEvent
import com.example.gohike.connection.route.RouteRetrievalSuccessEvent
import com.example.gohike.connection.route.RouteSaveEvent
import com.example.gohike.data.route.Route
import org.osmdroid.util.GeoPoint

class RouteViewModel : ViewModel(), LoginHandle, RouteHandle {
    val TAG : String = "RouteViewModel"

    var userUid : String? = null
    var state : MutableLiveData<RouteState> = MutableLiveData(RouteState.ROUTE_DISPLAYING)
    var canEditThisRoute : Boolean = false

    var error = MutableLiveData<String>()

    var viewedRoute = Route()
    var editedRoute = Route()

    init {
        ConnectionThread.addRouteHandler(this)
        ConnectionThread.addLoginHandler(this)

        ConnectionThread.addEvent(LoginRequestEvent(null, null))
    }

    private fun getActualRoute() : Route {
        return when (state.value) {
            RouteState.ROUTE_DISPLAYING -> viewedRoute
            RouteState.ROUTE_EDITING -> editedRoute

            // The below does not matter
            RouteState.ROUTE_ERROR,
            null -> viewedRoute
        }
    }

    fun getName() : String {
        return getActualRoute().name ?: ""
    }

    fun getDescription() : String {
        return getActualRoute().description ?: ""
    }

    fun getLocation() : String {
        return getActualRoute().location ?: ""
    }

    fun getDifficulty() : Int {
        return getActualRoute().difficulty ?: 0
    }

    fun getTime() : Int {
        return getActualRoute().time ?: 0
    }

    fun saveRouteLocally(
        name : String?,
        location : String?,
        difficulty : Int?,
        description : String?
    ) {
        editedRoute.name = null
        editedRoute.location = null
        editedRoute.difficulty = null
        editedRoute.description = null

        name?.let {
            editedRoute.name = name
        }

        location?.let {
            editedRoute.location = location
        }

        difficulty?.let {
            editedRoute.difficulty = difficulty
        }

        description?.let {
            editedRoute.description = description
        }
    }

    fun saveRoute(
        name : String?,
        location : String?,
        difficulty : Int?,
        description : String?
    ) {
        saveRouteLocally(name, location, difficulty, description)
        ConnectionThread.addEvent(RouteSaveEvent(editedRoute))
    }

    fun edit() {
        if (viewedRoute.creatorUid == userUid) {
            editedRoute = viewedRoute
            state.postValue(RouteState.ROUTE_EDITING)
            Log.i(TAG, "State posted value ROUTE_EDITING")
        }
    }

    override fun onRouteRetrievalSuccess(event: RouteRetrievalSuccessEvent) {
        viewedRoute = event.route
        if (viewedRoute.creatorUid == userUid) {
            canEditThisRoute = true
            if (viewedRoute.uid == null) {
                editedRoute = viewedRoute
                state.postValue(RouteState.ROUTE_EDITING)
                Log.i(TAG, "State posted value ROUTE_EDITING")
            } else {
                state.postValue(RouteState.ROUTE_DISPLAYING)
                Log.i(TAG, "State posted value ROUTE_EDITING")
            }
        } else {
            canEditThisRoute = false
            state.postValue(RouteState.ROUTE_DISPLAYING)
            Log.i(TAG, "State posted value ROUTE_DISPLAYING")
        }

        if (viewedRoute.routeGeopoints == null) {
            ConnectionThread.addEvent(RouteRequestEvent(viewedRoute))
        }
    }

    fun setTimeInc(increment: Int) {
        if (editedRoute.time == null)
            editedRoute.time = 0

        var time = 0
        editedRoute.time?.let { time = it }

        time += increment

        if (time < 0) time = 0
        if (time > 95) time = 95

        editedRoute.time = time
    }

    override fun onLoginSuccess(event: LoginSuccessEvent) {
        userUid = event.user.uid
    }

    override fun onLogout(event: LogoutEvent) {
        viewedRoute = Route()
        editedRoute = Route()
        userUid = null
        state.postValue(RouteState.ROUTE_DISPLAYING)
    }

    fun getRouteGeopoints(): MutableList<GeoPoint> {
        return getActualRoute().routeGeopoints ?: return ArrayList()
    }
}

enum class RouteState {
    ROUTE_DISPLAYING,
    ROUTE_EDITING,
    ROUTE_ERROR
}