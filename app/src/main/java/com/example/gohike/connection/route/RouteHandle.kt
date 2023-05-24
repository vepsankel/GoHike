package com.example.gohike.connection.route

interface RouteHandle {
    /**
     * Requests a list of Route.
     * For each element of the route only the basic information
     * will be provided.
     */
    fun onRoutesRequest(event: RoutesRequestEvent) {}

    /**
     * Requesting a list of Route ended up successfully.
     * For each element of the route only the basic information
     * is provided.
     */
    fun onRoutesRetrievalSuccess(event: RoutesRetrievalSuccessEvent) {}
    fun onRoutesRetrievalFailure(event: RoutesRetrievalFailureEvent) {}

    /**
     * Requests additional information about route.
     */
    fun onRouteRequest(event: RouteRequestEvent) {}

    /**
     * Route additional information request ended up successfully.
     */
    fun onRouteRetrievalSuccess(event: RouteRetrievalSuccessEvent) {}
    fun onRouteRetrievalFailure(event: RouteRetrievalFailureEvent) {}
    fun onRouteSave(event: RouteSaveEvent) {}
}