package com.example.sg_safety_mobile.Presentation.Activity

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.sg_safety_mobile.Logic.OSMapActivityManager
import com.example.sg_safety_mobile.Logic.LocationReceiver
import com.example.sg_safety_mobile.Logic.LocationService
import com.example.sg_safety_mobile.Logic.ReverseGeocoder
import com.example.sg_safety_mobile.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
import java.io.IOException
import java.net.URL

class AEDMapActivity : AppCompatActivity() {

    private val gc= ReverseGeocoder(this)
    private lateinit var locationReceiver: LocationReceiver;
    private lateinit var lm: LocationManager
    private lateinit var buildingName_textview:TextView
    private lateinit var aedDescription_textview:TextView
    private lateinit var aedFloor_textview:TextView
    private lateinit var opHrs_textview:TextView
    private lateinit var googleMapLink:TextView
    private lateinit var locateMe:Button
    private lateinit var aedretrieve: Button
    private lateinit var mapManager:OSMapActivityManager
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private lateinit var map : MapView;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();


        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            &&ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),111)
        }
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));


        //inflate and create the map
        setContentView(R.layout.activity_aedmap)
        viewEInitializations()
        //initialize map
        map.setTileSource(TileSourceFactory.MAPNIK);
        mapManager=OSMapActivityManager(this,map)
        val mapController = map.controller



        var loc=mapManager.getCurrentLocation()
        //get user postal district
        //val user_district= loc?.let { getUserPostalDistrict(it) }

        //move to user location point
        val startPoint= GeoPoint(loc!!.latitude, loc!!.longitude)
        mapController.animateTo(startPoint);
        mapController.setZoom(14)
        if (startPoint != null) {
            mapManager.addMarker(map,startPoint,"Your Location")
        }
        //set min max zoom level
        map.maxZoomLevel= 24.0
        map.minZoomLevel=12.0
        map.invalidate()



        mapManager.startLocationService()
        registerLocationReceiver()

        //BUTTON-----------------------------------------------------------------------------
        //use to move camera of map to cur location

        locateMe.setOnClickListener {
            lm=getSystemService(Context.LOCATION_SERVICE) as LocationManager
            loc= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!

            val curPoint=GeoPoint(loc!!.latitude, loc!!.longitude)
            mapController.animateTo(curPoint);

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



        //retrieve aed from data gov sg
        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO) {

                //see the CKAN API thing to know how does the URL works
                val dataAPI = URL("https://data.gov.sg/api/action/datastore_search?resource_id=cdab7435-7bf0-4fa4-a8bd-6cfd231ca73a&limit=999").readText()
                val parser: Parser = Parser()
                val stringBuilder: StringBuilder = StringBuilder(dataAPI)
                val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                val result: JsonObject =json.get("result") as JsonObject

                //record array for all AED location
                val array=result.get("records") as JsonArray<*>


                //if (user_district != null) {
                  //  setNearbyAEDLocation(user_district.toString(),array)
                    setNearbyAEDLocation(array)
                    //setNearbyAEDLocation((user_district+1).toString(),array)
                //}

            }
        }
    }
    private fun viewEInitializations() {
        map = findViewById(R.id.map)
        locateMe=findViewById(R.id.locate)
        googleMapLink=findViewById(R.id.googlemaplink)
        aedretrieve=findViewById(R.id.aed_retrieve)
        //get textview
        aedDescription_textview=findViewById(R.id.aed_location)
        aedFloor_textview=findViewById(R.id.aed_floor)
        buildingName_textview=findViewById(R.id.building_name)
        opHrs_textview=findViewById(R.id.ophrs)

    }
    private fun registerLocationReceiver(){
        locationReceiver = LocationReceiver(map)
        val filter = IntentFilter("UPDATE_LOCATION")
        registerReceiver(locationReceiver, filter); // Register our receiver
    }

    private fun getUserPostalDistrict(loc:Location):Int {
        val user_postal= gc.reverseGeocodePostalCode(loc.latitude,loc.longitude)
        val user_district= user_postal.toInt()/10000
        Log.d("postal district","${user_district}")
        return user_district
    }

    private fun setNearbyAEDLocation(array:JsonArray<*>){

//        val db = Firebase.firestore
//
//        //start and end index of aed record array
//        var start:Long=0
//        var end:Long=0


        val postalCode=array.get("postal_code")
        val roadname=array.get("road_name")
        val ophrs=array.get("operating_hours")
        val aed_description=array.get("aed_location_description")
        val aed_floor=array.get("aed_location_floor_level")
        val building_name=array.get("building_name")


//        runBlocking {
//            db.collection("AED_Postal_Code").document(postal_district.toString()).get()
//                .addOnSuccessListener { document ->
//
//                    start = document.get("start_index") as Long
//                    end = document.get("end_index") as Long
//                    Log.d("array", "${start},${end}")
//                }
//                .addOnFailureListener { e ->
//                    Log.e("CZ2006:VicTIM aed not found", "Error getting document", e)
//
//                }.await()
//        }
//        //if(document not found then return)
//        if(end.toInt()==0)
//            return
        //otherwise start adding aed marker
        for(i in 0..(array.size-1))
        {
            val address="${roadname[i]}, ${postalCode[i]} Singapore"
            Log.d("CZ2006:VicTIM geocode aed", "${address}")
            val location=gc.getLocationFromAddress(address)
            Log.d("CZ2006:VicTIM geocoded aed", "${location}")
            val point= location?.let { GeoPoint(it.latitude,location.longitude) }
            if (point != null)
            {
                addOnClickAEDMarker(map,point,"AED ${i}",building_name[i].toString(),aed_description[i].toString()
                    ,aed_floor[i].toString(),ophrs[i].toString())
            }
        }

    }

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