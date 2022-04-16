package com.example.sg_safety_mobile.Presentation.Activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.sg_safety_mobile.Logic.FirebaseManager
import com.example.sg_safety_mobile.R

/**
 *Activity that allows user to change their email which is accessed
 * via Manage Profile Fragment[com.example.sg_safety_mobile.Presentation.Fragment.ManageProfileFragment]
 *
 * @since 2022-4-15
 */
class EditEmailActivity : AppCompatActivity() {

    /**
     *UI edit text for user to enter his/her account password
     */
    private lateinit var currentPs: EditText
    /**
     *UI edit text for user to enter his/her new email
     */
    private lateinit var currentEmail: EditText
    /**
     *UI button to be pressed to update user accoutn email
     */
    private lateinit var updateEmail:Button
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
        setContentView(R.layout.activity_edit_email)
        viewEInitializations()

        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
        val curr = sharedPreference.getString("login_email" , null).toString()

        //set email hint
        currentEmail.hint = curr

        //setup support bar and back button
        supportActionBar?.title = "Edit Email"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        //Button-------------------------------------------------------------------------------------------------------
        //click to update email
        updateEmail.setOnClickListener{
            firebaseManager.performEmailReset(updateEmail, currentEmail, currentPs)
        }
    }

    /**
     *Initialize all the UI views
     */
    private fun viewEInitializations() {
        currentPs = findViewById(R.id.cur_password)
        currentEmail = findViewById(R.id.current_email)
        updateEmail=findViewById(R.id.btn_updateEmail)
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