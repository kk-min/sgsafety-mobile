package com.example.sg_safety_mobile.Logic

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.sg_safety_mobile.R
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 *Manager of the OSMap that interacts with maps
 *
 * @since 2022-4-15
 */
class OSMapActivityManager(val context: Context,val map:MapView) {
    /**
     *(context)
     * Application context
     */

    /**
     *(map)
     * Map view of current activity or fragment
     */

    /**
     *Location service class
     */
    private val locationService: LocationService=LocationService()
    /**
     *Intent of location service
     */
    private var locationServiceIntent: Intent? = null
    /**
     *Location Manager
     */
    private lateinit var lm: LocationManager

    /**
     *Add new marker into the map
     *
     * @param map map to be updated
     * @param point geopoint to add marker
     * @param title title and id of the marker
     */
    fun addMarker(map: MapView?, point: GeoPoint, title: String) {

        val startMarker = Marker(map)
        startMarker.position = point
        startMarker.title = title
        startMarker.id= title
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map?.overlays?.add(startMarker)
        map?.invalidate()
    }

    /**
     *Delete existing marker on map
     *
     * @param map map to be updated
     * @param id id of marker to be deleted
     */
    private fun deleteMarker(map:MapView, id:String){
        for (i in 0 until map.overlays.size) {
            val overlay: Overlay = map.overlays[i]
            if (overlay is Marker && overlay.id == id) {
                map.overlays.remove(overlay)
            }
        }
    }

    /**
     *Update location of the marker
     *
     * @param map map to be updated
     * @param loc new location
     * @param id id of marker to be updated
     */
    fun updateMarker(map:MapView,loc: Location?,id:String){
        //Delete marker if applicable, then insert new currentLocation marker
//        if (currentMarker != null){
//            Log.d("CZ2006:LocationService", "prev Location deleted")
//            mapView?.overlays?.remove(currentMarker)
//        }
        try{
            deleteMarker(map,id)

            val point: GeoPoint? = loc?.let { GeoPoint(it.latitude, loc.longitude) }


            val currentMarker = Marker(map)
            currentMarker.position = point
            currentMarker.title = "Current Location"
            currentMarker.id ="Current Location"
            currentMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            val mapController = map.controller
            mapController.animateTo(point)
            map.overlays?.add(currentMarker)
            map.invalidate()
            Log.d("CZ2006:LocationService", "New location set and marker added")
        }
        catch(e:Exception) {
            e.printStackTrace()
            Log.e("CZ2006:LocationService", "Error adding marker")
        }

    }

    /**
     *Draw routes between two points
     *
     * @param map map to be updated
     * @param startPoint startpoint of the route
     * @param endPoint endpoint of the route
     */
    fun addingWaypoints(map: MapView?, startPoint: GeoPoint, endPoint: GeoPoint) {
        val roadManager = OSRMRoadManager(context, "MYUSERAGENT")
        roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT)

        val waypoints = ArrayList<GeoPoint>()
        waypoints.add(startPoint)
        waypoints.add(endPoint)

        MyRoadAsyncTask(roadManager, waypoints).execute()

        Observable.fromCallable {
            retrievingRoad(roadManager, waypoints)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }, {

            }, {
                map?.invalidate()
            })

        //addMarker(map, endPoint, "End Point")
    }

    /**
     *Retrieve the route calculated
     *
     * @param roadManager OSMap road manager
     * @param waypoints waypoint of the route
     */
    private fun retrievingRoad(roadManager: OSRMRoadManager, waypoints: ArrayList<GeoPoint>) {
        // Retrieving road
        roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT)
        val road = roadManager.getRoad(waypoints)
        val roadOverlay = RoadManager.buildRoadOverlay(road)
        map.overlays?.add(roadOverlay)

        val nodeIcon = map.context?.resources?.getDrawable(R.mipmap.walk)
        for (i in 0 until road.mNodes.size) {
            val node = road.mNodes[i]
            val nodeMarker = Marker(map)
            nodeMarker.position = node.mLocation
            if(i!=0&&i!=road.mNodes.size-1) {
                nodeMarker.icon = nodeIcon
            }
            nodeMarker.title = "Step $i"
            map.overlays?.add(nodeMarker)
            nodeMarker.snippet = node.mInstructions
            nodeMarker.subDescription =
                Road.getLengthDurationText(map.context, node.mLength, node.mDuration)
        }
    }

    /**
     * Async task of getting route in background
     */
    private inner class MyRoadAsyncTask(
        val roadManager: OSRMRoadManager,
        val waypoints: ArrayList<GeoPoint>
    ) : AsyncTask<Void, Void, String>() {

        /**
         *Get the route between two points in background
         */
        override fun doInBackground(vararg params: Void?): String? {
            roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT)
            val road = roadManager.getRoad(waypoints)
            val roadOverlay = RoadManager.buildRoadOverlay(road)
            map.overlays?.add(roadOverlay)

            val nodeIcon = map.context?.resources?.getDrawable(R.mipmap.walk)
            for (i in 0 until road.mNodes.size) {
                val node = road.mNodes[i]
                val nodeMarker = Marker(map)
                nodeMarker.position = node.mLocation
                if(i!=0&&i!=road.mNodes.size-1) {
                    nodeMarker.icon = nodeIcon
                }
                nodeMarker.title = "Step $i"
                map.overlays?.add(nodeMarker)
                nodeMarker.snippet = node.mInstructions
                nodeMarker.subDescription =
                    Road.getLengthDurationText(map.context, node.mLength, node.mDuration)
            }

            return null
        }
    }

    /**
     *Get user current location
     *
     * @return user location
     */
    fun getCurrentLocation(): Location {
        lateinit var loc: Location
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return loc
        }
        lm=context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        loc= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
        return loc
    }

    /**
     *Start the location service updates
     */
    fun startLocationService(){
        Log.d("CZ2006:MainActivityManager", "LocationService Starting...")

        locationServiceIntent = Intent(context, locationService.javaClass)
        if (!isMyServiceRunning(locationService.javaClass)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(locationServiceIntent)
            } else {
                context.startService(locationServiceIntent)
            }
        }
    }

    /**
     *Stop the location service updates
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun stopLocationService(){
        val intent = Intent(context, LocationService::class.java)
        intent.action = "StopService"

        Log.d("CZ2006:MainActivityManager", "LocationService Starting...")
        //locationService =
        if (isMyServiceRunning(locationService.javaClass)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)

            } else {
                context.startForegroundService(intent)
            }
        }
    }

    /**
     *Checking of whether service is running
     *
     * @param serviceClass service to be checking
     */
    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("CZ2006:MainActivityManager:Service status", "Running")
                return true
            }
        }
        Log.i("CZ2006MainActivityManager:Service status", "Not running")
        return false
    }
}