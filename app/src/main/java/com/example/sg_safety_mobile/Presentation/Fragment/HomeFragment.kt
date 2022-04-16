package com.example.sg_safety_mobile.Presentation.Fragment


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.sg_safety_mobile.Logic.LocationViewModel
import com.example.sg_safety_mobile.Logic.OSMapActivityManager
import com.example.sg_safety_mobile.Logic.ReverseGeocoder
import com.example.sg_safety_mobile.Presentation.Activity.AlertPageActivity
import com.example.sg_safety_mobile.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView
import kotlin.collections.ArrayList

/**
 *Fragment that shows the home page of this app consist of a map and location text that shows user current location
 * (Location GPS needs to be ON) and is the first fragment showed up when Main Activity runs and can be accessed again
 * in MainActivity[com.example.sg_safety_mobile.Presentation.Activity.MainActivity]
 * via NavigationDrawer
 *
 * @since 2022-4-15
 */
class HomeFragment : Fragment(),View.OnClickListener {

    /**
     *permission request code
     */
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    /**
     *OSMap view
     */
    private lateinit var map : MapView
    /**
     *OSMap Manager
     */
    private lateinit var mapManager:OSMapActivityManager
    /**
     *UI Button for sending help
     */
    private lateinit var button: Button
    /**
     *UI text view of showing of current location address
     */
    private lateinit var locationtext:TextView
    /**
     *UI Image button of refreshing the map and location details when pressed
     */
    private lateinit var refresh:ImageView

    /**
     *Runs when fragment is created
     */
    //FOR PAGE VIEW
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //initialize current page
        val v= inflater.inflate(R.layout.fragment_home, container, false)
        viewEInitializations(v)
        val locationViewModel = LocationViewModel(this.activity as Context)

        //check location permissions
        if(ActivityCompat.checkSelfPermission(v.context,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            &&  ActivityCompat.checkSelfPermission(v.context,android.Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),111)
        }

        Configuration.getInstance().load(v.context , PreferenceManager.getDefaultSharedPreferences(v.context))


        //setup map
        map.setTileSource(TileSourceFactory.MAPNIK)
        val mapController = map.controller
        mapController.setZoom(20)
        map.maxZoomLevel= 20.0
        map.minZoomLevel=14.0
        mapManager= OSMapActivityManager(v.context,map)

        //start location update to get current user location in live map
        val locationObserver = Observer<Location>{
                newLocation ->
            if (newLocation != null) {
                Log.d("Observer", "Observed Location change. ${newLocation.latitude}, ${newLocation.longitude}")
                mapManager.updateMarker(map,newLocation,"Current Location")
                setLocationDetails(mapController as MapController,map)
            }
        }
        locationViewModel.currentLocation?.observe(viewLifecycleOwner, locationObserver)

        //Button-------------------------------------------------------------------------------------------------
        //PROMPT ALERT BOX TO MAKE SURE USER REALLY NEED HELP
        button.setOnClickListener{
            try{
                mapManager.getCurrentLocation()
                showAlertDialog()
            }
            catch (e:Exception){
                Toast.makeText(context, "Please turn on your location!", Toast.LENGTH_SHORT).show()
            }

        }
        //refresh the map and user location details
        refresh.setOnClickListener{

            setLocationDetails(mapController as MapController,map)
        }
        return v
    }

    /**
     *Set the details of current location on this page
     */
    private fun setLocationDetails(mapController:MapController,map:MapView){
        try{
            val cur_location=mapManager.getCurrentLocation()
            val cur_geopoint=GeoPoint(cur_location.latitude,cur_location.longitude)
            mapController.animateTo(cur_geopoint)
            mapManager.updateMarker(map,cur_location,"Current Location")

            val gc= context?.let { ReverseGeocoder(it) }
            if (gc != null) {
                locationtext.text=gc.reverseGeocode(cur_geopoint.latitude,cur_geopoint.longitude)
            }

        }
        catch (e:Exception)
        {
            Log.e("cz2006:HomeFragment","$e")
            map.overlays.clear()
            locationtext.text="Please turn on your device location!!!"
        }
    }

    /**
     *Initialize all the UI views
     *
     * @param v view of this fragment
     */
    private fun viewEInitializations(v:View) {
        button=v.findViewById(R.id.alert_button)
        map = v.findViewById<MapView>(R.id.map)
        locationtext=v.findViewById(R.id.location)
        refresh=v.findViewById(R.id.refresh)
    }

    /**
     *Show alert box to make sure that user really need to send help
     */
    //ALERT TO MAKE SURE USER DON'T ACCIDENTALLY PRESS THE SEND HELP BUTTON
    private fun showAlertDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(requireActivity())

        alertDialog.setTitle("Are you sure?")
        alertDialog.setMessage("By pressing Yes,help message will be sent to SCDF and Users nearby")
        alertDialog.setPositiveButton(
            "Yes"
        ) { _, _ ->
            //Go to Alert Page Activity which will display No. of helpers accepted to help
            val intent = Intent(activity, AlertPageActivity::class.java)
            startActivity(intent)

        }
        //cancel the alert button
        alertDialog.setNegativeButton(
            "No"
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()

        alert.setCanceledOnTouchOutside(false)
        alert.show()

        //layout for alert box
        val layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.weight=10F
        layoutParams.gravity= Gravity.CENTER
        //layoutParams.width=10
        alert.getButton(Dialog.BUTTON_POSITIVE).layoutParams = layoutParams
        alert.getButton(Dialog.BUTTON_POSITIVE).textSize=20F
        alert.getButton(Dialog.BUTTON_NEGATIVE).layoutParams = layoutParams
        alert.getButton(Dialog.BUTTON_NEGATIVE).textSize=20F
    }

    /**
     *Runs when onClick
     *
     * @param v view of this fragment
     */
    override fun onClick(v: View?) {
        TODO("Not yet implemented")
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
                view?.context as Activity,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }



}
