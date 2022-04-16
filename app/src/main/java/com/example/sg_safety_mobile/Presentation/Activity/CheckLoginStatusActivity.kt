package com.example.sg_safety_mobile.Presentation.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sg_safety_mobile.R


/**
 *Activity that runs first when application is first started to check user login status
 * and prompt them to respective activity
 *
 * @since 2022-4-15
 */
class CheckLoginStatusActivity : AppCompatActivity() {

    /**
     *Runs when activity is created
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        //initialize current page
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_login_status)

        //hide support bar
        supportActionBar?.hide()

        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
        //get the login status from the shared preference ,default value is false if did not get ant value from it
        val handler = Handler()
        //while it is checking login status this page also act as a splash screen when opening app
        handler.postDelayed(
            {
                        if(sharedPreference.getBoolean("log in status",false))
                        {
                            Toast.makeText(this, "Welcome Back "+sharedPreference.getString("login_email",""), Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        else
                        {
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                     },
            700
        )

    }
}