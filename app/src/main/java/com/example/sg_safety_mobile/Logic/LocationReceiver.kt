package com.example.sg_safety_mobile.Logic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.sg_safety_mobile.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay



class LocationReceiver(val view: View): BroadcastReceiver() {

    private val geoCoder = ReverseGeocoder(view.context)
    private var currentMarker: Marker? = null
    private lateinit var mapView:MapView

    override fun onReceive(context: Context, intent: Intent) {
        mapView=view.findViewById(R.id.map)
        if(mapView==null)
        {
            Log.e("CZ2006:LocationReceiver:","Map is null")
            return
        }
        if(intent.action.equals("UPDATE_LOCATION+ADDRESS")){
            val loc: Location? = intent.getParcelableExtra("LOCATION_DATA")
            //Update marker
            if (loc != null) {
                updateMarker(loc,"Current Location")
                val text= view.findViewById<TextView>(R.id.location)
                text.text=geoCoder.reverseGeocode(loc.latitude,loc.longitude)
            }
        }
        else if(intent.action.equals("UPDATE_LOCATION"))
        {
            val loc: Location? = intent.getParcelableExtra("LOCATION_DATA")
            if (loc != null) {
                updateMarker(loc,"Current Location")
            }
        }
    }
    private fun updateMarker(loc: Location?,id:String){
        //Delete marker if applicable, then insert new currentLocation marker
//        if (currentMarker != null){
//            Log.d("CZ2006:LocationService", "prev Location deleted")
//            mapView?.overlays?.remove(currentMarker)
//        }
        try{
            for (i in 0 until mapView.overlays.size) {
                val overlay: Overlay = mapView.overlays[i]
                if (overlay is Marker && overlay.id == id) {
                    mapView.overlays.remove(overlay)
                }
            }

            val point: GeoPoint? = loc?.let { GeoPoint(it.latitude, loc.longitude) }


            currentMarker = Marker(mapView)
            currentMarker?.position = point
            currentMarker?.title = "Current Location"
            currentMarker?.id="Current Location"
            currentMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            val mapController = mapView.controller
            mapController.animateTo(point)
            mapView.overlays?.add(currentMarker)
            mapView.invalidate()
            Log.d("CZ2006:LocationService", "New location set and marker added")
        }
        catch(e:Exception) {
            e.printStackTrace()
            Log.e("CZ2006:LocationService", "Error adding marker")
        }

    }

}
