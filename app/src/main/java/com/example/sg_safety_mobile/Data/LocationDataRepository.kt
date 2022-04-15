package com.example.sg_safety_mobile.Data


import android.content.Context
import android.content.IntentFilter
import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.example.sg_safety_mobile.Logic.LocationReceiver

class LocationDataRepository(val context: Context) {
    var currentLocation = MutableLiveData<Location>()
    val locationReceiver = LocationReceiver(context, this)
    init{
        val filter = IntentFilter("UPDATE_LOCATION_REPOSITORY")
        context.applicationContext.registerReceiver(locationReceiver, filter); // Register our receiverontext));
    }
}