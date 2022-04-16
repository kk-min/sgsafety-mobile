package com.example.sg_safety_mobile.Presentation.Activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.*
import com.example.sg_safety_mobile.Logic.FirebaseManager
import com.example.sg_safety_mobile.R

/**
 *Activity that allows user to change their contact number which is accessed
 * via Manage Profile Fragment[com.example.sg_safety_mobile.Presentation.Fragment.ManageProfileFragment]
 *
 * @since 2022-4-15
 */
class ContactNumberActivity : AppCompatActivity() {

    /**
     *UI edit text to let user enter the contact no. they intend to update
     */
    private lateinit var contactNum: EditText
    /**
     *UI edit text for user to input the OTP sent to him/her
     */
    private lateinit var inputOTP: EditText
    /**
     *UI button to send OTP to a specific contact no. when pressed
     */
    private lateinit var returnC:Button
    /**
     *UI button to verify the OTP entered by user when pressed
     */
    private lateinit var verifyContact:Button
    /**
     *Firebase Manager
     */
    private val firebaseManager = FirebaseManager(this)
    /**
     *Permission request code
     */
    private val permissionCode = 1

    /**
     *Runs when activity is created
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //initialize current page
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_number)
        viewEInitializations()

        //setup support bar and back button
        supportActionBar?.title = "Change Contact"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //Button-------------------------------------------------------------------------------------------------
        //click to send otp to new phone number
        returnC.setOnClickListener{
            firebaseManager.sendCode(returnC, contactNum, checkForSmsPermission())
        }

        //click to verify otp and update phone no.
        verifyContact.setOnClickListener{
            firebaseManager.verifyCode(verifyContact, contactNum, inputOTP)
        }
    }

    /**
     *Initialize all the UI views
     */
    private fun viewEInitializations() {
        contactNum = findViewById(R.id.mobileNum)
        inputOTP = findViewById(R.id.inputting_otp)
        returnC = findViewById(R.id.btn_sendOTP)
        verifyContact = findViewById(R.id.verify_contact)
    }

    /**
     *Check for user device SMS permission
     *
     * @return validation of permission
     */
    //checking if user has enabled the checking of password
    private fun checkForSmsPermission() : Boolean {
        if (checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), permissionCode)
            return false
        }
        return true
    }

    /**
     *Request for permission
     *
     * @param requestCode request code
     * @param permissions array of permission to be requested
     * @param grantResults result of requested permission
     */
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

    /**
     *Do something when a certain item is selected
     *
     * @param item  menu item to be selected
     *
     * @return true when item selected false otherwise
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}