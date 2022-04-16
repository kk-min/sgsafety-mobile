package com.example.sg_safety_mobile.Logic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import com.example.sg_safety_mobile.Data.LocationDataRepository

/**
 * Location Receiver class that inherits BroadcastReceiver class and runs when location is updated
 *
 * @since 2022-4-15
 */

class LocationReceiver(context: Context, locationRepo: LocationDataRepository): BroadcastReceiver() {

    /**
     *(context)
     * Application context of the activity or fragment
     */

    /**
     *Location Data Repository
     */
    val repo = locationRepo

    /**
     *Runs when location is received
     *
     * @param context application context
     * @param intent intent to be done when data is received
     */
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

