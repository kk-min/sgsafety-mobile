package com.example.sg_safety_mobile.Logic

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.sg_safety_mobile.BuildConfig
import com.example.sg_safety_mobile.Presentation.Activity.ChoicePageActivity
import com.example.sg_safety_mobile.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import org.osmdroid.util.GeoPoint
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

/**
 *Topic-based messaging class from Firebase Messaging Service that is used for sending and receiving of
 * messages between user that has subscribed to the same topic
 *
 *
 * @since 2022-4-15
 *
 */
class MyFirebaseMessagingService:FirebaseMessagingService() {

    /**
     *In-built Location Manager
     */
    private lateinit var lm: LocationManager
    companion object {
        /**
         *Token of the Firebase Messaging
         */
        var token : String? = null

        /**
         *API key of Firebase Messaging Service
         */
        //Firebase Cloud Messaging Key
        private val key : String = BuildConfig.FIREBASE_KEY
        /**
         *Subscribe current user to a topic
         *
         * @param context application context
         * @param topic topic to be subscribed
         */
        fun subscribeTopic(context: Context, topic: String) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnSuccessListener {
                //Toast.makeText(context, "Subscribed $topic", Toast.LENGTH_LONG).show()
                Log.d("CZ2006:MyFirebaseMessaging:", "Subscribed to $topic")
            }.addOnFailureListener {
                //Toast.makeText(context, "Failed to Subscribe $topic", Toast.LENGTH_LONG).show()
                Log.d("CZ2006:MyFirebaseMessaging:", "Failed to Subscribe $topic")

            }
        }

