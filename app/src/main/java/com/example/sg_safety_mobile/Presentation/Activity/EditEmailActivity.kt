package com.example.sg_safety_mobile.Presentation.Activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sg_safety_mobile.Logic.FirebaseManager
import com.example.sg_safety_mobile.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class EditEmailActivity : AppCompatActivity() {

    private lateinit var currentPs: EditText
    private lateinit var currentEmail: EditText
    private val firebaseManager = FirebaseManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_email)

        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
        val curr = sharedPreference.getString("login_email" , null).toString()
        val em_hint = findViewById<EditText>(R.id.current_email)

        em_hint.hint = curr

        supportActionBar?.title = "Edit Email"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewEInitializations()

        var updateEmail = findViewById<Button>(R.id.btn_updateEmail)
        updateEmail.setOnClickListener(View.OnClickListener {
            firebaseManager.performEmailReset(updateEmail, currentEmail, currentPs)
        })
    }

    private fun viewEInitializations() {
        currentPs = findViewById(R.id.cur_password)
        currentEmail = findViewById(R.id.current_email)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}