package com.example.sg_safety_mobile.Presentation.Activity

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
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


class AlertPageActivity : AppCompatActivity() {

    private val geoCoder = ReverseGeocoder(this)
    private lateinit var lm: LocationManager
    private lateinit var loc: Location
    private lateinit var address:String
    private lateinit var buttonClick:Button
    private val smsSender= SMSManager()
    private val permissionRequest = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert_page)
        viewEInitializations()

        //SEND SMS TO SCDF(Phone No.=111)
        loc=getCurrentLocation()
        address=geoCoder.reverseGeocode(loc.latitude,loc.longitude)
        sendMessage(address,loc)

        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
        val userId=sharedPreference.getString("UserID","").toString()
        val location=getCurrentLocation()
        val userGeopoint= GeoPoint(location.latitude,location.longitude)
        MyFirebaseMessagingService.sendMessage("SG Safety Need You!","There is a user nearby that require help, please click on notification to continue","HelpMessage",userId,userGeopoint)

        Toast.makeText(this, "Help Message Broadcasted, SMS Sent to SCDF", Toast.LENGTH_SHORT).show()
        Log.d("CZ2006:AlertPageActivity", "SMS and Notification sent to other user")

        buttonClick.setOnClickListener {
            this.finish()
        }
    }

    private fun viewEInitializations() {
        buttonClick= findViewById(R.id.i_am_savedButton)
    }

    //CHECKING OF PHONE'S USER PERMISSION
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults:
    IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequest) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //var location:String=reverseGeocode(1.353174,103.9480955)
                smsSender.sendSMS(address,loc);
            } else {
                Toast.makeText(this, "You don't have required permission to send a message", Toast.LENGTH_SHORT).show();
                Log.d("CZ2006:AlertPageActivity", "Need Permision for sending sms")

            }
        }
    }

    //SEND SMS FUNCTION(NEED TO GET PERMISSION SO I PUT IN ACTIVITY CLASS INSTEAD OF FRAGMENT
    private fun sendMessage(address: String,loc:Location) {
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            smsSender.sendSMS(address,loc)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS),
                permissionRequest)
        }
    }

    private fun getCurrentLocation():Location{
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            &&ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),111)
        }

        lm=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
        return location
    }


}