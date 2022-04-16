package com.example.sg_safety_mobile.Presentation.Activity

import android.content.Context
import android.content.Intent
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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.sg_safety_mobile.Logic.OSMapActivityManager
//import com.example.sg_safety_mobile.Logic.LocationReceiver
import com.example.sg_safety_mobile.Logic.LocationViewModel
import com.example.sg_safety_mobile.Logic.ReverseGeocoder
import com.example.sg_safety_mobile.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.net.URL

/**
 *Activity that is being started when user choose to retrieve AED
 * via HelperChoiceActivity[com.example.sg_safety_mobile.Presentation.Activity.HelperChoiceActivity]
 *
 * @since 2022-4-15
 */
class AEDMapActivity : AppCompatActivity() {

    /**
     *Reverse geocoder class to convert location or address
     */
    private val gc= ReverseGeocoder(this)
    /**
     *In-built location manager
     */
    private lateinit var lm: LocationManager
    /**
     *UI text view of showing of building name
     */
    private lateinit var buildingName_textview:TextView
    /**
     *UI text view of showing of AED description
     */
    private lateinit var aedDescription_textview:TextView
    /**
     *UI text view of showing of AED Floor No,
     */
    private lateinit var aedFloor_textview:TextView
    /**
     *UI text view of showing of Operating Hrs of AED
     */
    private lateinit var opHrs_textview:TextView
    /**
     *UI text view of google map link button
     */
    private lateinit var googleMapLink:TextView
    /**
     *UI button of locating current location in the map
     */
    private lateinit var locateMe:Button
    /**
     *UI button to be pressed when AED is retrieved
     */
    private lateinit var aedretrieve: Button
    /**
     *Map manager of OSMap
     */
    private lateinit var mapManager:OSMapActivityManager
    /**
     *Permission request code
     */
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    /**
     *Map view of OSMap
     */
    private lateinit var map : MapView

    /**
     *Runs when activity is created
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //hide status bar
        supportActionBar?.hide()

        //check location permission
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            &&ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),111)
        }
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))


        //inflate and create the map
        setContentView(R.layout.activity_aedmap)
        viewEInitializations()

        //initialize map
        map.setTileSource(TileSourceFactory.MAPNIK)
        mapManager=OSMapActivityManager(this,map)
        val mapController = map.controller


        //get current location and add marker
        var loc=mapManager.getCurrentLocation()
        val startPoint= GeoPoint(loc.latitude, loc.longitude)

        mapController.setZoom(14)
        if (startPoint != null) {
            mapManager.addMarker(map,startPoint,"Current Location")
        }

        //set min max zoom level
        map.maxZoomLevel= 24.0
        map.minZoomLevel=12.0
        map.invalidate()
        mapController.animateTo(startPoint)

        //start location updates to live update the marker of current location
        val locationViewModel = LocationViewModel(this)
        mapManager.startLocationService()
        val locationObserver = Observer<Location>{
                newLocation ->
            if (newLocation != null) {
                Log.d("Observer", "Observed Location change. ${newLocation.latitude}, ${newLocation.longitude}")
                mapManager.updateMarker(map,newLocation,"Current Location")
            }
        }
        locationViewModel.currentLocation?.observe({ lifecycle }, locationObserver)


        //BUTTON-----------------------------------------------------------------------------
        //use to move camera of map to cur location
        locateMe.setOnClickListener {
            lm=getSystemService(Context.LOCATION_SERVICE) as LocationManager
            loc= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!

            val curPoint=GeoPoint(loc.latitude, loc.longitude)
            mapController.animateTo(curPoint)

        }

        //use to store clicked aed longlat
        val locationPreferences:SharedPreferences=getSharedPreferences("AED", MODE_PRIVATE)
        val editor=locationPreferences.edit()

        //prompt to google map for navigation
        googleMapLink.setOnClickListener {
            val aed_lon=locationPreferences.getString("aed_lon","")
            val aed_lat=locationPreferences.getString("aed_lat","")
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${aed_lat},${aed_lon}"))
            startActivity(browserIntent)
        }

        //end current aed retrieval and move to user map
        aedretrieve.setOnClickListener {
            editor.clear()
            editor.commit()
            val intent = Intent(this, CPRMapActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        //-------------------------------------------------------------------------------------------
        //retrieve aed from data gov sg
        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO) {

                //see the CKAN API thing to know how does the URL works
                val dataAPI = URL("https://data.gov.sg/api/action/datastore_search?resource_id=cdab7435-7bf0-4fa4-a8bd-6cfd231ca73a&limit=999").readText()
                val parser = Parser()
                val stringBuilder: StringBuilder = StringBuilder(dataAPI)
                val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                val result: JsonObject = json["result"] as JsonObject

                //record array for all AED location
                val array= result["records"] as JsonArray<*>
                setAEDLocation(array)

            }
        }
    }

    /**
     *Initialize all the UI views
     */
    private fun viewEInitializations() {

        map = findViewById(R.id.map)
        locateMe=findViewById(R.id.locate)
        googleMapLink=findViewById(R.id.googlemaplink)
        aedretrieve=findViewById(R.id.aed_retrieve)
        aedDescription_textview=findViewById(R.id.aed_location)
        aedFloor_textview=findViewById(R.id.aed_floor)
        buildingName_textview=findViewById(R.id.building_name)
        opHrs_textview=findViewById(R.id.ophrs)

    }

