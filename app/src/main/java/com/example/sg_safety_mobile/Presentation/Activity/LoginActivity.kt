package com.example.sg_safety_mobile.Presentation.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.sg_safety_mobile.Logic.FirebaseManager
import com.example.sg_safety_mobile.R

/**
 *Activity for user to login when user login status is found to be false from
 * Check Login Status Activity[com.example.sg_safety_mobile.Presentation.Activity.CheckLoginStatusActivity]
 *
 * @since 2022-4-15
 */
class LoginActivity : AppCompatActivity() {

    /**
     *UI button to be pressed to login into the application
     */
    private lateinit var loginButton: Button
    /**
     *UI edit text for user to input their email
     */
    private lateinit var username :EditText
    /**
     *UI edit text for user to input their password
     */
    private lateinit var password :EditText
    /**
     *UI button to be pressed when user forget their account password
     */
    private lateinit var forgetpassword:TextView
    /**
     *UI text view that acts as a button to be pressed by user when he/she intend to register a new account
     */
    private lateinit var registerAcc:TextView
    /**
     *Firebase Manager
     */
    private val firebaseManager: FirebaseManager = FirebaseManager(this)

    /**
     *Runs when activity is created
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        //initialize current page
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        viewEInitializations()

        //hide support bar
        supportActionBar?.hide()

        //Shared Preference is used to keep user login
        val sharedPreference: SharedPreferences = getSharedPreferences("Login", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreference.edit()

        //Button-----------------------------------------------------------------------------------------------------
        //prompt to forget password page
        forgetpassword.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        //prompt to website register page
        registerAcc.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://sg-safety.web.app/Register"))
            startActivity(browserIntent)
        }

        //validate account info input after login button is pressed
        loginButton.setOnClickListener {
            firebaseManager.validateAcc(username.text.toString(), password.text.toString(), editor)
        }

    }

    /**
     *Initialize all the UI views
     */
    private fun viewEInitializations() {

        loginButton= findViewById(R.id.login_button)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        forgetpassword = findViewById(R.id.forgot_password)
        registerAcc = findViewById(R.id.register)
    }
}




