package com.example.sg_safety_mobile

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide();

        //Shared Preference is used to keep user login
        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
        val editor:SharedPreferences.Editor=sharedPreference.edit()
        //get the login status from the shared preference ,default value is false if did not get ant value from it
        /*if(sharedPreference.getBoolean("log in status",false))
        {
            Toast.makeText(this, "Welcome Back "+sharedPreference.getString("username",""), Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }*/


        //Initialize button and textbox
        val loginButton: Button = findViewById(R.id.login_button)
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)


        val remember_password = findViewById<RadioButton>(R.id.remember_password)
        remember_password.setOnClickListener(View.OnClickListener {
            if (!(remember_password.isSelected)) {
                remember_password.isChecked = true
                remember_password.isSelected = true
            } else {
                remember_password.isChecked = false
                remember_password.isSelected = false
            }
        })

        val forgetpassword=findViewById<TextView>(R.id.forgot_password)
        forgetpassword.setOnClickListener{
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        val registerAcc=findViewById<TextView>(R.id.register)
        registerAcc.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sg-safety.web.app/register"))
            startActivity(browserIntent)
        }
        //validate account info input after login button is pressed
        loginButton.setOnClickListener{
            validateAccount(username.getText().toString(),password.getText().toString(),editor)
        }

    }

    //validate account by taking out username and password from FireStore Database and compare with current input
    private fun validateAccount(userName:String, passWord:String,editor: SharedPreferences.Editor)
    {
        //checkUser is for validation of user
        var checkUser:Int=0
        //checkPwd is for validation of password if user is found
        var checkPwd:Int=0

        //Get all the document from the "Users" collection from the FireStore Database
        val db = Firebase.firestore
        db.collection("Users")
            .get()
            //when retrieval is success
            .addOnCompleteListener {
                //check all the document in the collection
                for(document in it.result!!)
                {
                    val usernameInDoc:String=document.data.getValue("username").toString()
                    //if username is the same, continue with validation of password
                    if(usernameInDoc==userName)
                    {
                        checkUser=1
                        val pwdInDoc=document.data.getValue("password").toString()
                        //if password is the same, then store current info into the shared preference so that next time
                        //will be kept logged in
                        if(pwdInDoc==passWord)
                        {
                            checkPwd=1
                            //store the user info and update login status to shared preference
                            editor.putString("username",userName)
                            editor.putString("password",passWord)
                            editor.putBoolean("log in status",true)

                            editor.putString("UserID",document.id)

                            //val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
                            //val docID=sharedPreference.getString("UserID","")

                            editor.commit()


                            //Go to the main page of this application
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }

                //To remind user what is wrong with the input
                if(checkUser!=1)
                {
                    Toast.makeText(this, "Invalid UserName or Password!", Toast.LENGTH_LONG).show()
                }
                else if(checkPwd!=1)
                {
                    Toast.makeText(this, "Invalid Password!", Toast.LENGTH_LONG).show()
                }
            }
            //when retrieval of collection is failed
            .addOnFailureListener {Toast.makeText(this, "Cannot access to Firebase!", Toast.LENGTH_LONG).show() }
    }

}

