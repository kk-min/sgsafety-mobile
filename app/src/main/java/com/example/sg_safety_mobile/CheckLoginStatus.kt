package com.example.sg_safety_mobile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CheckLoginStatus : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_login_status)
        supportActionBar?.hide();

        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
        //get the login status from the shared preference ,default value is false if did not get ant value from it
        val handler = Handler()
        handler.postDelayed(
            Runnable {
                        if(sharedPreference.getBoolean("log in status",false))
                        {
                            Toast.makeText(this, "Welcome Back "+sharedPreference.getString("username",""), Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent)
                        }
                        else
                        {
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent)
                        }
                     },
            500
        )

    }
}