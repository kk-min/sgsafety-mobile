package com.example.sg_safety_mobile.Presentation.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.sg_safety_mobile.R
import com.example.sg_safety_mobile.Logic.Adaptors.VPAadaptors
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 *Fragment that show the About Page consist of about us,terms of use and privacy policy
 * accessed from Navigation drawer in MainActivity[com.example.sg_safety_mobile.Presentation.Activity.MainActivity]
 *
 * @since 2022-4-15
 */
class AboutFragment : Fragment() {

    /**
     *Table layout
     */
    private lateinit var tablayout :TabLayout
    /**
     * View pager
     */
    private lateinit var viewPager :ViewPager2


    /**
     *Runs when fragment is created
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        //initialize page
        val v= inflater.inflate(R.layout.fragment_about, container, false)
        viewEInitializations(v)

        val adapter = VPAadaptors(parentFragmentManager , lifecycle)
        viewPager.adapter = adapter

        //tab with about app, privacy policy and terms of use
        TabLayoutMediator(tablayout,viewPager){tab,position->
            when(position){
                0->{
                    tab.text="About The App"
                }
                1->{
                    tab.text="Privacy Policy"
                }
                2->{
                    tab.text="Terms Of Use"
                }
            }
        }.attach()

        return v
    }

    /**
     *Initialize all the UI views
     *
     * @param v view of this fragment
     */
    private fun viewEInitializations(v:View) {
        tablayout = v.findViewById(R.id.tabLayout3)
        viewPager = v.findViewById(R.id.viewPager)
    }
}