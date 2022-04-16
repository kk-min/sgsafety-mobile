package com.example.sg_safety_mobile.Presentation.Activity

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.sg_safety_mobile.Logic.LocationServiceRestarter
import com.example.sg_safety_mobile.Logic.MainActivityManager
import com.example.sg_safety_mobile.Logic.MyFirebaseMessagingService
import com.example.sg_safety_mobile.Presentation.Fragment.AboutFragment
import com.example.sg_safety_mobile.Presentation.Fragment.GuideFragment
import com.example.sg_safety_mobile.Presentation.Fragment.HomeFragment
import com.example.sg_safety_mobile.Presentation.Fragment.ManageProfileFragment
import com.example.sg_safety_mobile.R
import com.google.android.material.navigation.NavigationView
/**
 *Activity that runs the main page of this app after Login from [com.example.sg_safety_mobile.Presentation.Activity.LoginActivity]
 * or check to be logged in from [com.example.sg_safety_mobile.Presentation.Activity.CheckLoginStatusActivity]
 * This activity consists of 4 pages and a sign out option which can be accessed via navigation drawer
 *
 * 4 Fragment are:
 * Home Fragment[com.example.sg_safety_mobile.Presentation.Fragment.HomeFragment]
 * Guide Fragment[com.example.sg_safety_mobile.Presentation.Fragment.GuideFragment]
 * About Fragment[com.example.sg_safety_mobile.Presentation.Fragment.AboutFragment]
 * Manage Profile Fragment[com.example.sg_safety_mobile.Presentation.Fragment.ManageProfileFragment]
 *
 *
 * @since 2022-4-15
 */

class MainActivity : AppCompatActivity() {

    // Initialise the DrawerLayout, NavigationView and ToggleBar
    /**
     *Layout for navigation drawer
     */
    private lateinit var drawerLayout: DrawerLayout
    /**
     *UI toggle for the action bar
     */
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    /**
     *UI navigation view for the drawer
     */
    private lateinit var navView: NavigationView
    /**
     *Main Activity Manager
     */
    private val mainManager :MainActivityManager = MainActivityManager(this)

    /**
     *Runs when activity is created
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        //initialize current page
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewEInitializations()

        //subscribe current account to topic that victim will send help messege(even app closed will still receive message)
        MyFirebaseMessagingService.subscribeTopic(this,"HelpMessage")


        // Pass the ActionBarToggle action into the drawerListener
        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(actionBarToggle)

        // Display the hamburger icon to launch the drawer
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // Call syncState() on the action bar so it'll automatically change to the back button when the drawer layout is open
        actionBarToggle.syncState()
        if(savedInstanceState==null)
        {
            replaceFragment(HomeFragment(),"SG Safety")
        }

        mainManager.startLocationService()

        // Call setNavigationItemSelectedListener on the NavigationView to detect when items are clicked
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                //prompt to home page
                R.id.nav_home -> {
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                    replaceFragment(HomeFragment(),"SG Safety")
                    true
                }
                //prompt to guide page
                R.id.nav_guide -> {
                    Toast.makeText(this, "Guide", Toast.LENGTH_SHORT).show()
                    replaceFragment(GuideFragment(),menuItem.title.toString())
                    true
                }
                //prompt to about page
                R.id.nav_about -> {
                    Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
                    replaceFragment(AboutFragment(),menuItem.title.toString())
                    true
                }
                //prompt to manage profile page
                R.id.nav_manage_profile -> {
                    Toast.makeText(this, "Manage Profile", Toast.LENGTH_SHORT).show()
                    replaceFragment(ManageProfileFragment(),menuItem.title.toString())
                    true
                }
                //prompt to sign out and back to login page
                R.id.nav_sign_out -> {
                    mainManager.promptSignOutAlert()
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    /**
     *Initialize all the UI views
     */
    private fun viewEInitializations() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
    }

    /**
     *Close the drawer when the back button on navigation drawer is pressed
     */
    // override the onSupportNavigateUp() function to launch the Drawer when the hamburger icon is clicked
    override fun onSupportNavigateUp(): Boolean {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            actionBarToggle.syncState()
            return true
        }
        drawerLayout.openDrawer(navView)
        return true
    }

    /**
     *Close the drawer when the back button is pressed
     */
    // override the onBackPressed() function to close the Drawer when the back button is clicked
    override fun onBackPressed() {
        //val drawerLayout:DrawerLayout=findViewById(R.id.drawerLayout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /**
     *Replace the fragment of current activity
     *
     * @param fragment fragment to be replaced to
     * @param title title of the fragment
     */
    //function to replace frame layout with fragments
    private fun replaceFragment(fragment : Fragment, title : String){
        val fragmentManager =supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()

        //close drawer
        drawerLayout.closeDrawers()

        //set title when clicked
        setTitle(title)
    }

    /**
     *Runs when the app is destroyed
     */
    override fun onDestroy() {
        //stopService(mServiceIntent);
        val sharedPreference: SharedPreferences = getSharedPreferences("Login", Service.MODE_PRIVATE)
        if(!sharedPreference.getBoolean("log in status",true)) {
            super.onDestroy()
            return
        }
        Log.e("CZ2006:Destroy In MainActivity", "Restarting....." )
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, LocationServiceRestarter::class.java)
        this.sendBroadcast(broadcastIntent)
        super.onDestroy()
    }

}