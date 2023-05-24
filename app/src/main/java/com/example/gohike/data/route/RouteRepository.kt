package com.example.gohike.data.route

import com.example.gohike.connection.ConnectionThread
import com.example.gohike.connection.login.LoginFailureEvent
import com.example.gohike.connection.login.LoginHandle
import com.example.gohike.connection.login.LoginSuccessEvent
import com.example.gohike.connection.route.RouteHandle
import com.example.gohike.connection.route.RouteRequestEvent
import com.example.gohike.connection.route.RouteSaveEvent
import com.example.gohike.connection.route.RoutesRequestEvent

class RouteRepository(val dataSource: RouteDataSource) : RouteHandle, LoginHandle {

    init {
        ConnectionThread.addRouteHandler(this)
        ConnectionThread.addLoginHandler(this)
    }

    override fun onRoutesRequest(event: RoutesRequestEvent) {
        dataSource.loadRouteList()
    }

    override fun onRouteSave(event: RouteSaveEvent) {
        dataSource.saveRoute(event.route)
    }

    override fun onRouteRequest(event: RouteRequestEvent) {
        dataSource.loadRoute(event.route)
    }

    override fun onLoginSuccess(event: LoginSuccessEvent) {
        dataSource.firebaseUser = event.user
    }

    override fun onLoginFailure(event: LoginFailureEvent) {
        dataSource.firebaseUser = null
    }
}