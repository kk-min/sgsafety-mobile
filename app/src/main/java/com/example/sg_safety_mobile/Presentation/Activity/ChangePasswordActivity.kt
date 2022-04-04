package com.example.sg_safety_mobile.Presentation.Activity

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
import com.example.sg_safety_mobile.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ChangePasswordActivity : AppCompatActivity() {

    lateinit var etPassword:EditText
    lateinit var etRepeatPassword: EditText
    lateinit var etCurrentPassword: EditText

    val MIN_PASSWORD_LENGTH = 8;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        supportActionBar?.title = "Reset Password";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        viewInitializations()
    }


    fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.activity_change_password, container, false)
        var resetbtn= v.findViewById(R.id.bt_forget) as Button

        resetbtn.setOnClickListener(View.OnClickListener {
            performResetPassword(resetbtn)
        })

        return v
    }

    fun viewInitializations() {
        etPassword = findViewById(R.id.et_password)
        etRepeatPassword = findViewById(R.id.et_repeat_password)
        etCurrentPassword = findViewById(R.id.cur_password)
    }

    // Checking if the input in form is valid
    fun validateInput(dummy:String): Boolean {

        //if no current password entered
        if (etCurrentPassword.text.toString() == "") {
            etCurrentPassword.error = "Please Enter Current Password"
            return false
        }

        // Checking if current password is entered correctly - currently dummy
        if (etCurrentPassword.text.toString() != dummy) {
            etCurrentPassword.error = "Wrong Current Password"
            return false
        }

        //if no password entered
        if (etPassword.text.toString() == "") {
            etPassword.error = "Please Enter Password"
            return false
        }
        //if no repeat password entered
        if (etRepeatPassword.text.toString() == "") {
            etRepeatPassword.error = "Please Enter Repeat Password"
            return false
        }

        if(etCurrentPassword.text.toString()==etPassword.text.toString())
        {
            etPassword.error="Please Do Not Reuse An Old Password"
            return false
        }


        when {
            //check if contain at least 8 characters
            etPassword.text.length < 8 -> {
                etPassword.error = "Password Length must be more than " + MIN_PASSWORD_LENGTH + "characters"
                return false
            }

            //check if contain at least 1 Uopercase
            !etPassword.text.toString().matches(".*[A-Z].*".toRegex()) -> {
                etPassword.error = "Password Must Contain 1 Upper-case Character"
                return false
            }

            //check if contain at least 1 Lowercase
            !etPassword.text.toString().matches(".*[a-z].*".toRegex()) -> {
                etPassword.error = "Password Must Contain 1 Lower-case Character"
                return false
            }

            //check if contains special character
            !etPassword.text.toString().matches(".*[!@#$%^&*+=/?].*".toRegex()) -> {
                etPassword.error = "Password Must Contain 1 Symbol"
                return false
            }

            //check if contains digits
            !etPassword.text.toString().matches(".*[0-9].*".toRegex()) -> {
                etPassword.error = "Password Must Contain 1 Digit"
                return false
            }

            //check if both passwords match
            etPassword.text.toString() != etRepeatPassword.text.toString() -> {
                etRepeatPassword.error = "Passwords Don't Match"
                return false
            }
            else -> return true
        }


        return true
    }

    fun performResetPassword(view: View) {

        //either firestore
        val user = Firebase.auth.currentUser
        val db = Firebase.firestore
        val sharedPreference: SharedPreferences = getSharedPreferences("Login", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreference.edit()
        var docid: String = sharedPreference.getString("UserID", null).toString()
        var dummy = sharedPreference.getString("password", null).toString()
        val curremail: String = sharedPreference.getString("login_email", null).toString()

        val credential = EmailAuthProvider.getCredential(curremail, dummy)

        user?.reauthenticate(credential)?.addOnCompleteListener {
            if (validateInput(dummy)) {
                val newpassword = etPassword.text.toString()
                user?.updatePassword(newpassword)
                db.collection("Users").document(docid).update("password", newpassword)
                editor.putString("password", newpassword)
                editor.commit()
                Toast.makeText(this, "Password Reset Successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
