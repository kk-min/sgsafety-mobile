package com.example.sg_safety_mobile

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.telephony.SmsManager
import android.text.TextUtils
import android.widget.TextView
import java.util.*


class SMSManager (){
    private val helpNo = 111
    private val helpMessage = "Ambulance"
    //NEED TO CONVERT LONGITUDE LATITUDE TO POSTAL CODE,NEED TO BRING IN USER LOCATION

    //SEND SMS TO THE DESIGNATED PHONE NUMBER
    fun sendSMS(location:String) {
        if (TextUtils.isDigitsOnly(helpNo.toString())) {
                val smsManager: SmsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(helpNo.toString(), null,
                    "$helpMessage I need help! I am currently at Latitude: 1.353174 Longitude: 103.9480955 Address:$location ", null, null)

        }
    }

}