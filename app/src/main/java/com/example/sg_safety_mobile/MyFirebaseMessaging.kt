package com.example.sg_safety_mobile

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

import android.content.SharedPreferences
import android.location.Location

import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.GlobalScope

import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import kotlinx.coroutines.launch

import org.json.JSONObject
import java.io.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection

import kotlinx.coroutines.tasks.await
import org.osmdroid.util.GeoPoint


class MyFirebaseMessagingService:FirebaseMessagingService() {
    companion object {
        var token : String? = null

        //Firebase Cloud Messaging Key
        val key : String = "AAAA8SS5-jY:APA91bGsabxqNTp0aE71JbAEibWiUiA-HHSv344LTmxpPNa3fLNJyeS_tALDHmeaouIjPpe0jE0gxeBXsS5WP_xcWzfLL2Zwc8ZYnTXtVN-EMcsgtNtwhoMSTBNc801LCW_HtgPlGXZ3"


        fun subscribeTopic(context: Context, topic: String) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnSuccessListener {
                //Toast.makeText(context, "Subscribed $topic", Toast.LENGTH_LONG).show()
                Log.d("CZ2006:MyFirebaseMessaging:", "Subscribed to $topic")
            }.addOnFailureListener {
                //Toast.makeText(context, "Failed to Subscribe $topic", Toast.LENGTH_LONG).show()
                Log.d("CZ2006:MyFirebaseMessaging:", "Failed to Subscribe $topic")

            }
        }

        fun unsubscribeTopic(context: Context, topic: String) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).addOnSuccessListener {

                //Toast.makeText(context, "Unsubscribed $topic", Toast.LENGTH_LONG).show()
                Log.d("CZ2006:MyFirebaseMessaging:", "Unsubscribed to $topic")
            }.addOnFailureListener {
                //Toast.makeText(context, "Failed to Unsubscribe $topic", Toast.LENGTH_LONG).show()
                Log.d("CZ2006:MyFirebaseMessaging:", "Failed to Unsubscribe $topic")
            }
        }

        fun sendMessage(title: String, content: String,topic: String,id:String) {
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
    override fun onMessageReceived(p0: RemoteMessage) {
        //ADD CONSTRAINT BASED ON USER LOCATION AND VICTIM LOCATION AND CHECK WHETHER CURRENT USER IS THE VICTIM ITSELF
        super.onMessageReceived(p0)

        Log.e("CZ2006:onMessageReceived: ", p0.data.toString())


        val title = p0.data.get("title")
        val content = p0.data.get("content")
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val victim_id=p0.data.get("victim_id").toString()
        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
        val current_user_id= sharedPreference.getString("UserID","")

        //NOTIFICATION DISPLAY CHECK( (I)NOT CURRENT USER (II)DISTANCE<=400M)
        /*if(victim_id==current_user_id)
        {
            Log.e("CZ2006:Messaging: Current user is the one who sent out message", "Notification not display")
            return
        }
        else
        {
            val victimLocation:GeoPoint=getuserLocationViaID(victim_id)
            val userLocation:GeoPoint=getuserLocationViaID(current_user_id.toString())

            Log.e("CZ2006:Victim Location","${victimLocation}")
            Log.e("CZ2006:User Location","${userLocation}")
            Log.e("CZ2006:Distance between 2 user in km","${countDistanceBetweenTwoPoints(victimLocation,userLocation)}")
            if(countDistanceBetweenTwoPoints(victimLocation,userLocation)>=0.4)
            {
                return
            }

        }*/
        val intent = Intent(this,ChoicePage::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(applicationContext,0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            checkNotificationChannel("1")
        }

//        val person = Person.Builder().setName("test").build()
        val notification = NotificationCompat.Builder(applicationContext,"1")
            .setSmallIcon(R.drawable.sgsafety_icon)

            .setContentTitle(title)
            .setContentText(content)
//                .setStyle(NotificationCompat.MessagingStyle(person)
//                        .setGroupConversation(false)
//                        .addMessage(title,
//                                currentTimeMillis(), person)
//                )
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSound)


        val notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1,notification.build())

    }
    fun countDistanceBetweenTwoPoints(p0:GeoPoint,p1:GeoPoint):Double{
        val lat1=p0.latitude
        val lon1=p0.longitude
        val lat2=p1.latitude
        val lon2=p1.longitude
        val theta = lon1 - lon2
        var dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        dist = dist * 1.609344
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    fun getuserLocationViaID(id:String): GeoPoint {
        val db = Firebase.firestore
        lateinit var geoPoint: GeoPoint
        runBlocking {

            db.collection("Users").document(id).get()
                .addOnSuccessListener { document ->

                    val result = document.getGeoPoint("Location")
                    geoPoint = result?.let { GeoPoint(result.latitude, it.longitude) }!!
                }
                .addOnFailureListener { e ->
                    Log.e("CZ2006:VicTIM location not found", "Error getting document", e)

                }
                .await()


        }
        return geoPoint
    }

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

    override fun onNewToken(p0: String) {
        token = p0
        super.onNewToken(p0)
    }
}