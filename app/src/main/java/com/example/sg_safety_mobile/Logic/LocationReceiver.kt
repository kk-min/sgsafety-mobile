package com.example.sg_safety_mobile.Logic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.sg_safety_mobile.Data.LocationDataRepository
import com.example.sg_safety_mobile.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay



class LocationReceiver(context: Context, locationRepo: LocationDataRepository): BroadcastReceiver() {

    val repo = locationRepo
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action.equals("UPDATE_LOCATION_REPOSITORY"))
        {
            var loc: Location? = intent.getParcelableExtra("LOCATION_DATA")
            if (loc != null){
                repo.currentLocation.postValue(loc)
            }
        }
    }
    }

