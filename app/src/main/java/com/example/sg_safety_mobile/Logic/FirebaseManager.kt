package com.example.sg_safety_mobile.Logic

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.CountDownTimer
import android.telephony.SmsManager
import android.util.Log
import android.util.Patterns.EMAIL_ADDRESS
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import com.example.sg_safety_mobile.Presentation.Activity.ContactNumberActivity
import com.example.sg_safety_mobile.Presentation.Activity.ForgetPasswordActivity
import com.example.sg_safety_mobile.Presentation.Activity.MainActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import java.util.*

class FirebaseManager(val context: Context){
    private val db = Firebase.firestore
    private lateinit var givenOTP: String
    fun updateUserLocationToDatabase(geoPoint: com.google.firebase.firestore.GeoPoint){
        val sharedPreference: SharedPreferences = context.getSharedPreferences("Login", Service.MODE_PRIVATE)
        val current_user_id= sharedPreference.getString("UserID","").toString()
        val longlat = db.collection("Users").document(current_user_id)


        longlat.update("Location", geoPoint)
            .addOnSuccessListener {
                Log.d("CZ2006:LocationService", "Location successfully updated to firebase !")
            }
            .addOnFailureListener { e ->
                Log.w("CZ2006:LocationService", "Error updating location to firebase", e)
            }
        Log.d("CZ2006:LocationService", "Location changed: ${geoPoint.latitude},${geoPoint.longitude}")
    }

    fun performEmailReset(view: View, currentEmail: EditText, currentPs: EditText) {
        Log.d("UPDATE EMAIL", "Inside performEmailReset Function")
        val sharedPreference: SharedPreferences = context.getSharedPreferences("Login",
            AppCompatActivity.MODE_PRIVATE
        )
        val password = sharedPreference.getString("password" , null).toString()
        var present =0

        //new email value
        val newEmail = currentEmail.text.toString()


        //check if the current password matches the input password
        if(validateEInput(password, currentPs, currentEmail)){

            //check if email has been used
            db.collection("Users")
                .get()
                //when retrieval is successful
                .addOnCompleteListener {
                    for(document in it.result!!)
                    {
                        val emailInDoc:String =document.data.getValue("email").toString()

                        //if email is the same, do not allow update
                        if(emailInDoc==newEmail){
                            currentEmail.error = "Email Already In Use"
                            Toast.makeText(context,"Email Reset Unsuccessful", Toast.LENGTH_SHORT).show()
                            present =1
                            break
                        }
                    }

                    //email has not been used, enable update
                    if (present==0){
                        modifyEmail(newEmail)
                    }

                }
                //when retrieval of collection is failed
                .addOnFailureListener {

                    Toast.makeText(context, "Cannot access to Firebase!", Toast.LENGTH_LONG).show() }

        }
        else{
            Toast.makeText(context, "Either Current Password/ Email Has Wrong Format", Toast.LENGTH_LONG).show()
        }
    }

