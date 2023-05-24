package com.example.gohike.data.route

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.gohike.connection.ConnectionThread
import com.example.gohike.connection.profile.ProfileRetrievalFailureEvent
import com.example.gohike.connection.route.RouteRetrievalSuccessEvent
import com.example.gohike.connection.route.RoutesRetrievalFailureEvent
import com.example.gohike.connection.route.RoutesRetrievalSuccessEvent
import com.google.firebase.annotations.concurrent.UiThread
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.reflect.KFunction

class RouteDataSource() {
    private val TAG: String = "RouteDataSource"
    var getContext: KFunction<Context>? = null

    // Get a reference to our posts
    val database = Firebase.firestore
    var firebaseUser: FirebaseUser? = null

    fun loadRouteList() {
        database.collection("routes").get().addOnSuccessListener {
            val list =
                it.documents.map { document -> Route(document.data).buildUid(document.id) }.toList()
            ConnectionThread.addEvent(RoutesRetrievalSuccessEvent(list))
            Log.i(TAG, "Route list loaded $list")
        }.addOnFailureListener {
            ConnectionThread.addEvent(RoutesRetrievalFailureEvent())
            Log.i(TAG, "Could not get route list: $it")

            val context = getContext?.call()
            Toast.makeText(context, "Could not get route list: $it", Toast.LENGTH_LONG).show()
        }
    }

    fun loadRoute(route: Route) {
        val uid = route.uid ?: return

        database.collection("routes-geodata").document(uid).get().addOnSuccessListener {
            route.geodataFromMutableMap(it.data)
            ConnectionThread.addEvent(RouteRetrievalSuccessEvent(route))
            Log.i(TAG, "Route geodata loaded ${route.routeGeopoints}")
        }
    }

    fun saveRoute(route: Route) {
        val firebaseUserCopy = firebaseUser
        val routeUid = route.uid

        if (firebaseUserCopy == null) {
            ConnectionThread.addEvent(ProfileRetrievalFailureEvent("User not logged in"))
            Log.e(TAG, "User not logged!")
            return
        }

        route.creatorUid = firebaseUserCopy.uid

        firebaseUserCopy.let {
            database.runBatch {
                var routeDocRef: DocumentReference
                var routeGeodataDocRef: DocumentReference

                // save new route
                if (routeUid == null) {
                    routeDocRef = database.collection("routes").document()
                    routeGeodataDocRef =
                        database.collection("routes-geodata").document(routeDocRef.id)
                } else
                // edit old route
                {
                    routeDocRef = database.collection("routes").document(routeUid)
                    routeGeodataDocRef = database.collection("routes-geodata").document(routeUid)
                }

                it.set(routeDocRef, route.toMutableMap())
                it.set(routeGeodataDocRef, route.geodataToMutableMap() as Map<String, Any>)
            }.addOnSuccessListener {
                Log.e(TAG, "Route $route saved!")
                val context = getContext?.call()
                Toast.makeText(context, "Route saved successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Log.w(TAG, "Could not save route: $it")

                val context = getContext?.call()
                Toast.makeText(context, "Could not save route: $it", Toast.LENGTH_LONG).show()
            }
        }
    }
}
