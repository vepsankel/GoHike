package com.example.gohike.connection.route

import com.example.gohike.connection.ConnectionThreadEvent
import com.example.gohike.connection.ConnectionThreadEventType
import com.example.gohike.data.route.Route

class RoutesRetrievalSuccessEvent(var list: List<Route>) : ConnectionThreadEvent(ConnectionThreadEventType.ROUTES_RETRIEVAL_SUCCESS){

}