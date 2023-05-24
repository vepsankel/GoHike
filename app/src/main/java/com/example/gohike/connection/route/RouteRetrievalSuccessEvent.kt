package com.example.gohike.connection.route

import com.example.gohike.connection.ConnectionThreadEvent
import com.example.gohike.connection.ConnectionThreadEventType
import com.example.gohike.data.route.Route

class RouteRetrievalSuccessEvent(var route : Route) : ConnectionThreadEvent(ConnectionThreadEventType.ROUTE_RETRIEVAL_SUCCESS){

}
