package com.example.sg_safety_mobile

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class EditEmail : AppCompatActivity() {

    lateinit var currentPs: EditText
    lateinit var currentEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_email)
        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
        val db = Firebase.firestore
        var docid: String = sharedPreference.getString("UserID" , null).toString()
        db.collection("Users").get()
            .addOnCompleteListener{
                for(document in it.result!!) {
                    var id: String = document.id

                    if (id == docid) {
                        var email: String = document.data.getValue("email").toString()
                        var em_hint = findViewById<EditText>(R.id.current_email)

                        //display current email as the hint
                        em_hint.setHint(email)
                        return@addOnCompleteListener
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Cannot access to Firebase!", Toast.LENGTH_LONG).show() }



        supportActionBar?.title = "Edit Email";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        viewEInitializations()
    }

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.activity_edit_email, container, false)
        var updateEmail = v.findViewById(R.id.btn_updateEmail) as Button

        updateEmail.setOnClickListener(View.OnClickListener {
            performEmailReset(updateEmail)
        })

        return v
    }

    fun viewEInitializations() {
        currentPs = findViewById(R.id.cur_password)
        currentEmail = findViewById(R.id.current_email)
    }

    //retrieved password from the firebase will be a parameter of this function
    private fun validateEInput(dummy:String): Boolean {

        if (currentPs.text.toString() == "") {
            currentPs.error = "Please Enter Current Password"
            return false
        }

        //check if input password is same as current password
        if (currentPs.text.toString() != dummy) {
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


    fun performEmailReset(view: View) {

        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
        val db = Firebase.firestore
        var docid: String = sharedPreference.getString("UserID" , null).toString()
        var password = sharedPreference.getString("password" , null).toString()
        var present =0

        //new email value
        var new_email = currentEmail.text.toString()

        //check if the new email is updated
        if(new_email==""){
            currentEmail.error="Please enter a new Email"
            return
        }

        //check if the current password matches the input password
        if(validateEInput(password)){

            //check if email has been used
            db.collection("Users")
                .get()
                //when retrieval is successful
                .addOnCompleteListener {
                    for(document in it.result!!)
                    {
                        var emailInDoc:String =document.data.getValue("email").toString()

                        //if username is the same, do not allow update
                        if(emailInDoc==new_email){
                            currentEmail.error = "Email Already In Use"
                            Toast.makeText(this,"Email Reset Unsuccessful", Toast.LENGTH_SHORT).show()
                            present =1
                            break
                        }

                    }

                    if (present==0){

                        //update username
                        db.collection("Users").document(docid).update("email" , new_email)

                        Toast.makeText(this,"Email Reset Successfully", Toast.LENGTH_SHORT).show()
                    }

                }
                //when retrieval of collection is failed
                .addOnFailureListener {

                    Toast.makeText(this, "Cannot access to Firebase!", Toast.LENGTH_LONG).show() }

        }
        else{
            Toast.makeText(this, "Either Current Password/ Email Has Wrong Format", Toast.LENGTH_LONG).show()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}