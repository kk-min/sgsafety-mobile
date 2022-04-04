package com.example.sg_safety_mobile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.view.View
import android.widget.TextView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.*

class LocationReceiver(val view: View): BroadcastReceiver() {
    var currentMarker: Marker? = null;
    lateinit var mapView:MapView
    override fun onReceive(context: Context, intent: Intent) {
        mapView=view.findViewById<MapView>(R.id.map)
        if(mapView==null)
        {
            Log.e("CZ2006:LocationReceiver:","Map is null")
            return
        }
        if(intent.action.equals("UPDATE_LOCATION+ADDRESS")){
            var loc: Location? = intent.getParcelableExtra("LOCATION_DATA")
            //Update marker
            if (loc != null) {
                updateMarker(loc)
                val text=view?.findViewById<TextView>(R.id.location)
                text.text=reverseGeocode(loc.latitude,loc.longitude)
            }
        }
        else if(intent.action.equals("UPDATE_LOCATION"))
        {
            var loc: Location? = intent.getParcelableExtra("LOCATION_DATA")
            if (loc != null) {
                updateMarker(loc)
            }
        }
    }
    fun updateMarker(loc: Location?){
        //Delete marker if applicable, then insert new currentLocation marker
        if (currentMarker != null){
            Log.d("CZ2006:LocationService", "prev Location deleted")
            mapView?.overlays?.remove(currentMarker)
        }
        var point: GeoPoint? = loc?.let { GeoPoint(it.latitude, loc.longitude) }

        try{
            currentMarker = Marker(mapView)
            currentMarker?.position = point
            currentMarker?.title = "Current Location"
            currentMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            val mapController = mapView.controller
            mapController.animateTo(point);
            mapView?.overlays?.add(currentMarker)
            mapView.invalidate()
            Log.d("CZ2006:LocationService", "New location set and marker added")
        }
        catch(e:Exception) {
            e.printStackTrace()
            Log.e("CZ2006:LocationService", "Error adding marker")
        }

    }

    private fun reverseGeocode(latitude:Double,longitude:Double):String{
        var gc= Geocoder(view.context, Locale.getDefault())
        var addresses= gc.getFromLocation(latitude,longitude,1)
        var address: Address = addresses[0]
        var addressStr:String="${address.getAddressLine(0)} ${address.locality}"
        return addressStr
    }

}