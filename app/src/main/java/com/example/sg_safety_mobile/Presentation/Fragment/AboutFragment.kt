package com.example.sg_safety_mobile.Presentation.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.sg_safety_mobile.R
import com.example.sg_safety_mobile.Logic.Adaptors.VPAadaptors
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class AboutFragment : Fragment() {

    private lateinit var tablayout :TabLayout
    private lateinit var viewPager :ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val v= inflater.inflate(R.layout.fragment_about, container, false)

        viewEInitializations(v)

        val adapter = VPAadaptors(parentFragmentManager , lifecycle)
        viewPager.adapter = adapter

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
    private fun viewEInitializations(v:View) {
        tablayout = v.findViewById(R.id.tabLayout3)
        viewPager = v.findViewById(R.id.viewPager)
    }
}