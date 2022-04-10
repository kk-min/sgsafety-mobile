package com.example.sg_safety_mobile.Presentation.Activity


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.sg_safety_mobile.R


class HelperChoiceActivity : AppCompatActivity() {

    private lateinit var cprbutton:Button
    private lateinit var aedbutton:Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helper_choice)
        viewEInitializations()

        cprbutton.setOnClickListener {
            val intent = Intent(this, CPRMapActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


        aedbutton.setOnClickListener {
            val intent = Intent(this, AEDMapActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }
    private fun viewEInitializations() {
        cprbutton=findViewById(R.id.cpr)
        aedbutton=findViewById(R.id.aed)
    }
}