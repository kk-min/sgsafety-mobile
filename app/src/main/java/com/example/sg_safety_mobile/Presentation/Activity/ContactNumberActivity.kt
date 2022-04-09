package com.example.sg_safety_mobile.Presentation.Activity

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
import com.example.sg_safety_mobile.Logic.FirebaseManager
import com.example.sg_safety_mobile.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.String.format
import java.util.*


class ContactNumberActivity : AppCompatActivity() {

    private lateinit var contactNum: EditText
    private lateinit var inputOTP: EditText
    private lateinit var givenOTP: String
    private val firebaseManager = FirebaseManager(this);

    private val permissionCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_number)

        supportActionBar?.title = "Change Contact"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        viewCInitializations()

        val returnC = findViewById<Button>(R.id.btn_sendOTP)
        val verifyContact = findViewById<Button>(R.id.verify_contact)

        returnC.setOnClickListener(View.OnClickListener {
            firebaseManager.sendCode(returnC, contactNum, checkForSmsPermission())
        })

        verifyContact.setOnClickListener(View.OnClickListener {
            firebaseManager.verifyCode(verifyContact, contactNum, inputOTP)
        })
    }

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.activity_edit_email, container, false)
        val returnC = v.findViewById<Button>(R.id.btn_sendOTP)
        val verifyContact = v.findViewById<Button>(R.id.verify_contact)

        return v
    }

    private fun viewCInitializations() {
        contactNum = findViewById(R.id.mobileNum)
        inputOTP = findViewById(R.id.inputting_otp)
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