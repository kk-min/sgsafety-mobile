package com.example.sg_safety_mobile.Presentation.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.sg_safety_mobile.Presentation.Activity.ChangePasswordActivity
import com.example.sg_safety_mobile.Presentation.Activity.ContactNumberActivity
import com.example.sg_safety_mobile.Presentation.Activity.EditEmailActivity
import com.example.sg_safety_mobile.Presentation.Activity.UpdateCPRActivity
import com.example.sg_safety_mobile.R

/**
 *Fragment that show the page of Manage Profile consist of 4 options:
 * 1)edit email
 * 2)change password
 * 3)change contact no.
 * 4)update cpr certificate
 * and can be accessed via Navigation Drawer in
 * MainActivity[com.example.sg_safety_mobile.Presentation.Activity.MainActivity]
 *
 * @since 2022-4-15
 *
 */
class ManageProfileFragment : Fragment() {

    /**
     *UI button that start update email activity when pressed
     */
    private lateinit var email_button:Button
    /**
     *UI button that start update contact no. activity when pressed
     */
    private lateinit var contact_button:Button
    /**
     *UI button that start update password activity when pressed
     */
    private lateinit var pwd_button:Button
    /**
     *UI button that start update cpr certificate activity when pressed
     */
    private lateinit var cpr_button:Button



    /**
     *Runs when fragment is created
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v= inflater.inflate(R.layout.fragment_manage_profile, container, false)
        viewEInitializations(v)

        //NAVIGATE TO RELEVANT SUBPAGES
        email_button.setOnClickListener{
            val intent = Intent( activity , EditEmailActivity::class.java)
            startActivity(intent)
        }

        contact_button.setOnClickListener{
            val intent = Intent( activity , ContactNumberActivity::class.java)
            startActivity(intent)
        }

        pwd_button.setOnClickListener{
            val intent = Intent( activity , ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        cpr_button.setOnClickListener{
            val intent = Intent( activity , UpdateCPRActivity::class.java)
            startActivity(intent)
        }

        return v
    }

    /**
     *Initialize all the UI views
     *
     * @param v view of this fragment
     */
    private fun viewEInitializations(v:View) {

        email_button =v.findViewById(R.id.btn_email)
        contact_button=v.findViewById(R.id.btn_contact)
        pwd_button =v.findViewById(R.id.btn_password)
        cpr_button =v.findViewById(R.id.btn_cpr)
    }

}