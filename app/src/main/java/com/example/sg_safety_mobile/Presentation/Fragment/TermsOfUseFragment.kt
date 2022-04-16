package com.example.sg_safety_mobile.Presentation.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sg_safety_mobile.R

/**
 *Fragment that shows content of Terms of Use in the tab layout
 * of About Fragment[com.example.sg_safety_mobile.Presentation.Fragment.AboutFragment]
 *
 * @since 2022-4-15
 */
class TermsOfUse : Fragment() {

    /**
     *Runs when fragment is created
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_terms_of_use, container, false)
    }


}