package com.example.sg_safety_mobile.Presentation.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns.EMAIL_ADDRESS
import android.view.LayoutInflater
import android.view.View;
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText;
import android.widget.Toast;
import androidx.core.view.GravityCompat
import com.example.sg_safety_mobile.Logic.FirebaseManager
import com.example.sg_safety_mobile.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private val firebaseManager = FirebaseManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        supportActionBar?.title = "Reset Password";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        viewInitializations()

        val resetbtn= findViewById<Button>(R.id.bt_forget)
        resetbtn.setOnClickListener(View.OnClickListener {
            firebaseManager.performForgetPassword(resetbtn, etEmail)
        })
    }

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.activity_forget_password, container, false)
        return v
    }
    fun viewInitializations() {

        etEmail = findViewById(R.id.et_email)

        // To show back button in actionbar

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}