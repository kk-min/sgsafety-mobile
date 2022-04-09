package com.example.sg_safety_mobile.Logic

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.view.View
import java.util.*

class ReverseGeocoder(val context: Context) {

    fun reverseGeocode(latitude:Double,longitude:Double):String{
        var gc= Geocoder(context, Locale.getDefault())

        var addresses= gc.getFromLocation(latitude,longitude,1)
        var address: Address = addresses[0]


        var addressStr:String="${address.getAddressLine(0)}"
        return addressStr
    }
    fun reverseGeocodePostalCode(latitude:Double,longitude:Double):String{
        var gc= Geocoder(context, Locale.getDefault())

        var addresses= gc.getFromLocation(latitude,longitude,1)
        var address: Address = addresses[0]


        var postalStr:String="${address.postalCode}"

        return postalStr
    }
}