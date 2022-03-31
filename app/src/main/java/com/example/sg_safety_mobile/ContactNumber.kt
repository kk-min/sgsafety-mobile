package com.example.sg_safety_mobile

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.String.format
import java.util.*


class ContactNumber : AppCompatActivity() {

    private lateinit var contactNum: EditText
    private lateinit var inputOTP: EditText
    private lateinit var givenOTP: kotlin.String

    private val permissionCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_number)

        supportActionBar?.title = "Change Contact"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        viewCInitializations()
    }

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.activity_edit_email, container, false)
        val returnC = v.findViewById<Button>(R.id.btn_sendOTP)
        val verifyContact = v.findViewById<Button>(R.id.verify_contact)


        returnC.setOnClickListener(View.OnClickListener {
            sendCode(returnC)

        })

        verifyContact.setOnClickListener(View.OnClickListener {
                verifyCode(verifyContact)
        })

        return v
    }

    private fun viewCInitializations() {
        contactNum = findViewById(R.id.mobileNum)
        inputOTP = findViewById(R.id.inputting_otp)
    }


    //generate and send OTP code via SMS
    fun sendCode(view:View) {

        val db = Firebase.firestore
        var invalid=0

        //check if contact is Empty
        if(contactNum.text.toString() ==""){
            contactNum.error = "Please Enter Contact Number"
            return
        }

        //check for format of the input number
        /*if(!contactNum.text.toString().matches("^(9|8)\\d{3}[- .]?\\d{4}$".toRegex())){
            contactNum.error = "Wrong Contact Number Format"
            return
        }*/


        //check if contact number has been used
        db.collection("Users")
            .get()
            //when retrieval is successful
            .addOnCompleteListener {
                for(document in it.result!!) {

                    //if registered users do not have a registered contact number
                    if(!document.data.containsKey("contact")){
                        continue
                    }

                    val contactInDoc: kotlin.String = document.data.getValue("contact").toString()

                    //if contact is the same, do not allow update
                    if (contactInDoc == contactNum.text.toString()) {
                        contactNum.error = "Phone Number Already In Use"
                        Toast.makeText(this, "Contact Number Reset Unsuccessful", Toast.LENGTH_SHORT).show()
                        invalid = 1
                        break
                    }
                }

                if(invalid==0) {
                    val random = Random()
                    val generatedPassword = format("%06d", random.nextInt(1000000))
                    givenOTP = generatedPassword.toString()
                    val num = contactNum.text.toString()
                    try{
                        if(checkForSmsPermission()){
                            val smsManager: SmsManager = SmsManager.getDefault()
                            val msg= "Your SMS OTP is: $givenOTP . Use it within 2 minutes to access the SG Safety app"
                            smsManager.sendTextMessage(num , null, msg, null, null)

                            //timer for 2 minutes. Each OTP is valid for 2 minutes only
                            val time = object : CountDownTimer(120000, 1000) {

                                override fun onTick(millisUntilFinished: Long) {
                                    var dur = millisUntilFinished/1000
                                }

                                override fun onFinish() {
                                    Toast.makeText(this@ContactNumber , "OTP Expired" , Toast.LENGTH_SHORT).show()
                                    givenOTP=""
                                }
                            }.start()

                            //Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e : Exception) {
                        Toast.makeText(this, "Requesting for permission", Toast.LENGTH_SHORT).show()
                    }
                   }
            }
            //when retrieval of collection is failed
            .addOnFailureListener {
                Toast.makeText(this, "Cannot access to Firebase!", Toast.LENGTH_LONG).show() }


    }



    fun verifyCode(view: View) {
        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
        val db = Firebase.firestore
        var docid: kotlin.String = sharedPreference.getString("UserID" , null).toString()

        if(contactNum.text.toString()==""){
            contactNum.error="Please Enter A New Contact Number"
            return
        }

        if(inputOTP.text.toString()==""){
            inputOTP.error="Please Enter OTP"
            return
        }

        if(givenOTP==""){
            inputOTP.error="OTP Expired. Generate another OTP"
            return
        }

        if(inputOTP.text.toString()!=givenOTP){
            inputOTP.error="Wrong OTP"
            return
        }
        else{
            db.collection("Users").document(docid).update("contact" , contactNum.text.toString())
            Toast.makeText(this,"Contact Number Reset Successfully", Toast.LENGTH_SHORT).show()
        }
    }

    //checking if user has enabled the checking of password
    private fun checkForSmsPermission() : Boolean {
        if (checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this , "Permission denied" , Toast.LENGTH_SHORT).show()
            //if no permission is granted, request from users
            requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), permissionCode)
            return false
        }
        return true
    }

    //requesting permission from user
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionCode -> {
                if (permissions[0].equals(Manifest.permission.SEND_SMS, ignoreCase = true)
                    && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(this , "Permission granted" , Toast.LENGTH_SHORT).show()
                } else {
                    // Permission denied.
                    Toast.makeText(this , "Permission denied" , Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}