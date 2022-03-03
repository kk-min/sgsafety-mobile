package com.example.sgsafetyloginpage

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton: Button = findViewById(R.id.login_button)
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)

        val remember_password = findViewById<RadioButton>(R.id.remember_password)
        remember_password.setOnClickListener(View.OnClickListener {
            if (!(remember_password.isSelected)) {
                remember_password.isChecked = true
                remember_password.isSelected = true
            } else {
                remember_password.isChecked = false
                remember_password.isSelected = false
            }
        })

        loginButton.setOnClickListener{
            Toast.makeText(this, username.text.toString(), Toast.LENGTH_LONG).show()
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("Users")
            .whereEqualTo("username", username.text.toString())
            .get()
            .addOnSuccessListener {}

    }
}


