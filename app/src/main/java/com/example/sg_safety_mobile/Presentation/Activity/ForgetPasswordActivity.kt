package com.example.sg_safety_mobile.Presentation.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText;
import com.example.sg_safety_mobile.Logic.FirebaseManager
import com.example.sg_safety_mobile.R

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var resetbtn:Button
    private val firebaseManager = FirebaseManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        supportActionBar?.title = "Reset Password";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        viewInitializations()

        resetbtn.setOnClickListener{
            firebaseManager.performForgetPassword(resetbtn, etEmail)
        }
    }
    private fun viewInitializations() {
        etEmail = findViewById(R.id.et_email)
        resetbtn= findViewById(R.id.bt_forget)
    }
    // To show back button in actionbar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}