    /**
     *Add all the AED marker into the map
     *
     * @param array array of JSON of AED details
     */
    private fun setAEDLocation(array:JsonArray<*>){

        val postalCode= array["postal_code"]
        val roadname= array["road_name"]
        val ophrs= array["operating_hours"]
        val aed_description= array["aed_location_description"]
        val aed_floor= array["aed_location_floor_level"]
        val building_name= array["building_name"]

        for(i in 0 until array.size)
        {
            val address="${roadname[i]}, ${postalCode[i]} Singapore"
            Log.d("CZ2006:VicTIM geocode aed", address)
            val location=gc.getLocationFromAddress(address)
            Log.d("CZ2006:VicTIM geocoded aed", "$location")
            val point= location?.let { GeoPoint(it.latitude,location.longitude) }
            if (point != null)
            {
                addOnClickAEDMarker(map,point,"AED ${i}",building_name[i].toString(),aed_description[i].toString()
                    ,aed_floor[i].toString(),ophrs[i].toString())
            }
        }

    }

    /**
     *Add a marker that show its details when the marker is clicked
     *
     * @param map map to be updated
     * @param point geopoint of marker
     * @param title title and id of the marker
     * @param buildingName AED building name
     * @param aedDescription AED location description
     * @param aedFloor  AED floor no.
     * @param opHrs operating hours of AED
     */
    private fun addOnClickAEDMarker(map: MapView?, point: GeoPoint, title: String,buildingName:String,aedDescription:String,aedFloor:String,opHrs:String) {

        //setup marker
        val startMarker = Marker(map)
        startMarker.position = point
        startMarker.title = title
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        //set marker clicking
        startMarker.setOnMarkerClickListener { marker, mapView ->

            val mapController= map?.controller
            mapController?.animateTo(point)

            //update textview
            aedDescription_textview.text=aedDescription
            aedFloor_textview.text=aedFloor
            buildingName_textview.text=buildingName
            opHrs_textview.text=opHrs

            //add aed longlat to preference
            val locationPreference:SharedPreferences=getSharedPreferences("AED", MODE_PRIVATE)
            val editor=locationPreference.edit()

            editor.putString("aed_lat",startMarker.position.latitude.toString())
            editor.putString("aed_lon",startMarker.position.longitude.toString())
            editor.commit()

            true
        }

        map?.overlays?.add(startMarker)
        map?.invalidate()

    }

    /**
     *Runs when app is resumed
     */
    override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume() //needed for compass, my location overlays, v6.0.0 and up
    }

    /**
     *Runs when app is paused
     */
    override fun onPause() {
        super.onPause()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause()  //needed for compass, my location overlays, v6.0.0 and up
    }

    /**
     *Request for permission
     *
     * @param requestCode request code
     * @param permissions array of permission to be requested
     * @param grantResults result of requested permission
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }


}