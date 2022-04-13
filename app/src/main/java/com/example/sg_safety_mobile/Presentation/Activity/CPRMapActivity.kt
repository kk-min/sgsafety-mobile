package com.example.sg_safety_mobile.Presentation.Activity

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.example.sg_safety_mobile.Logic.*
import com.example.sg_safety_mobile.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView


class CPRMapActivity : AppCompatActivity() {


    private var locationServiceIntent: Intent? = null
    private val gc=ReverseGeocoder(this)

    private lateinit var locationReceiver: LocationReceiver;
    private lateinit var lm: LocationManager
    private lateinit var loc: Location
    private lateinit var mapManager:OSMapActivityManager
    private lateinit var map:MapView
    private lateinit var victimlocationtext: TextView
    private lateinit var googleMapLink:TextView
    private lateinit var locateMe:Button
    private lateinit var doneButton:Button
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private var locationService: LocationService? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        supportActionBar?.hide();


        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            &&ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),111)
        }
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
        viewEInitializations()

        map.setTileSource(TileSourceFactory.MAPNIK);
        val mapController = map.controller
        mapManager=OSMapActivityManager(this,map)



        //get user location
        loc=mapManager.getCurrentLocation()
        val startPoint=GeoPoint(loc.latitude,loc.longitude)

        val endPoint=getVictimLocation()
        //set text view of victim location

        victimlocationtext.text=gc.reverseGeocode(endPoint.latitude,endPoint.longitude)


        //map setting
        mapController.animateTo(startPoint);
        mapController.setZoom(20)
        map.maxZoomLevel= 24.0
        map.minZoomLevel=14.0
        map.invalidate()

        mapManager.startLocationService()
        registerLocationReceiver()
        //add pathway
        mapManager.addingWaypoints(map, startPoint,endPoint)






        googleMapLink.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${endPoint.latitude},${endPoint.longitude}"))
            startActivity(browserIntent)
        }


        locateMe.setOnClickListener {
            lm=getSystemService(Context.LOCATION_SERVICE) as LocationManager
            loc= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!

            val curPoint=GeoPoint(loc.latitude,loc.longitude)
            mapController.animateTo(curPoint);

        }
        val helpPreference: SharedPreferences =getSharedPreferences("VictimDetails", MODE_PRIVATE)

        doneButton.setOnClickListener {
            //clear all saved victim details
            val editor:SharedPreferences.Editor=helpPreference.edit()
            editor.clear()
            editor.commit()

            val intent = Intent(this, ThankYouPageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }


    }

    private fun viewEInitializations() {
        map = findViewById(R.id.map)
        victimlocationtext=findViewById(R.id.victimlocation)
        googleMapLink = findViewById(R.id.googlemaplink)
        locateMe=findViewById(R.id.locate)
        doneButton=findViewById(R.id.Done)
    }

    private fun getVictimLocation(): GeoPoint {
        //get victim location
        //val helpPreference: SharedPreferences =getSharedPreferences("VictimDetails", MODE_PRIVATE)
        //val current_user_id= helpPreference.getString("UserID","")
        val helpPreference: SharedPreferences = getSharedPreferences("VictimDetails", MODE_PRIVATE)
        val victimLongitude = helpPreference.getFloat("Victim_Longitude", 0.0F).toDouble()
        val victimLatitude = helpPreference.getFloat("Victim_Latitude", 0.0F).toDouble()
        Log.d("CZ2006:CPR Map Victim Location", "${victimLatitude} ${victimLongitude}")

        return GeoPoint(victimLatitude, victimLongitude)
    }

    private fun registerLocationReceiver(){
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





}