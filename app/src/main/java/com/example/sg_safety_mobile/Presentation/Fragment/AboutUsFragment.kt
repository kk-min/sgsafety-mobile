package com.example.sg_safety_mobile.Presentation.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sg_safety_mobile.R

/**
 *Fragment that shows the About us content in tab layout in
 * About Fragment[com.example.sg_safety_mobile.Presentation.Fragment.AboutFragment]
 *
 * @since 2022-4-15
 */
class AboutUs : Fragment() {

    /**
     *Runs when fragment is created
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_us, container, false)
    }

}