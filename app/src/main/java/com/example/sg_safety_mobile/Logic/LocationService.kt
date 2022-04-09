package com.example.sg_safety_mobile.Logic

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LocationService : Service() {
    var counter = 0
    val firebaseManager: FirebaseManager = FirebaseManager(this)

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) startMyOwnForeground() else startForeground(
            1,
            Notification()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = "example.permanence"
        val channelName = "Location Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager =
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)!!
        manager.createNotificationChannel(chan)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setContentTitle("App is running in background")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startLocationUpdates()
        //startTimer()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        //stoptimertask()
        stopLocationUpdates()
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, LocationServiceRestarter::class.java)
        this.sendBroadcast(broadcastIntent)
    }
    private var lm: LocationManager?=null
    private var loc: Location?=null
    private var ll: LocationListener?=null
    fun startLocationUpdates() {

        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            return
        }

        ll = object : LocationListener {
            override fun onLocationChanged(p0: Location) {

                Log.d("CZ2006:LocationService", "Location Changed! Doing Location Update.....")
                val geopoint = com.google.firebase.firestore.GeoPoint(p0.latitude, p0.longitude)
                firebaseManager.updateUserLocationToDatabase(geopoint)
                sendDataToActivity(p0)
                sendDataToActivity2(p0)

            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

            }

            override fun onProviderEnabled(provider: String) {

            }

            override fun onProviderDisabled(provider: String) {

            }
        }
        lm!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100.2f,
            ll as LocationListener
        )
    }

    fun stopLocationUpdates(){
        if(lm!=null&&ll!=null)
        {
            lm!!.removeUpdates(ll!!)
        }
    }
    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    private fun sendDataToActivity2(p0: Location ){
        val sendLocation = Intent()
        sendLocation.action = "UPDATE_LOCATION+ADDRESS"
        sendLocation.putExtra("LOCATION_DATA", p0)
        sendBroadcast(sendLocation)
        Log.d("CZ2006:LocationService", "data sent to activity")
    }

    private fun sendDataToActivity(p0: Location ){
        val sendLocation = Intent()
        sendLocation.action = "UPDATE_LOCATION"
        sendLocation.putExtra("LOCATION_DATA", p0)
        sendBroadcast(sendLocation)
        Log.d("CZ2006:LocationService", "data sent to activity")
    }
}