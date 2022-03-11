package com.example.sg_safety_mobile

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.*

class AlertPage : AppCompatActivity() {

    private val smsSender= SMSManager()
    private val permissionRequest = 101
    //private val userLocation =longlatToAddress(103.6920069,1.3525963)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert_page)

        //SEND SMS TO SCDF(Phone No.=111)
        sendMessage()
        Toast.makeText(this, "SMS Sent to SCDF", Toast.LENGTH_SHORT).show()
        //SEND USER LOCATION TO HELP
        //
        //
        //

        val buttonClick:Button = findViewById(R.id.i_am_savedButton)
        buttonClick.setOnClickListener {
            this.finish()
        }
    }


    //SEND SMS FUNCTION(NEED TO GET PERMISSION SO I PUT IN ACTIVITY CLASS INSTEAD OF FRAGMENT
    private fun sendMessage() {
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            smsSender.sendSMS()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS),
                permissionRequest)
        }
    }


    //CHECKING OF PHONE'S USER PERMISSION
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults:
    IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequest) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                smsSender.sendSMS();
            } else {
                Toast.makeText(this, "You don't have required permission to send a message",
                    Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*fun longlatToAddress(longitude: Double, latitude: Double): String {


        // Initializing Geocoder

        val mGeocoder = Geocoder(applicationContext, Locale.getDefault())
        var addressString = ""

        // Reverse-Geocoding starts
        try {
            val addressList: List<Address> = mGeocoder.getFromLocation(latitude, longitude, 1)

            // use your lat, long value here
            if (addressList != null && addressList.isNotEmpty()) {
                val address = addressList[0]
                val sb = StringBuilder()
                for (i in 0 until address.maxAddressLineIndex) {
                    sb.append(address.getAddressLine(i)).append("\n")
                }

                // Various Parameters of an Address are appended
                // to generate a complete Address
                if (address.premises != null)
                    sb.append(address.premises).append(", ")

                sb.append(address.subAdminArea).append("\n")
                sb.append(address.locality).append(", ")
                sb.append(address.adminArea).append(", ")
                sb.append(address.countryName).append(", ")
                sb.append(address.postalCode)

                // StringBuilder sb is converted into a string
                // and this value is assigned to the
                // initially declared addressString string.
                addressString = sb.toString()
            }
        } catch (e: IOException) {

        }

        // Finally, the address string is posted in the textView with LatLng.
        return "Address: $addressString"
    }*/

}