package com.example.sg_safety_mobile


import android.content.Intent

import android.os.Bundle
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity



class HelperChoice : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helper_choice)

        val cprbutton: Button =findViewById(R.id.cpr)
        cprbutton.setOnClickListener {
            val intent = Intent(this, CPR_Map::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent)
        }

    }
}