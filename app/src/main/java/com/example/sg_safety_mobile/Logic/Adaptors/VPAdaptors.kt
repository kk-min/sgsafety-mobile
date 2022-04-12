package com.example.sg_safety_mobile.Logic.Adaptors

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sg_safety_mobile.Presentation.Fragment.AboutUs
import com.example.sg_safety_mobile.Presentation.Fragment.PrivacyPolicy
import com.example.sg_safety_mobile.Presentation.Fragment.TermsOfUse


class VPAadaptors(fragmentManager: FragmentManager,lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return   when(position){
            0->{
                AboutUs()
            }
            1->{
                PrivacyPolicy()
            }
            2->{
                TermsOfUse()
            }
            else->{
                Fragment()
            }

        }
    }
}