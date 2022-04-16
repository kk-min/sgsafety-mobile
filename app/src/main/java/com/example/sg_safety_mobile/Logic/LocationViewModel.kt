package com.example.sg_safety_mobile.Logic

import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sg_safety_mobile.Data.LocationDataRepository

/**
 *View model of the location service
 *
 * @since 2022-4-15
 */
class LocationViewModel(val context: Context): ViewModel() {
    /**
     *Location Data Repository class
     */
    private val locationDataRepository = LocationDataRepository(context.applicationContext) // Initialize the Repository to get our data from
    /**
     *Liva data of the constantly updated location
     */
    var currentLocation: MutableLiveData<Location>? = locationDataRepository.currentLocation

    /**
     *Runs to clear the location
     */
    override fun onCleared() {
        super.onCleared()
        currentLocation = null
    }
}