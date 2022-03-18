package com.example.sg_safety_mobile

import android.content.Intent
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
import java.util.*
import kotlin.concurrent.schedule


class ChangeUsername : AppCompatActivity() {

    lateinit var newUname: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_username)

        supportActionBar?.title = "Edit Username";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        uVisualisation()
    }

    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.activity_change_username, container, false)
        var resetbtn= v.findViewById(R.id.btn_submitUsername) as Button

        resetbtn.setOnClickListener(View.OnClickListener {
            performUsernameReset(resetbtn)
        })

        return v
    }

    fun uVisualisation(){
        newUname = findViewById(R.id.plain_text_input)
    }
/*
    fun validateUInput() : Boolean{

        var present: Boolean = false
        var name: String = newUname.text.toString()

        //check if text field is empty
        if(newUname.text.toString()==""){
            newUname.error = "Please Enter New Username"
            return false
        }

        //check in firebase that username has not been used before
        val db = Firebase.firestore
        db.collection("Users").whereEqualTo("username" , name)
            .get()
            //when retrieval is successful
            .addOnCompleteListener {
                //check if the username is in the document and and set the present to 1
                newUname.error = "Username Already In Use"

            }
            //when retrieval of collection is failed
            .addOnFailureListener {Toast.makeText(this, "Cannot access to Firebase!", Toast.LENGTH_LONG).show() }

        return !present

    }*/

    fun performUsernameReset(view: View) {


        var name: String = newUname.text.toString()
        var present =0

        //check if text field is empty

        if(name==""){
            newUname.error = "Please Enter New Username"
        }

        //check in firebase that username has not been used before
        val db = Firebase.firestore
        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
        val editor:SharedPreferences.Editor=sharedPreference.edit()
        db.collection("Users")
            .get()
            //when retrieval is successful
            .addOnCompleteListener {
                for(document in it.result!!)
                {
                    var usernameInDoc:String =document.data.getValue("username").toString()

                    //if username is the same, do not allow update
                    if(usernameInDoc==name){
                        newUname.error = "Username Already In Use"
                        Toast.makeText(this,"Username Reset Unsuccessful", Toast.LENGTH_SHORT).show()
                        present =1
                        break
                    }

                }

                if (present==0){
                    //figure out to find the document of the current user and then update it
                    //FIX THIS!!!
                    editor.remove("username")
                    editor.putString("username",name)
                    editor.commit()
                    Toast.makeText(this,"Username Reset Successfully", Toast.LENGTH_SHORT).show()
                }

            }
            //when retrieval of collection is failed
            .addOnFailureListener {
                // Here you can call you API
                //store the user info and update login status to shared preference
                Toast.makeText(this, "Cannot access to Firebase!", Toast.LENGTH_LONG).show() }

        /*
        var valid:Boolean  = false
        valid = validateUInput()

        if (valid) {
            // Input is valid, here send data to your server
            Toast.makeText(this,"Username Reset Unsuccessful", Toast.LENGTH_SHORT).show()
            val uName = newUname.text.toString()
            // Here you can call you API
            //store the user info and update login status to shared preference
            Toast.makeText(this,"Username Reset Successfully", Toast.LENGTH_SHORT).show()
        }

        else{
            Toast.makeText(this,"Username Reset Unsuccessful", Toast.LENGTH_SHORT).show()
        }*/
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}