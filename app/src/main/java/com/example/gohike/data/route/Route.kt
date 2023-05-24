package com.example.gohike.data.route

import com.google.firebase.firestore.DocumentSnapshot
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline

/**
 * This class represents a route.
 *
 */

class Route {
    var uid: String? = null
    var creatorUid: String? = null

    var name: String? = null
    var location: String? = null
    var description: String? = null
    var difficulty: Int? = null
    var time: Int? = null

    var routeGeopoints : ArrayList<GeoPoint>? = null

    constructor() {}

    constructor(hashMap: MutableMap<String, Any>?) {
        fromMutableMap(hashMap)
    }

    fun buildUid(uid : String) : Route {
        this.uid = uid
        return this
    }

    fun buildCreatorUid(uid : String) : Route {
        this.creatorUid = uid
        return this
    }

    fun getDistance() : Double {
        val geoPoints = routeGeopoints ?: return 0.0

        val p = Polyline()
        p.setPoints(geoPoints)
        return p.distance
    }

    fun toMutableMap(): HashMap<String, Any> {
        var map = hashMapOf<String, Any>()

        val creatorUid = creatorUid
        val name = name
        val location = location
        val description = description
        val difficulty = difficulty
        val time = time

        creatorUid?.let { map["creatorUid"] = creatorUid }
        name?.let { map["name"] = name }
        location?.let { map["location"] = location }
        description?.let { map["description"] = description }
        difficulty?.let { map["difficulty"] = difficulty }
        time?.let { map["time"] = time }

        return map
    }

    fun geodataToMutableMap(): HashMap<String, HashMap<String, Any>> {

        val list: HashMap<String, HashMap<String, Any>> = HashMap()
        var index = 0;

        if (routeGeopoints == null) return list

        for (point : GeoPoint in routeGeopoints!!) {
            val map = hashMapOf<String, Any>()
            map["latitude"] = point.latitude
            map["longitude"] = point.longitude
            map["altitude"] = point.altitude
            list[index.toString()] = map
            index++
        }

        return list
    }

    fun fromMutableMap(hashMap: MutableMap<String, Any>?) {
        if (hashMap == null)
            return

        val longDifficulty = hashMap["difficulty"] as Long?
        val longTime = hashMap["time"] as Long?

        creatorUid = hashMap["creatorUid"] as String?
        name = hashMap["name"] as String?
        location = hashMap["location"] as String?
        description = hashMap["description"] as String?
        longTime?.let {
            time = longTime.toInt()
        }

        longDifficulty?.let {
            difficulty = longDifficulty.toInt()
        }
    }

    fun geodataFromMutableMap(hashMap: MutableMap<String, Any>?) {
        var geopoints = routeGeopoints

        if (geopoints == null)
            geopoints = ArrayList()

        if (hashMap == null) {
            routeGeopoints = geopoints
            return
        }

        var index = 0
        geopoints.clear()

        while (hashMap[index.toString()] != null) {
            val geoPointData : HashMap<String, Double> = hashMap[index.toString()] as HashMap<String, Double>
            var point : GeoPoint? = null

            val latitude = geoPointData["latitude"]
            val longitude = geoPointData["longitude"]
            val altitude = geoPointData["altitude"]


            if (latitude != null && longitude != null) {
                if (altitude != null) {
                    point = GeoPoint(latitude, longitude, altitude)
                } else {
                    point = GeoPoint(latitude, longitude, 0.0)
                }
            }

            if (point != null) {
                geopoints.add(point)
            }
            index++
        }

        routeGeopoints = geopoints
    }

    fun addGeopoint(p: GeoPoint) {
        var geopoints = routeGeopoints

        if (geopoints == null)
            geopoints = ArrayList()
        geopoints.add(p)

        routeGeopoints = geopoints
    }
}