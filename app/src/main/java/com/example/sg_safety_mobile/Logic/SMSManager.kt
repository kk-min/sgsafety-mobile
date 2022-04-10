package com.example.sg_safety_mobile.Logic


import android.location.Location
import android.telephony.SmsManager
import android.text.TextUtils



class SMSManager (){
    private val helpNo = 111
    private val helpMessage = "Ambulance"

    //SEND SMS TO THE DESIGNATED PHONE NUMBER
    fun sendSMS(location:String,loc:Location) {
        if (TextUtils.isDigitsOnly(helpNo.toString())) {
                val smsManager: SmsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(helpNo.toString(), null,
                    "$helpMessage I need help! I am currently at Latitude: ${loc.latitude} Longitude: ${loc.longitude} Address:$location ", null, null)

        }
    }

}