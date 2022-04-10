package com.example.sg_safety_mobile.Presentation.Activity


import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.sg_safety_mobile.Logic.FirebaseManager
import com.example.sg_safety_mobile.R


class ChangePasswordActivity : AppCompatActivity() {

    private val MIN_PASSWORD_LENGTH = 8;
    private lateinit var etPassword:EditText
    private lateinit var etRepeatPassword: EditText
    private lateinit var etCurrentPassword: EditText
    private lateinit var resetbtn:Button
    private val firebaseManager=FirebaseManager(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        supportActionBar?.title = "Reset Password";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        viewInitializations()

        resetbtn.setOnClickListener(View.OnClickListener {
            firebaseManager.performResetPassword(etPassword,etRepeatPassword,etCurrentPassword)
        })
    }

    private fun viewInitializations() {
        etPassword = findViewById(R.id.et_password)
        etRepeatPassword = findViewById(R.id.et_repeat_password)
        etCurrentPassword = findViewById(R.id.cur_password)
        resetbtn= findViewById(R.id.bt_forget)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
