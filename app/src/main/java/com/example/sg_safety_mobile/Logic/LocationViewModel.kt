package com.example.sg_safety_mobile.Logic

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sg_safety_mobile.Data.LocationDataRepository
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class LocationViewModel(val context: Context): ViewModel() {
    private val locationDataRepository = LocationDataRepository(context.applicationContext) // Intialize the Repository to get our data from
    var currentLocation: MutableLiveData<Location>? = locationDataRepository.currentLocation


    override fun onCleared() {
        super.onCleared()
        currentLocation = null
    }
}