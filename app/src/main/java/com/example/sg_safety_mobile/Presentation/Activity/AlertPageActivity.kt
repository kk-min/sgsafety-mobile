package com.example.sg_safety_mobile.Presentation.Activity

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sg_safety_mobile.Logic.MyFirebaseMessagingService
import com.example.sg_safety_mobile.Logic.ReverseGeocoder
import com.example.sg_safety_mobile.Logic.SMSManager
import com.example.sg_safety_mobile.R
import org.osmdroid.util.GeoPoint
import java.util.*

class AlertPageActivity : AppCompatActivity() {
    lateinit var lm: LocationManager
    lateinit var loc: Location
    private val smsSender= SMSManager()
    val geoCoder = ReverseGeocoder(this)
    private val permissionRequest = 101
    lateinit var address:String
    //private val userLocation =longlatToAddress(103.6920069,1.3525963)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert_page)

        //SEND SMS TO SCDF(Phone No.=111)

        loc=getUserLocation()
        address=geoCoder.reverseGeocode(loc.latitude,loc.longitude)
        sendMessage(address)

        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
        val userId=sharedPreference.getString("UserID","").toString()
        val location=getUserLocation()
        val userGeopoint= GeoPoint(location.latitude,location.longitude)
        MyFirebaseMessagingService.sendMessage("SG Safety Need You!","There is a user nearby that require help, please click on notification to continue","HelpMessage",userId,userGeopoint)



        Toast.makeText(this, "SMS Sent to SCDF", Toast.LENGTH_SHORT).show()
        Log.d("CZ2006:AlertPage", "SMS and Notification sent to other user")

        val buttonClick:Button = findViewById(R.id.i_am_savedButton)
        buttonClick.setOnClickListener {
            this.finish()
        }
    }


    //CHECKING OF PHONE'S USER PERMISSION
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults:
    IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequest) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //var location:String=reverseGeocode(1.353174,103.9480955)
                smsSender.sendSMS(address);
            } else {
                Toast.makeText(this, "You don't have required permission to send a message", Toast.LENGTH_SHORT).show();
                Log.d("CZ2006:AlertPage", "Need Permision for sending sms")

            }
        }
    }

    //SEND SMS FUNCTION(NEED TO GET PERMISSION SO I PUT IN ACTIVITY CLASS INSTEAD OF FRAGMENT
    private fun sendMessage(address: String) {
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //var location:String=reverseGeocode(latitude,longitude)
            smsSender.sendSMS(address)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS),
                permissionRequest)
        }
    }

    fun getUserLocation():Location{
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            &&ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),111)
        }

        lm=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
        return location
    }
    /*private fun reverseGeocode(latitude:Double,longitude:Double,context:AppCompatActivity):String{
        var gc= Geocoder(context, Locale.getDefault())
        var addresses= gc.getFromLocation(latitude,longitude,1)
        var address: Address = addresses[0]
        var addressStr:String="${address.getAddressLine(0)} ${address.locality}"
        return addressStr
    }*/

}