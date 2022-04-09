package com.example.sg_safety_mobile.Presentation.Activity


import android.content.Intent

import android.os.Bundle
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity
import com.example.sg_safety_mobile.R


class HelperChoiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helper_choice)

        val cprbutton: Button =findViewById(R.id.cpr)
        cprbutton.setOnClickListener {
            val intent = Intent(this, CPRMapActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent)
        }

        val aedbutton:Button=findViewById(R.id.aed)
        aedbutton.setOnClickListener {
            val intent = Intent(this, AEDMapActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent)
        }

    }
}