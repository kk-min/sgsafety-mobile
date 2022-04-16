package com.example.sg_safety_mobile.Logic

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import org.osmdroid.util.GeoPoint
import java.io.IOException
import java.util.*

/**
 *Class that is used for finding the address via location of location or finding of location address via location
 *
 * @since 2022-4-15
 */
class ReverseGeocoder(val context: Context) {

    /**
     *(context)
     * Application Context
     */

    /**
     *Find the address via geopoint
     *
     * @param latitude latitude of location
     * @param longitude longitude of location
     *
     * @return address found
     */
    fun reverseGeocode(latitude:Double,longitude:Double):String{
        val gc= Geocoder(context, Locale.getDefault())
        try{
            val addresses= gc.getFromLocation(latitude,longitude,1)
            val address: Address = addresses[0]
            val addressStr:String= address.getAddressLine(0)
            return addressStr
        }
        catch (e:Exception)
        {
            e.printStackTrace()
            Log.e("CZ2006:ReverseGeocode","grpc failed T^T")
        }
        return "Geocode Failed"
    }

    /**
     *Find the location via address
     *
     * @param strAddress address of current location
     * @return Geopoint of the location found
     */
    fun getLocationFromAddress(strAddress: String): GeoPoint? {
        val gc= Geocoder(context, Locale.getDefault())
        val address: List<Address>
        var p1: GeoPoint?
        try {
            address = gc.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location: Address = address[0]
            location.latitude
            location.longitude
            p1 = GeoPoint(
                (location.latitude),
                (location.longitude)
            )
            return p1
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("CZ2006:ReverseGeocode","grpc failed T^T")
        }
        return null
    }
}