package com.example.sg_safety_mobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.lang.String
import java.lang.String.format
import java.util.*

class ContactNumber : AppCompatActivity() {

    lateinit var contactNum: EditText
    lateinit var inputOTP: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_number)

        supportActionBar?.title = "Verify Contact";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        viewCInitializations()
    }

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.activity_edit_email, container, false)
        var returnC = v.findViewById<Button>(R.id.btn_sendOTP)
        var verifyContact = v.findViewById<Button>(R.id.verify_contact)
        var sendClickCount = 0
        var verifyClickCount = 0

        returnC.setOnClickListener(View.OnClickListener {
            sendClickCount+=1
            sendCode(returnC)
        })

        verifyContact.setOnClickListener(View.OnClickListener {
            verifyClickCount+=1
            fun onClick(v: View){
                contactNum = findViewById(R.id.mobileNum)
                inputOTP = findViewById(R.id.inputting_otp)

                when (sendClickCount) {
                    0 -> {

                        Toast.makeText(this,"Generate OTP First", Toast.LENGTH_SHORT).show()
                    }
                    in 1..3 -> {
                        if(contactUsed() && inputOTP.text.toString()!=""){
                            Toast.makeText(this,"Contact Number Already Exists", Toast.LENGTH_SHORT).show()
                            return
                        }
                        if(verifyCode(v , sendCode(v))){
                            Toast.makeText(this,"Contact Number Updated", Toast.LENGTH_SHORT).show()
                            //steps to update API
                        }
                    }
                    else -> {
                        Toast.makeText(this,"Too Many Failed Attempts! Try again Later", Toast.LENGTH_SHORT).show()
                    }
                }

                return
            }

        })

        return v
    }

    fun viewCInitializations() {
        contactNum = findViewById(R.id.mobileNum)
        inputOTP = findViewById(R.id.inputting_otp)
    }

    //verify if the input value is the same as the generated code from sendCode
    fun verifyCode(view: View , string: kotlin.String) : Boolean{
        var OTP = string
        if(contactNum.text.toString()!=OTP){
            inputOTP.error="Wrong OTP! Try again!"
            return false
        }
        return true
    }

    //verify if contact has already been used in API and check format
    fun contactUsed() : Boolean {

        //check if contact is Empty
        if(contactNum.text.toString() ==""){
            contactNum.error = "Please Enter Contact Number"
            return true
        }

        //check for format of the input number
        if(!contactNum.text.toString().matches("^(9|8)\\d{3}[- .]?\\d{4}$".toRegex())){
            contactNum.error = "Wrong Contact Number Format"
            return true
        }

        //check if present in API - dummy 91234567 if phone number in API
        var dummy = "91234567"
        if(contactNum.text.toString()== dummy){
            contactNum.error = "Phone number already in use"
            return true
        }
        return false
    }

    //generate and send OTP code via SMS
    fun sendCode(view:View) : kotlin.String {
        val random = Random()
        var generatedPassword = format("%06d", random.nextInt(1000000))
        Toast.makeText(this, generatedPassword, Toast.LENGTH_LONG).show()

        return generatedPassword.toString()
    }
    /*
        fun performContactVerification(view: View) {
            contactNum = findViewById(R.id.mobileNum)
            inputOTP = findViewById(R.id.inputting_otp)

            when (sendClickCount) {
                0 -> {

                    Toast.makeText(this,"Generate OTP First", Toast.LENGTH_SHORT).show()
                }
                in 1..3 -> {
                    if(contactUsed() && inputOTP.text.toString()!=""){
                        Toast.makeText(this,"Contact Number Already Exists", Toast.LENGTH_SHORT).show()
                        return
                    }
                    if(verifyCode(view , sendCode(view))){
                        Toast.makeText(this,"Contact Number Updated", Toast.LENGTH_SHORT).show()
                        //steps to update API
                    }
                }
                else -> {
                    Toast.makeText(this,"Too Many Failed Attempts! Try again Later", Toast.LENGTH_SHORT).show()
                }
            }

            return
        }
    */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}