        /**
         *Unsubscribe current user to a topic
         *
         * @param context application context
         * @param topic topic to be unsubscribed
         */
        fun unsubscribeTopic(context: Context, topic: String) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).addOnSuccessListener {
                //Toast.makeText(context, "Unsubscribed $topic", Toast.LENGTH_LONG).show()
                Log.d("CZ2006:MyFirebaseMessaging:", "Unsubscribed to $topic")
            }.addOnFailureListener {
                //Toast.makeText(context, "Failed to Unsubscribe $topic", Toast.LENGTH_LONG).show()
                Log.d("CZ2006:MyFirebaseMessaging:", "Failed to Unsubscribe $topic")
            }
        }

        /**
         *Send message to a current topic
         *
         * @param title title of the notification
         * @param content content of the notification
         * @param topic topic to be sent to
         * @param id userid
         * @param location location of current user
         */
        fun sendMessage(title: String, content: String,topic: String,id:String,location:GeoPoint) {
            GlobalScope.launch {
                val endpoint = "https://fcm.googleapis.com/fcm/send"
                try {
                    val url = URL(endpoint)
                    val httpsURLConnection: HttpsURLConnection = url.openConnection() as HttpsURLConnection
                    httpsURLConnection.readTimeout = 10000
                    httpsURLConnection.connectTimeout = 15000
                    httpsURLConnection.requestMethod = "POST"
                    httpsURLConnection.doInput = true
                    httpsURLConnection.doOutput = true

                    // Adding the necessary headers
                    httpsURLConnection.setRequestProperty("authorization", "key=$key")
                    httpsURLConnection.setRequestProperty("Content-Type", "application/json")

                    // Creating the JSON with post params
                    val body = JSONObject()

                    val data = JSONObject()
                    data.put("title", title)
                    data.put("content", content)
                    data.put("victim_id",id)
                    body.put("data",data)

                    data.put("victimLongitude",location.longitude)
                    data.put("victimLatitude",location.latitude)

                    body.put("to","/topics/$topic")

                    val outputStream: OutputStream = BufferedOutputStream(httpsURLConnection.outputStream)
                    val writer = BufferedWriter(OutputStreamWriter(outputStream, "utf-8"))
                    writer.write(body.toString())
                    writer.flush()
                    writer.close()
                    outputStream.close()
                    val responseCode: Int = httpsURLConnection.responseCode
                    val responseMessage: String = httpsURLConnection.responseMessage
                    Log.d("Response:", "$responseCode $responseMessage")
                    var result = String()
                    var inputStream: InputStream? = null
                    inputStream = if (responseCode in 400..499) {
                        httpsURLConnection.errorStream
                    } else {
                        httpsURLConnection.inputStream
                    }

                    if (responseCode == 200) {

                        Log.e("CZ2006:Success:", "notification sent $title \n $content")
                        // The details of the user can be obtained from the result variable in JSON format
                    } else {
                        Log.e("CZ2006:Error", "Error Response")

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    /**
     *Runs when messages are received
     *
     * @param p0 received message
     */
    override fun onMessageReceived(p0: RemoteMessage) {
        //ADD CONSTRAINT BASED ON USER LOCATION AND VICTIM LOCATION AND CHECK WHETHER CURRENT USER IS THE VICTIM ITSELF
        super.onMessageReceived(p0)

        Log.e("CZ2006:onMessageReceived: ", p0.data.toString())


        val title = p0.data["title"]
        val content = p0.data["content"]
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val victim_id= p0.data["victim_id"].toString()
        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
        val current_user_id= sharedPreference.getString("UserID","")

        val helpPreference:SharedPreferences=getSharedPreferences("VictimDetails", MODE_PRIVATE)

        var victimLatitude: Double =0.0
        var victimLongitude: Double = 0.0
        lateinit var victimLocation:GeoPoint



        //NOTIFICATION DISPLAY CHECK( (I)NOT CURRENT USER (II)DISTANCE<=400M)
        if(victim_id==current_user_id)
        {
            Log.e("CZ2006:Messaging: Current user is the one who sent out message", "Notification not display")
            return
        }
        else
        {
            victimLatitude= p0.data["victimLatitude"]?.toDouble()!!
            victimLongitude= p0.data["victimLongitude"]?.toDouble()!!
            if(victimLatitude!=null&& victimLongitude!=null)
            {
                victimLocation= GeoPoint(victimLatitude,victimLongitude)
            }
            val userLoc:Location=getCurrentLocation()
            val userLocation=GeoPoint(userLoc.latitude,userLoc.longitude)

            Log.e("CZ2006:Victim Location","$victimLocation")
            Log.e("CZ2006:User Location","$userLocation")
            Log.e("CZ2006:Distance between 2 user in km","${countDistanceBetweenTwoPoints(victimLocation,userLocation)}")
            if(countDistanceBetweenTwoPoints(victimLocation,userLocation)>=0.4)
            {
                return
            }

        }

        val editor: SharedPreferences.Editor = helpPreference.edit()
        editor.putString("UserID",current_user_id)
        Log.e("CZ2006:MyFirebaseMessaging:victim location check","${victimLatitude.toFloat()},${victimLongitude.toFloat()}")
        editor.putFloat("Victim_Longitude",victimLongitude.toFloat())
        editor.putFloat("Victim_Latitude",victimLatitude.toFloat())
        editor.commit()


        val intent = Intent(this, ChoicePageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(applicationContext,0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            checkNotificationChannel("1")
        }


        val notification = NotificationCompat.Builder(applicationContext,"1")
            .setSmallIcon(R.drawable.sgsafety_icon)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSound)


        val notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1,notification.build())

    }

    /**
     *Calculate the distance between two geopoint
     *
     * @param p0 Geopoint 1
     * @param p1 Geopoint 2
     *
     * @return  distance in KM
     */
    private fun countDistanceBetweenTwoPoints(p0:GeoPoint, p1:GeoPoint):Double{
        val lat1=p0.latitude
        val lon1=p0.longitude
        val lat2=p1.latitude
        val lon2=p1.longitude
        val theta = lon1 - lon2
        var dist = sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(deg2rad(theta))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        dist *= 1.609344
        return dist
    }

    /**
     *Change degree to radians
     *
     * @return radians converted
     */
    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    /**
     *Change radians to degree
     *
     * @return degree converted
     */
    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    /**
     *Get user current location
     *
     * @return return current location
     */
    private fun getCurrentLocation(): Location {
        lateinit var loc: Location
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
            return loc
        }
        lm=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        loc= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
        return loc
    }

    /**
     *Checking of certain notification channel
     *
     * @param CHANNEL_ID id of the channel to be checked
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkNotificationChannel(CHANNEL_ID:String) {
        val notificationChannel = NotificationChannel(CHANNEL_ID,
            getString(R.string.app_name),
            NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.description = "CHANNEL_DESCRIPTION"
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    /**
     *Update when there is a new token
     *
     * @param p0 token to be updated
     */
    override fun onNewToken(p0: String) {
        token = p0
        super.onNewToken(p0)
    }
}