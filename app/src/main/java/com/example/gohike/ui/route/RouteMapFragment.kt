package com.example.gohike.ui.route

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.gohike.R
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.config.Configuration.*
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.ScaleBarOverlay
import kotlin.math.roundToInt


class RouteMapFragment : Fragment(), MapEventsReceiver {
    private val TAG : String = "RouteMapFragment"

    private lateinit var viewModel: RouteViewModel

    private lateinit var map: MapView
    private lateinit var line: Polyline

    var zoomedToRoute : Boolean = false
    var mapEditing : Boolean = false

    private lateinit var mapController: IMapController
    private lateinit var mapOverlay: LinearLayout
    private lateinit var mapSelectedStat: TextView
    private lateinit var editSaveButton: Button
    private lateinit var clearLastButton: Button
    private lateinit var markerStart : Marker
    private lateinit var markerFinish : Marker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity()).get(RouteViewModel::class.java)

        // Necessary as per osmdroid documentation
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        val view = inflater.inflate(R.layout.fragment_route_map, container, false)

        // setting tile source
        map = view.findViewById<MapView>(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)

        // setting start point and zoom
        mapController = map.controller
        mapController.setZoom(9.5)
        val startPoint = GeoPoint(45.9157634,26.0017784);
        mapController.setCenter(startPoint)

        // necessary to make font size suitable for phone screens
        map.isTilesScaledToDpi = true

        // zoom controller (not working???)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        map.zoomController.setZoomInEnabled(true)
        map.zoomController.setZoomOutEnabled(true)
        map.zoomController.activate()

        // multi-touch
        map.setMultiTouchControls(true);

        // add scale
        val context = this.requireActivity();
        val dm : DisplayMetrics = context.getResources().getDisplayMetrics();
        val mScaleBarOverlay = ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
        //play around with these values to get the location on screen in the right place for your application
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        map.getOverlays().add(mScaleBarOverlay);

        // events to retrieve geopoints from map
        val overlayEvents = MapEventsOverlay(this)
        map.overlays.add(overlayEvents)

        // polyline - trajectory
        line = Polyline()

        // views
        mapOverlay = view.findViewById(R.id.route_map_overlay)
        mapSelectedStat = view.findViewById(R.id.route_map_selected_stat)
        // buttons
        editSaveButton = view.findViewById(R.id.route_map_edit_save_button)
        clearLastButton = view.findViewById(R.id.route_map_clear_last_button)

        // actions
        editSaveButton.setOnClickListener {
            if (mapEditing == false) {
                mapEditing = true
                viewModel.state.value?.let { it1 -> updateRouteState(it1) }
            } else {
                mapEditing = false
                viewModel.state.value?.let { it1 -> updateRouteState(it1) }
                findNavController().navigateUp()
            }
        }

        clearLastButton.setOnClickListener {
            if (mapEditing && viewModel.state.value == RouteState.ROUTE_EDITING) {
                val last = viewModel.editedRoute.routeGeopoints!!.size
                viewModel.editedRoute.routeGeopoints!!.removeAt(last - 1)
                updateRouteStateToEditing()
            }
        }

        viewModel.state.observe(viewLifecycleOwner) {
            updateRouteState(it)
        }

        return view
    }

    fun updateRouteState(routeState: RouteState) {
        when (routeState) {
            RouteState.ROUTE_DISPLAYING -> updateRouteStateToDisplaying()
            RouteState.ROUTE_ERROR -> TODO()
            RouteState.ROUTE_EDITING -> updateRouteStateToEditing()
        }
    }

    fun updateRouteStateToDisplaying() {
        mapOverlay.visibility = View.GONE

        val geoPoints = viewModel.getRouteGeopoints()

        if (!zoomedToRoute && geoPoints.size >= 1) {
            mapController.setCenter(geoPoints[0])
            zoomedToRoute = true
        }

        line.setPoints(geoPoints)

        if (geoPoints.size >= 1) {
            var marker = Marker(map)
            marker.position = geoPoints[0]
            marker.icon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_place_24, null)
            marker.title = "Route start"
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            map.overlays.add(marker)
        }

        if (geoPoints.size >= 2) {
            var marker = Marker(map)
            marker.position = geoPoints[geoPoints.size-1]
            marker.icon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_flag_24, null)
            marker.title = "Route finish"
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            map.overlays.add(marker)
        }

        mapController.setZoom(15)
        map.overlays.add(line);
        map.invalidate()
    }

    fun updateRouteStateToEditing() {
        editSaveButton.visibility = View.VISIBLE

        val geoPoints = viewModel.getRouteGeopoints()
        line.setPoints(geoPoints)

        if (!zoomedToRoute && geoPoints.size >= 1) {
            mapController.setCenter(geoPoints[0])
            zoomedToRoute = true
        }

        if (mapEditing) {
            mapSelectedStat.visibility = View.VISIBLE
            mapSelectedStat.text = "${geoPoints.size} points, ${(line.distance / 100).roundToInt()/10.0}km"

            if (geoPoints.size >= 1) {
                clearLastButton.visibility = View.VISIBLE
            } else {
                clearLastButton.visibility = View.GONE
            }
        } else {
            clearLastButton.visibility = View.GONE
            mapSelectedStat.visibility = View.GONE
        }

        if (this::markerStart.isInitialized)
            map.overlays.remove(markerStart)
        if (this::markerFinish.isInitialized)
            map.overlays.remove(markerFinish)

        if (geoPoints.size >= 1) {
            var markerStart = Marker(map)
            markerStart.position = geoPoints[0]
            markerStart.icon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_place_24, null)
            markerStart.title = "Route start"
            markerStart.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            map.overlays.add(markerStart)
        }

        if (geoPoints.size >= 2) {
            markerFinish = Marker(map)
            markerFinish.position = geoPoints[geoPoints.size-1]
            markerFinish.icon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_flag_24, null)
            markerFinish.title = "Route finish"
            markerFinish.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            map.overlays.add(markerFinish)
        }

        map.overlays.add(line);
        map.invalidate()
    }

    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
        Log.i(TAG, "single tap on $p")
        if (mapEditing && viewModel.state.value == RouteState.ROUTE_EDITING) {
            if (p != null) {
                viewModel.editedRoute.addGeopoint(p)
                updateRouteStateToEditing()
            }
        }
        return true
    }

    override fun longPressHelper(p: GeoPoint?): Boolean {
        TODO("Not yet implemented")
    }

}