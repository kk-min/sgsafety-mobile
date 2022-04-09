package com.example.sg_safety_mobile.Presentation.Activity

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.example.sg_safety_mobile.Logic.LocationReceiver
import com.example.sg_safety_mobile.Logic.LocationService
import com.example.sg_safety_mobile.Logic.ReverseGeocoder
import com.example.sg_safety_mobile.R
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

class AEDMapActivity : AppCompatActivity() {

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private lateinit var map : MapView;
    var locationServiceIntent: Intent? = null
    private var locationService: LocationService? = null
    lateinit var locationReceiver: LocationReceiver;
    lateinit var lm: LocationManager
    lateinit var loc: Location

    val gc= ReverseGeocoder(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        setContentView(R.layout.activity_aedmap)

        supportActionBar?.hide();
        map = findViewById<MapView>(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK);

        val mapController = map.controller
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            &&ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),111)
        }

        lm=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        loc= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!

        val startPoint=GeoPoint(loc.latitude,loc.longitude)
        val user_postal=gc.reverseGeocodePostalCode(loc.latitude,loc.longitude)
        val user_district=user_postal.toInt()/10000
        Log.d("postal district","${user_district}")
        mapController.animateTo(startPoint);
        mapController.setZoom(20)
        val point=GeoPoint(1.3524,103.9449)
        addMarker(map,point,"point")
        //mapController.setCenter(startPoint);

        map.maxZoomLevel= 24.0
        map.minZoomLevel=14.0
        //addMarker(map, startPoint, "Your Location")
        //val marker = Marker(map)
        //marker.position = startPoint
        //marker.icon = getDrawable(R.drawable.ic_launcher_foreground)
        //marker.title = "Test Marker"
        //marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        //map.overlays.add(marker)
        map.invalidate()

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),111)
        }

        Log.d("CZ2006:LocationService", "LocationService Starting...")
        locationService = LocationService()
        locationServiceIntent = Intent(this, locationService!!.javaClass)
        if (!isMyServiceRunning(locationService!!.javaClass)) {
            startService(locationServiceIntent)
        }

        locationService = LocationService()
        locationServiceIntent = Intent(this, locationService!!.javaClass)
        if (!isMyServiceRunning(locationService!!.javaClass)) {
            startService(locationServiceIntent)
        }

        locationReceiver = LocationReceiver(map)
        val filter = IntentFilter("UPDATE_LOCATION")
        registerReceiver(locationReceiver, filter); // Register our receiver

        val aedretrieve: Button =findViewById(R.id.aed_retrieve)
        aedretrieve.setOnClickListener {
            val intent = Intent(this, CPRMapActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent)
        }
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



    private fun addMarker(map: MapView?, point: GeoPoint, title: String) {
        val startMarker = Marker(map)
        //Lat ‎23.746466 Lng 90.376015
        //startMarker.icon=map?.context?.resources?.getDrawable(R.drawable.userloc)
        startMarker.position = point
        startMarker.title = title
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
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
            return
        }

        startMarker.setOnMarkerClickListener { marker, mapView ->

            val curlocation= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
            val currentPoint=GeoPoint(curlocation.latitude,curlocation.longitude)
            addingWaypoints(mapView,currentPoint,point)

            true
        }
        map?.overlays?.add(startMarker)
        map?.invalidate()
    }


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

        val nodeIcon = map?.context?.resources?.getDrawable(R.mipmap.walk)
        for (i in 0 until road.mNodes.size) {
            val node = road.mNodes[i]
            val nodeMarker = Marker(map)
            nodeMarker.position = node.mLocation
            nodeMarker.icon=nodeIcon
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
                Log.i("CZ2006:Service status", "Running")
                return true
            }
        }
        Log.i("CZ2006:Service status", "Not running")
        return false
    }


    private inner class MyRoadAsyncTask(val roadManager: OSRMRoadManager,
                                        val waypoints: ArrayList<GeoPoint>) : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String? {
            val road = roadManager.getRoad(waypoints)
            val roadOverlay = RoadManager.buildRoadOverlay(road)
            map?.overlays?.add(roadOverlay);

            val nodeIcon = map?.context?.resources?.getDrawable(R.mipmap.walk)
            for (i in 0 until road.mNodes.size) {
                val node = road.mNodes[i]
                val nodeMarker = Marker(map)
                nodeMarker.position = node.mLocation
                nodeMarker.setIcon(nodeIcon)
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