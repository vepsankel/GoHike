package com.example.gohike.ui.route

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gohike.connection.ConnectionThread
import com.example.gohike.connection.login.LoginHandle
import com.example.gohike.connection.login.LoginRequestEvent
import com.example.gohike.connection.login.LoginSuccessEvent
import com.example.gohike.connection.login.LogoutEvent
import com.example.gohike.connection.route.RouteHandle
import com.example.gohike.connection.route.RoutesRequestEvent
import com.example.gohike.connection.route.RoutesRetrievalSuccessEvent
import com.example.gohike.data.route.Route

class RouteListViewModel : ViewModel(), RouteHandle, LoginHandle {
    var list : MutableLiveData<List<Route>> = MutableLiveData()
    var uid : MutableLiveData<String?> = MutableLiveData(null)

    init {
        ConnectionThread.addRouteHandler(this)
        ConnectionThread.addLoginHandler(this)
        ConnectionThread.addEvent(LoginRequestEvent(null, null))
    }

    fun getRouteList() {
        ConnectionThread.addEvent(RoutesRequestEvent())
    }

    override fun onRoutesRetrievalSuccess(event: RoutesRetrievalSuccessEvent) {
        list.postValue(event.list)
    }

    override fun onLoginSuccess(event: LoginSuccessEvent) {
        uid.postValue(event.user.uid)
    }

    override fun onLogout(event: LogoutEvent) {
        uid.postValue(null)
    }
}
