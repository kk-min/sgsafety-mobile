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



class EditEmail : AppCompatActivity() {

    lateinit var currentPs: EditText
    lateinit var currentEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_email)

        supportActionBar?.title = "Edit Email";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        viewEInitializations()
    }

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.activity_edit_email, container, false)
        var updateEmail = v.findViewById(R.id.btn_updateEmail) as Button

        updateEmail.setOnClickListener(View.OnClickListener {
            performEmailReset(updateEmail)
        })

        return v
    }

    fun viewEInitializations() {
        currentPs = findViewById(R.id.cur_password)
        currentEmail = findViewById(R.id.current_email)
    }

    //retrieved password from the firebase will be a parameter of this function
    private fun validateEInput(): Boolean {
        var dummy = "Hello1234!"

        if (currentPs.text.toString() == "") {
            currentPs.error = "Please Enter Current Password"
            return false
        }

        if (currentPs.text.toString() != dummy) {
            currentPs.error = "Wrong Current Password"
            return false
        }

        if (currentEmail.text.toString() == "") {
            currentEmail.error = "Please Enter New Email"
            return false
        }
        //Email format check
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentEmail.text.toString()).matches()) {
            currentEmail.error = "Email Format Error"
            return false
        }

        return true
    }

    //Check if email already exists in the Firebase
    fun emailUsed(): Boolean {
        if(currentEmail.text.toString() == ""){
            return true
        }
        if(currentEmail.text.toString() == "email"){
            return true
        }
        return false
    }

    fun performEmailReset(view: View) {

        //If email exists in firebase, error message and error notice
        if(emailUsed()){
            Toast.makeText(this,"Email Has Already Been Used", Toast.LENGTH_SHORT).show()
        }

        else{
            if (validateEInput()) {

                // Input is valid, here send data to your server

                val password = currentPs.text.toString()
                val newEmail = currentEmail.text.toString()

                Toast.makeText(this,"Email Successfully Updated", Toast.LENGTH_SHORT).show()
                // Here you can call you API
                //

            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}