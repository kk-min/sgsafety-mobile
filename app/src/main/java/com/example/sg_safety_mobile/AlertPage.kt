package com.example.sg_safety_mobile

import android.Manifest
import android.content.Context
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
            var location:String=reverseGeocode(1.353174,103.9480955)
            smsSender.sendSMS(location)
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
                var location:String=reverseGeocode(1.353174,103.9480955)
                smsSender.sendSMS(location);
            } else {
                Toast.makeText(this, "You don't have required permission to send a message",
                    Toast.LENGTH_SHORT).show();

            }
        }
    }
    private fun reverseGeocode(latitude:Double,longitude:Double):String{
        var gc= Geocoder(this, Locale.getDefault())
        var addresses= gc.getFromLocation(latitude,longitude,1)
        var address: Address = addresses[0]
        var addressStr:String="${address.getAddressLine(0)} ${address.locality}"
        return addressStr
    }

    /*private fun reverseGeocode(latitude:Double,longitude:Double,context:AppCompatActivity):String{
        var gc= Geocoder(context, Locale.getDefault())
        var addresses= gc.getFromLocation(latitude,longitude,1)
        var address: Address = addresses[0]
        var addressStr:String="${address.getAddressLine(0)} ${address.locality}"
        return addressStr
    }*/

}