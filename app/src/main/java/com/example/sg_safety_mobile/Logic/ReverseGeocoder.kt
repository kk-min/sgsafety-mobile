package com.example.sg_safety_mobile.Logic

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import org.osmdroid.util.GeoPoint
import java.io.IOException
import java.util.*

class ReverseGeocoder(val context: Context) {


    fun reverseGeocode(latitude:Double,longitude:Double):String{
        var gc= Geocoder(context, Locale.getDefault())
        try{
            var addresses= gc.getFromLocation(latitude,longitude,1)
            var address: Address = addresses[0]
            var addressStr:String="${address.getAddressLine(0)}"
            return addressStr
        }
        catch (e:Exception)
        {
            e.printStackTrace()
            Log.e("CZ2006:ReverseGeocode","grpc failed T^T")
        }
        return "Geocode Failed"
    }
    fun reverseGeocodePostalCode(latitude:Double,longitude:Double):String{
        var gc= Geocoder(context, Locale.getDefault())
        try{
            var addresses= gc.getFromLocation(latitude,longitude,1)
            var address: Address = addresses[0]
            var postalStr:String="${address.postalCode}"
            return postalStr
        }
        catch (e:Exception)
        {
            e.printStackTrace()
            Log.e("CZ2006:ReverseGeocode","grpc failed T^T")
        }
        return "Geocode Failed"
    }
    fun getLocationFromAddress(strAddress: String): GeoPoint? {
        var gc= Geocoder(context, Locale.getDefault())
        val address: List<Address>
        var p1: GeoPoint? = null
        try {
            address = gc.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location: Address = address[0]
            location.getLatitude()
            location.getLongitude()
            p1 = GeoPoint(
                (location.getLatitude() ) as Double,
                (location.getLongitude() ) as Double
            )
            return p1
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("CZ2006:ReverseGeocode","grpc failed T^T")
        }
        return null
    }
}