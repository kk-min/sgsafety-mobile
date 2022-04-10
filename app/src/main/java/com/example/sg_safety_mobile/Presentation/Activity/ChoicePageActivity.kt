package com.example.sg_safety_mobile.Presentation.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.sg_safety_mobile.R

class ChoicePageActivity : AppCompatActivity() {

    private lateinit var yesbutton: Button
    private lateinit var nobutton:Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choice_page)
        viewEInitializations()

        yesbutton.setOnClickListener{
            val intent = Intent(this, HelperChoiceActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        nobutton.setOnClickListener{
            this.finish()
        }
    }
    private fun viewEInitializations() {
        yesbutton=findViewById(R.id.yes)
        nobutton=findViewById(R.id.no)
    }
}