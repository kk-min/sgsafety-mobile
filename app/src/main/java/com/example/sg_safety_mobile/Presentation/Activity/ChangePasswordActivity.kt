package com.example.sg_safety_mobile.Presentation.Activity


import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.sg_safety_mobile.Logic.FirebaseManager
import com.example.sg_safety_mobile.R

/**
 *Activity to let user change their password and it is accessed
 * via Manage Profile Fragment[com.example.sg_safety_mobile.Presentation.Fragment.ManageProfileFragment]
 *
 * @since 2022-4-15
 */
class ChangePasswordActivity : AppCompatActivity() {

    /**
     *UI edit text to let user key in new password
     */
    private lateinit var etPassword:EditText
    /**
     *UI edit text to let user key in repeated new password
     */
    private lateinit var etRepeatPassword: EditText
    /**
     *UI edit text to let user key in repeated old password
     */
    private lateinit var etCurrentPassword: EditText
    /**
     *UI button to be pressed to update user password
     */
    private lateinit var resetbtn:Button
    /**
     *Firebase Manager class
     */
    private val firebaseManager=FirebaseManager(this)

    /**
     *Runs when activity is created
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        //initialzie curretn page
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        viewInitializations()

        //setup the support bar and setup back button
        supportActionBar?.title = "Reset Password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        //Button-----------------------------------------------------------------------------------------------
        //click to change current password
        resetbtn.setOnClickListener{
            firebaseManager.performResetPassword(etPassword,etRepeatPassword,etCurrentPassword)
        }
    }

    /**
     *Initialize all the UI views
     */
    private fun viewInitializations() {
        etPassword = findViewById(R.id.et_password)
        etRepeatPassword = findViewById(R.id.et_repeat_password)
        etCurrentPassword = findViewById(R.id.cur_password)
        resetbtn= findViewById(R.id.bt_forget)
    }

    /**
     *Do something when a certain item is selected
     *
     * @param item  menu item to be selected
     *
     * @return true when item selected false otherwise
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
