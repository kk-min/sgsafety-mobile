package com.example.sg_safety_mobile.Logic


import android.location.Location
import android.telephony.SmsManager
import android.text.TextUtils


/**
 *Class that is used for sending of SMS to SCDF
 *
 * @since 2022-4-15
 */
class SMSManager (){
    /**
     *Phone No. to be sent to
     */
    private val helpNo = 111
    /**
     *Message to be sent (Vehicle needed)
     */
    private val helpMessage = "Ambulance"

    /**
     *Send SMS to a certain phone no.
     *
     * @param loc current location
     * @param location address of current location
     */
    //SEND SMS TO THE DESIGNATED PHONE NUMBER
    fun sendSMS(location:String,loc:Location) {
        if (TextUtils.isDigitsOnly(helpNo.toString())) {
                val smsManager: SmsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(helpNo.toString(), null,
                    "$helpMessage I need help! I am currently at Latitude: ${loc.latitude} Longitude: ${loc.longitude} Address:$location ", null, null)

        }
    }

}