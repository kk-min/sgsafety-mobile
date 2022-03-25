package com.example.sg_safety_mobile

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.app.ActivityCompat
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class CPR_Map : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private lateinit var map : MapView;
    var locationServiceIntent: Intent? = null
    private var locationService: LocationService? = null
    lateinit var locationReceiver: LocationReceiver;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        // This won't work unless you have imported this: org.osmdroid.config.Configuration.*
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, if you abuse osm's
        //tile servers will get you banned based on this string.

        //inflate and create the map
        setContentView(R.layout.activity_cpr_map);

        map = findViewById<MapView>(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK);

        val mapController = map.controller
        val startPoint = GeoPoint(1.3524, 103.9449);
        mapController.animateTo(startPoint);
        mapController.setZoom(17)

        //mapController.setCenter(startPoint);

        map.maxZoomLevel= 20.0
        map.minZoomLevel=14.0
        addMarker(map, startPoint, "Start Point")
        val marker = Marker(map)
        //marker.position = startPoint
        //marker.icon = getDrawable(R.drawable.ic_launcher_foreground)
        //marker.title = "Test Marker"
        //marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        map.overlays.add(marker)
        map.invalidate()
        val endPoint = GeoPoint(1.3532, 103.9481)
        addingWaypoints(map, startPoint,endPoint)

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),111)
        }

        Log.d("LocationService", "LocationService Starting...")
        locationService = LocationService()
        locationServiceIntent = Intent(this, locationService!!.javaClass)
        if (!isMyServiceRunning(locationService!!.javaClass)) {
            startService(locationServiceIntent)
        }

        locationReceiver = LocationReceiver(map)
        val filter = IntentFilter("UPDATE_LOCATION")
        registerReceiver(locationReceiver, filter); // Register our receiver


    }

    override fun onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = ArrayList<String>();
        var i = 0;
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i]);
            i++;
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    /*private fun requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            permissionsToRequest.add(permission);
        }
    }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }*/
    private fun addMarker(map: MapView?, point: GeoPoint, title: String) {
        val startMarker = Marker(map)
        //Lat ‎23.746466 Lng 90.376015
        startMarker.position = point
        startMarker.title = title
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map?.overlays?.add(startMarker)
        map?.invalidate()
    }
    /*private fun addMarker(map: MapView?, point: GeoPoint, title: String) {
        val startMarker = Marker(map)
        //Lat ‎23.746466 Lng 90.376015
        startMarker.position = point
        startMarker.title = title
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map?.overlays?.add(startMarker)
        map?.invalidate()
    }*/

    private fun addingWaypoints(map: MapView?, startPoint: GeoPoint, endPoint: GeoPoint) {
        val roadManager = OSRMRoadManager(this,"MYUSERAGENT")
        roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT)
        val waypoints = ArrayList<GeoPoint>()
        waypoints.add(startPoint)
        //waypoints.add(GeoPoint(23.816237, 90.366725))

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

        addMarker(map, endPoint, "End Point")
    }

    private fun retrievingRoad(roadManager: OSRMRoadManager, waypoints: ArrayList<GeoPoint>) {
        // Retrieving road

        val road = roadManager.getRoad(waypoints)
        val roadOverlay = RoadManager.buildRoadOverlay(road)
        map?.overlays?.add(roadOverlay);

        //val nodeIcon = map?.context?.resources?.getDrawable(R.mipmap.ic_navigation)
        for (i in 0 until road.mNodes.size) {
            val node = road.mNodes[i]
            val nodeMarker = Marker(map)
            nodeMarker.position = node.mLocation
            //nodeMarker.setIcon(nodeIcon)
            nodeMarker.title = "Step $i"
            map?.overlays?.add(nodeMarker)
            nodeMarker.snippet = node.mInstructions;
            nodeMarker.subDescription = Road.getLengthDurationText(map?.context, node.mLength, node.mDuration);
        }
    }
    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("Service status", "Running")
                return true
            }
        }
        Log.i("Service status", "Not running")
        return false
    }


    private inner class MyRoadAsyncTask(val roadManager: OSRMRoadManager,
                                        val waypoints: ArrayList<GeoPoint>) : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String? {
            val road = roadManager.getRoad(waypoints)
            val roadOverlay = RoadManager.buildRoadOverlay(road)
            map?.overlays?.add(roadOverlay);

            //val nodeIcon = map?.context?.resources?.getDrawable(R.mipmap.ic_navigation)
            for (i in 0 until road.mNodes.size) {
                val node = road.mNodes[i]
                val nodeMarker = Marker(map)
                nodeMarker.position = node.mLocation
                //nodeMarker.setIcon(nodeIcon)
                nodeMarker.title = "Step $i"
                map?.overlays?.add(nodeMarker)
                nodeMarker.snippet = node.mInstructions;
                nodeMarker.subDescription = Road.getLengthDurationText(map?.context, node.mLength, node.mDuration);
            }

            return null
        }

        override fun onPostExecute(result: String?) {
            map?.invalidate()
        }
    }
}