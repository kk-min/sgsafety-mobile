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
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class EditEmailActivity : AppCompatActivity() {

    lateinit var currentPs: EditText
    lateinit var currentEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_email)
        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)

        val curr = sharedPreference.getString("login_email" , null).toString()
        val em_hint = findViewById<EditText>(R.id.current_email)
        em_hint.hint = curr


      /*  val db = Firebase.firestore
        var docid: String = sharedPreference.getString("UserID" , null).toString()
        db.collection("Users").get()
            .addOnCompleteListener{
                for(document in it.result!!) {
                    var id: String = document.id

                    if (id == docid) {
                        var email: String = document.data.getValue("email").toString()
                        var em_hint = findViewById<EditText>(R.id.current_email)

                        //display current email as the hint
                        em_hint.hint = email
                        return@addOnCompleteListener
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Cannot access to Firebase!", Toast.LENGTH_LONG).show() }*/



        supportActionBar?.title = "Edit Email"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

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

    private fun viewEInitializations() {
        currentPs = findViewById(R.id.cur_password)
        currentEmail = findViewById(R.id.current_email)
    }

    //retrieved password from the firebase will be a parameter of this function
    private fun validateEInput(currentpassword:String): Boolean {

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


    fun performEmailReset(view: View) {

        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
        val db = Firebase.firestore
        val password = sharedPreference.getString("password" , null).toString()
        var present =0

        //new email value
        val newEmail = currentEmail.text.toString()


        //check if the current password matches the input password
        if(validateEInput(password)){

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
                            Toast.makeText(this,"Email Reset Unsuccessful", Toast.LENGTH_SHORT).show()
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

                    Toast.makeText(this, "Cannot access to Firebase!", Toast.LENGTH_LONG).show() }

        }
        else{
            Toast.makeText(this, "Either Current Password/ Email Has Wrong Format", Toast.LENGTH_LONG).show()
        }
    }

    private fun modifyEmail(newemail : String){

        //getting the current user
        val user = Firebase.auth.currentUser!!
        val db = Firebase.firestore
        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
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
                            Toast.makeText(this,"Email Reset Successfully", Toast.LENGTH_SHORT).show()

                        }
                        else{
                            //fails to update in firestore
                            Toast.makeText(this,"Firestore update Unsuccessful", Toast.LENGTH_SHORT).show()
                        }

                    }
                    .addOnFailureListener { Toast.makeText(this, "Failed Update" , Toast.LENGTH_SHORT).show() }
            }
            .addOnFailureListener {Toast.makeText(this, "Failed Reauthentication" , Toast.LENGTH_SHORT).show()  }


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}