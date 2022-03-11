package com.example.sg_safety_mobile

import android.telephony.SmsManager
import android.text.TextUtils


class SMSManager (){
    private val helpNo = 111
    private val helpMessage = "Ambulance"
    //NEED TO CONVERT LONGITUDE LATITUDE TO POSTAL CODE,NEED TO BRING IN USER LOCATION

    //SEND SMS TO THE DESIGNATED PHONE NUMBER
    fun sendSMS() {
        if (TextUtils.isDigitsOnly(helpNo.toString())) {
                val smsManager: SmsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(helpNo.toString(), null, helpMessage+" I need help!I am currently at longitude 111,latitude 222", null, null)

        }
    }


}