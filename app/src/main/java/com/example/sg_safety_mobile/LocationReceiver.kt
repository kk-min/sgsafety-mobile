package com.example.sg_safety_mobile

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment

class LocationReceiver(val view: View): BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if(intent.action.equals("UPDATE_LOCATION")){
            var loc: Location? = intent.getParcelableExtra("LOCATION_DATA")

            //Set textview5:
            val textview: TextView = view.findViewById(R.id.textView5)
            if (loc != null) {
                textview.setText("Longitude: ${loc.longitude}\nLatitude: ${loc.latitude}")
            }
        }
    }
}