package com.example.sg_safety_mobile.Logic.Adaptors

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sg_safety_mobile.Presentation.Fragment.AboutUs
import com.example.sg_safety_mobile.Presentation.Fragment.PrivacyPolicy
import com.example.sg_safety_mobile.Presentation.Fragment.TermsOfUse

/**
 *View Pagers Adapter Class for About Fragment[com.example.sg_safety_mobile.Presentation.Fragment.AboutFragment]
 * that inherits FragmentStatesAdapter
 *
 * @since 2022-4-15
 */
class VPAadaptors(fragmentManager: FragmentManager,lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {

    /**
     * (fragmentManager)
     *  In-built manager for fragment
     */

    /**
     * (lifecycle)
     *Managing the state of Activity like when its start, stop, user, using, not in front of the user, no more longer
     */

    /**
     * Return the no of items in the About Pages
     */
    override fun getItemCount(): Int {
        return 3
    }

    /**
     *Functions that runs when the fragment is created
     */
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