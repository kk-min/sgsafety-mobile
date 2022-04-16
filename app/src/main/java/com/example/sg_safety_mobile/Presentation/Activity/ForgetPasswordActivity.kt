package com.example.sg_safety_mobile.Presentation.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.sg_safety_mobile.Logic.FirebaseManager
import com.example.sg_safety_mobile.R

/**
 *Activity for user to reset their account password when the forget their password and is accessed
 * via Manage Profile Fragment[com.example.sg_safety_mobile.Presentation.Activity.LoginActivity]
 *
 * @since 2022-4-15
 */
class ForgetPasswordActivity : AppCompatActivity() {

    /**
     *UI edit text for user to enter their email address
     */
    private lateinit var etEmail: EditText
    /**
     *UI button to be pressed to send a validation email to user email for resetting of password
     */
    private lateinit var resetbtn:Button
    /**
     *Firebase Manager
     */
    private val firebaseManager = FirebaseManager(this)

    /**
     *Runs when activity is created
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        //initialize current page
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        viewInitializations()

        //setup support bar and back button
        supportActionBar?.title = "Reset Password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //Button------------------------------------------------------------------------------------------------------
        //click to reset user password
        resetbtn.setOnClickListener{
            firebaseManager.performForgetPassword(resetbtn, etEmail)
        }
    }

    /**
     *Initialize all the UI views
     */
    private fun viewInitializations() {
        etEmail = findViewById(R.id.et_email)
        resetbtn= findViewById(R.id.bt_forget)
    }


    /**
     *Runs when back button is pressed
     *
     * @return true when button is pressed
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}