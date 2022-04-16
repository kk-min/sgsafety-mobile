package com.example.sg_safety_mobile.Presentation.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.sg_safety_mobile.R

/**
 *Acitivty that runs when user receives a notification sent bu victim within 400m
 * and pressed on it, this activity allow user to choose whether
 * they intend to help the victim or not
 *
 * @since 2022-4-15
 */
class ChoicePageActivity : AppCompatActivity() {

    /**
     *UI button to be pressed when user intend to help
     */
    private lateinit var yesbutton: Button
    /**
     *UI button to be pressed when user does not intend to help
     */
    private lateinit var nobutton:Button

    /**
     *Runs when activity is created
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        //initialize current page
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choice_page)
        viewEInitializations()

        //Button----------------------------------------------------------------------------------------
        //accept to help victim and prompt to cpr/aed selection(Helper Choice Page)
        yesbutton.setOnClickListener{
            val intent = Intent(this, HelperChoiceActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        //choose not to help and exit the app
        nobutton.setOnClickListener{
            this.finish()
        }
    }

    /**
     *Initialize all the UI views
     */
    private fun viewEInitializations() {
        yesbutton=findViewById(R.id.yes)
        nobutton=findViewById(R.id.no)
    }
}