package com.example.sg_safety_mobile.Data

import android.content.Context
import android.content.IntentFilter
import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.example.sg_safety_mobile.Logic.LocationReceiver

/**
 * Repository class for Location Data
 *
 * @since 2022-4-15
 */
class LocationDataRepository(val context: Context) {

    /**
     *Current Live Location of the User
     */
    var currentLocation = MutableLiveData<Location>()
    /**
     *Location Receiver that detects and acts when location has changed and be sent to activities or fragment
     */
    private val locationReceiver = LocationReceiver(context, this)

    /**
     *Constructer of the this class
     */
    init{
        val filter = IntentFilter("UPDATE_LOCATION_REPOSITORY")
        context.applicationContext.registerReceiver(locationReceiver, filter); // Register our receiver on text));
    }
}