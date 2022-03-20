package com.example.sg_safety_mobile

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
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
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LocationService : Service() {
    var counter = 0
    lateinit var lm: LocationManager
    lateinit var loc: Location
    lateinit var ll: LocationListener
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
        loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!

        ll = object : LocationListener {
            override fun onLocationChanged(p0: Location) {
                Log.d("LocationService", "Location Changed!!!")
                val geopoint = GeoPoint(p0.latitude, p0.longitude)
                val db = Firebase.firestore
                val TAG = "firebase"
                val longlat = db.collection("Users").document("EA001nbepebIvfDsO9o3")
                longlat.update("Location", geopoint)
                    .addOnSuccessListener {
                        Log.d(
                            TAG,
                            "Location successfully updated to firebase !"
                        )
                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            TAG,
                            "Error updating location to firebase",
                            e
                        )
                    }
                Log.d("Location change", "${p0.latitude},${p0.longitude}")

                sendDataToActivity(p0)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

            }

            override fun onProviderEnabled(provider: String) {

            }

            override fun onProviderDisabled(provider: String) {

            }
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100.2f, ll)
    }

    fun stopLocationUpdates(){
        lm.removeUpdates(ll)
    }

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun sendDataToActivity(p0: Location ){
        val sendLocation = Intent()
        sendLocation.action = "UPDATE_LOCATION"
        sendLocation.putExtra("LOCATION_DATA", p0)
        sendBroadcast(sendLocation)
        Log.d("LocationService", "data sent to activity")
    }
}