    private fun validateEInput(currentpassword:String, currentPs: EditText, currentEmail: EditText): Boolean {

        if (currentPs.text.toString() == "") {
            currentPs.error = "Please Enter Current Password"
            return false
        }

        //check if input password is same as current password
        if (currentPs.text.toString() != currentpassword) {
            currentPs.error = "Wrong Current Password"
            return false
        }

        if (currentEmail.text.toString() == "") {
            currentEmail.error = "Please Enter New Email"
            return false
        }

        //Email format check
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentEmail.text.toString()).matches()) {
            currentEmail.error = "Email Format Error"
            return false
        }

        return true
    }

    private fun modifyEmail(newemail : String){

        //getting the current user
        val user = Firebase.auth.currentUser!!
        val sharedPreference: SharedPreferences = context.getSharedPreferences("Login",
            AppCompatActivity.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor = sharedPreference.edit()
        val docid: String = sharedPreference.getString("UserID" , null).toString()
        val curremail: String = sharedPreference.getString("login_email" , null).toString()
        val password: String = sharedPreference.getString("password" , null).toString()


        val credential = EmailAuthProvider.getCredential(curremail, password)

        //reauthentication needed if user has been logged in for a long time
        user.reauthenticate(credential)
            .addOnCompleteListener {

                //attempt to update email in auth after successful reauthentication
                user.updateEmail(newemail)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //update email in the firebase
                            db.collection("Users").document(docid).update("email" , newemail)
                            editor.putString("login_email",newemail)
                            editor.apply()
                            Toast.makeText(context,"Email Reset Successfully", Toast.LENGTH_SHORT).show()

                        }
                        else{
                            //fails to update in firestore
                            Toast.makeText(context,"Firestore update Unsuccessful", Toast.LENGTH_SHORT).show()
                        }

                    }
                    .addOnFailureListener { Toast.makeText(context, "Failed Update" , Toast.LENGTH_SHORT).show() }
            }
            .addOnFailureListener {Toast.makeText(context, "Failed Reauthentication" , Toast.LENGTH_SHORT).show()  }


    }
    fun sendCode(view:View, contactNum: EditText, smsPermission: Boolean) {
        var invalid=0
        //check if contact is Empty
        if(contactNum.text.toString() ==""){
            contactNum.error = "Please Enter Contact Number"
            return
        }

        //check for format of the input number
        /*if(!contactNum.text.toString().matches("^(9|8)\\d{3}[- .]?\\d{4}$".toRegex())){
            contactNum.error = "Wrong Contact Number Format"
            return
        }*/


        //check if contact number has been used
        db.collection("Users")
            .get()
            //when retrieval is successful
            .addOnCompleteListener {
                for(document in it.result!!) {

                    //if registered users do not have a registered contact number
                    if(!document.data.containsKey("contact")){
                        continue
                    }

                    val contactInDoc: String = document.data.getValue("contact").toString()

                    //if contact is the same, do not allow update
                    if (contactInDoc == contactNum.text.toString()) {
                        contactNum.error = "Phone Number Already In Use"
                        Toast.makeText(context, "Contact Number Reset Unsuccessful", Toast.LENGTH_SHORT).show()
                        invalid = 1
                        break
                    }
                }

                if(invalid==0) {
                    val random = Random()
                    val generatedPassword = java.lang.String.format("%06d", random.nextInt(1000000))
                    givenOTP = generatedPassword.toString()
                    val num = contactNum.text.toString()
                    try{
                        if(smsPermission){
                            val smsManager: SmsManager = SmsManager.getDefault()
                            val msg= "Your SMS OTP is: $givenOTP . Use it within 2 minutes to access the SG Safety app"
                            smsManager.sendTextMessage(num , null, msg, null, null)

                            //timer for 2 minutes. Each OTP is valid for 2 minutes only
                            val time = object : CountDownTimer(120000, 1000) {

                                override fun onTick(millisUntilFinished: Long) {
                                    var dur = millisUntilFinished/1000
                                }

                                override fun onFinish() {
                                    Toast.makeText(context , "OTP Expired" , Toast.LENGTH_SHORT).show()
                                    givenOTP=""
                                    Log.d("SMS", "OTP EXPIRED")
                                }
                            }.start()

                            //Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show()
                            Log.d("SMS", "OTP SENT TO PHONE")
                        }
                    } catch (e : Exception) {
                        Toast.makeText(context, "Requesting for permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            //when retrieval of collection is failed
            .addOnFailureListener {
                Toast.makeText(context, "Cannot access to Firebase!", Toast.LENGTH_LONG).show() }
    }

    fun verifyCode(view: View, contactNum: EditText, inputOTP: EditText) {
        val sharedPreference: SharedPreferences = context.getSharedPreferences("Login",
            AppCompatActivity.MODE_PRIVATE
        )
        var docid: String = sharedPreference.getString("UserID" , null).toString()

        if(contactNum.text.toString()==""){
            contactNum.error="Please Enter A New Contact Number"
            return
        }

        if(inputOTP.text.toString()==""){
            inputOTP.error="Please Enter OTP"
            return
        }

        if(givenOTP==""){
            inputOTP.error="OTP Expired. Generate another OTP"
            return
        }

        if(inputOTP.text.toString()!=givenOTP){
            inputOTP.error="Wrong OTP"
            return
        }
        else{
            db.collection("Users").document(docid).update("contact" , contactNum.text.toString())
            Toast.makeText(context,"Contact Number Reset Successfully", Toast.LENGTH_SHORT).show()
        }
    }

    fun validateAcc(userName: String, passWord: String, editor: SharedPreferences.Editor) {
        if(userName=="" || passWord == ""){
            Toast.makeText(context, "Enter email/password" , Toast.LENGTH_SHORT).show()
            return
        }

        Firebase.auth.signInWithEmailAndPassword(userName, passWord)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    val user = Firebase.auth.currentUser
                    editor.putString("login_email",userName)
                    editor.putString("password",passWord)
                    editor.putBoolean("log in status",true)
                    editor.commit()

                    //retrieving and storing document id to sharedpreference
                    db.collection("Users")
                        .get()
                        .addOnCompleteListener {
                            //check all the documents in the collection
                            for(document in it.result!!)
                            {
                                val emailInDoc:String=document.data.getValue("email").toString()
                                if(emailInDoc==userName)
                                {
                                    editor.putString("UserID" , document.id)
                                    editor.commit()
                                    break
                                }
                            }
                        }
                        .addOnFailureListener {  }

                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(context, intent, null)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(context, "Invalid Email/Password", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateEmailInput(etEmail: EditText): Boolean {

        if (etEmail.text.toString() == "") {
            etEmail.error = "Please Enter Email"
            return false
        }
        // checking the proper email format
        var email = etEmail.text.toString()
        if (!EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Please Enter Valid Email"
            return false
        }
        return true
    }

    fun performForgetPassword (view: View, etEmail: EditText) {
        Log.d("ForgetPassword", "Inside performForgetPassword function")
        if (validateEmailInput(etEmail)) {
            val email = etEmail.text.toString()
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("ForgetPassword", "Email sent")
                        Toast.makeText(
                            context,
                            "Check email to reset your password!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Email is not registered!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
    fun uploadFileToDB(link:String, expiry: String) {
        val sharedPreference: SharedPreferences = context.getSharedPreferences("Login",
            AppCompatActivity.MODE_PRIVATE
        )
        val docid: String = sharedPreference.getString("UserID" , null).toString()

        //update user's database with relevant file uri and cpr expiry
        db.collection("Users").document(docid).update("file" , link)
        db.collection("Users").document(docid).update("cprExpiry" , expiry)
    }
}