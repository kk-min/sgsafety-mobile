package com.example.sg_safety_mobile
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns.EMAIL_ADDRESS
import android.view.LayoutInflater
import android.view.View;
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
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
        val v = inflater.inflate(R.layout.activity_forget_password, container, false)
        var resetbtn= v.findViewById(R.id.bt_forget) as Button

        resetbtn.setOnClickListener(View.OnClickListener {
            performForgetPassword(resetbtn)
        })

        return v
    }
    fun viewInitializations() {

        etEmail = findViewById(R.id.et_email)

        // To show back button in actionbar

    }
    private fun validateInput(): Boolean {

        if (etEmail.text.toString() == "") {
            etEmail.error = "Please Enter Email"
            return false
        }
        // checking the proper email format
        if (!isEmailValid(etEmail.text.toString())) {
            etEmail.error = "Please Enter Valid Email"
            return false
        }
        return true
    }

    private fun isEmailValid(email: String): Boolean {
        return EMAIL_ADDRESS.matcher(email).matches()
    }

    fun performForgetPassword (view: View) {

        if (validateInput()) {
            val email = etEmail.text.toString()
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@ForgetPasswordActivity,
                            "Check email to reset your password!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@ForgetPasswordActivity,
                            "Email is not registered!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

    }

}