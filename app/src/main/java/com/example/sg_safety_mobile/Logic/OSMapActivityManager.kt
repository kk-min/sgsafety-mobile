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
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class OSMapActivityManager(val context: Context,val map:MapView) {

    private val locationService: LocationService=LocationService()
    private var locationServiceIntent: Intent? = null
    private lateinit var lm: LocationManager

    fun addMarker(map: MapView?, point: GeoPoint, title: String) {

        val startMarker = Marker(map)
        startMarker.position = point
        startMarker.title = title
        startMarker.id= String.toString()
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map?.overlays?.add(startMarker)
        map?.invalidate()
    }


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

    fun retrievingRoad(roadManager: OSRMRoadManager, waypoints: ArrayList<GeoPoint>) {
        // Retrieving road

        val road = roadManager.getRoad(waypoints)
        val roadOverlay = RoadManager.buildRoadOverlay(road)
        map?.overlays?.add(roadOverlay);

        val nodeIcon = map?.context?.resources?.getDrawable(R.mipmap.walk)
        for (i in 0 until road.mNodes.size) {
            val node = road.mNodes[i]
            val nodeMarker = Marker(map)
            nodeMarker.position = node.mLocation
            if(i!=0&&i!=road.mNodes.size-1) {
                nodeMarker.icon = nodeIcon
            }
            nodeMarker.title = "Step $i"
            map?.overlays?.add(nodeMarker)
            nodeMarker.snippet = node.mInstructions;
            nodeMarker.subDescription =
                Road.getLengthDurationText(map?.context, node.mLength, node.mDuration);
        }
    }


    private inner class MyRoadAsyncTask(
        val roadManager: OSRMRoadManager,
        val waypoints: ArrayList<GeoPoint>
    ) : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String? {
            val road = roadManager.getRoad(waypoints)
            val roadOverlay = RoadManager.buildRoadOverlay(road)
            map?.overlays?.add(roadOverlay);

            val nodeIcon = map?.context?.resources?.getDrawable(R.mipmap.walk)
            for (i in 0 until road.mNodes.size) {
                val node = road.mNodes[i]
                val nodeMarker = Marker(map)
                nodeMarker.position = node.mLocation
                if(i!=0&&i!=road.mNodes.size-1) {
                    nodeMarker.icon = nodeIcon
                }
                nodeMarker.title = "Step $i"
                map?.overlays?.add(nodeMarker)
                nodeMarker.snippet = node.mInstructions;
                nodeMarker.subDescription =
                    Road.getLengthDurationText(map?.context, node.mLength, node.mDuration);
            }

            return null
        }
    }

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
    fun startLocationService(){
        Log.d("CZ2006:MainActivityManager", "LocationService Starting...")

        locationServiceIntent = Intent(context, locationService!!.javaClass)
        if (!isMyServiceRunning(locationService!!.javaClass)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(locationServiceIntent)
            } else {
                context.startService(locationServiceIntent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun stopLocationService(){
        val intent = Intent(context, LocationService::class.java)
        intent.action = "StopService"

        Log.d("CZ2006:MainActivityManager", "LocationService Starting...")
        //locationService =
        if (isMyServiceRunning(locationService!!.javaClass)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)

            } else {
                context.startForegroundService(intent)
            }
        }
    }

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