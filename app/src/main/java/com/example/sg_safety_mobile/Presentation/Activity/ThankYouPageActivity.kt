package com.example.sg_safety_mobile.Presentation.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sg_safety_mobile.R

/**
 *Activity to display thank you for user help which is accessed via "I am here" button from
 * CPRMapActivity[com.example.sg_safety_mobile.Presentation.Activity.CPRMapActivity]
 *
 * @since 2022-4-15
 */
class ThankYouPageActivity : AppCompatActivity() {

    /**
     *Runs when activity is created
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //initialize current page
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thank_you_page)

    }
}