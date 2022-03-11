package com.example.sg_safety_mobile


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog




class HomeFragment : Fragment(),View.OnClickListener {


    //FOR PAGE VIEW
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v= inflater.inflate(R.layout.fragment_home, container, false)
        var button: Button =v.findViewById(R.id.alert_button)


        //PROMPT ALERT BOX TO MAKE SURE USER REALLY NEED HELP
        button.setOnClickListener{
            showAlertDialog()
        }
        return v
    }


    //FOR PAGE VIEW
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    //ALERT TO MAKE SURE USER DON'T ACCIDENTALLY PRESS THE SEND HELP BUTTON
    private fun showAlertDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(activity!!)

        alertDialog.setTitle("Are you sure?")
        alertDialog.setMessage("By pressing Yes,help message will be sent to SCDF and Users nearby")
        alertDialog.setPositiveButton(
            "Yes"
        ) { _, _ ->
            //Go to Alert Page Activity which will display No. of helpers accepted to help
            val intent = Intent(activity, AlertPage::class.java)
            startActivity(intent)

        }
        //cancel the alert button
        alertDialog.setNegativeButton(
            "No"
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }


    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }


}