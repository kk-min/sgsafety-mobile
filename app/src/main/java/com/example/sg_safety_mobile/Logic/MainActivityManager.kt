package com.example.sg_safety_mobile.Logic

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import com.example.sg_safety_mobile.Presentation.Activity.LoginActivity


class MainActivityManager(val context: Context) {
    val locationService: LocationService=LocationService()
    var locationServiceIntent: Intent? = null
    lateinit var locationReceiver: LocationReceiver

    @RequiresApi(Build.VERSION_CODES.O)
    fun promptSignOutAlert(){
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)

        alertDialog.setTitle("Sign Out")
        alertDialog.setMessage("Are You Sure?")
        alertDialog.setPositiveButton(
            "Yes"
        ) { _, _ ->
            Toast.makeText(context, "Signed Out", Toast.LENGTH_SHORT).show()
            val sharedPreference: SharedPreferences = context.getSharedPreferences("Login",
                AppCompatActivity.MODE_PRIVATE
            )
            val editor: SharedPreferences.Editor=sharedPreference.edit()
            editor.clear()
            editor.putBoolean("log in status",false)
            editor.commit()
            stopLocationService()
            MyFirebaseMessagingService.unsubscribeTopic(context,"HelpMessage")

            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent)
        }
        //cancel the alert button
        alertDialog.setNegativeButton(
            "No"
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()

    }

    fun startLocationService(){
        Log.d("CZ2006:MainActivity", "LocationService Starting...")
        //locationService =
        locationServiceIntent = Intent(context, locationService!!.javaClass)
        if (!isMyServiceRunning(locationService!!.javaClass)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(locationServiceIntent)
            } else {
                context.startService(locationServiceIntent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun stopLocationService(){
        val intent = Intent(context, LocationService::class.java)
        intent.action = "StopService"

        Log.d("CZ2006:MainActivity", "LocationService Starting...")
        //locationService =
        if (isMyServiceRunning(locationService!!.javaClass)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)

            } else {
                context.startForegroundService(intent)
            }
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("CZ2006:Service status", "Running")
                return true
            }
        }
        Log.i("CZ2006:Service status", "Not running")
        return false
    }
}