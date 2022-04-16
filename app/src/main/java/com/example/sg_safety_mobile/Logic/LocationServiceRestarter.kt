package com.example.sg_safety_mobile.Logic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

/**
 *Class that runs in order to restart the constantly location updates when the app is killed and this class inherits
 * Broadcast Receiver class
 *
 * @since 2022-4-15
 */
class LocationServiceRestarter : BroadcastReceiver() {

    /**
     * Runs when intent is received
     *
     * @param context   application context
     * @param   intent  intent to be started
     */
    override fun onReceive(context: Context, intent: Intent?) {

        Log.i("CZ2006:LocationServiceRestarter:Broadcast Listened", "Service tried to stop")
        Log.i("CZ2006:LocationServiceRestarter:Broadcast Listened", "Service restarted")
        //Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, LocationService::class.java))
        } else {
            context.startService(Intent(context, LocationService::class.java))
        }
    }
}