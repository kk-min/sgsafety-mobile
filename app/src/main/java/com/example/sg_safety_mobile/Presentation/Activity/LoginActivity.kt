package com.example.sg_safety_mobile.Presentation.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.sg_safety_mobile.Logic.FirebaseManager
import com.example.sg_safety_mobile.R
import org.w3c.dom.Text

class LoginActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var username :EditText
    private lateinit var password :EditText
    private lateinit var forgetpassword:TextView
    private lateinit var registerAcc:TextView
    private val firebaseManager: FirebaseManager = FirebaseManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide();
        viewEInitializations()
        //Shared Preference is used to keep user login
        val sharedPreference: SharedPreferences = getSharedPreferences("Login", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreference.edit()


        forgetpassword.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

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
    private fun viewEInitializations() {

        loginButton= findViewById(R.id.login_button)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        forgetpassword = findViewById(R.id.forgot_password)
        registerAcc = findViewById(R.id.register)
    }
}




