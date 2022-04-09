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
import androidx.core.app.ServiceCompat.stopForeground
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.sg_safety_mobile.*
import com.example.sg_safety_mobile.Logic.*
import com.example.sg_safety_mobile.Presentation.Fragment.*

import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.fragment_home.*

//Activity class should only content UI and those func interact with user
class MainActivity : AppCompatActivity() {

    // Initialise the DrawerLayout, NavigationView and ToggleBar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private val mainManager :MainActivityManager = MainActivityManager(this)


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        MyFirebaseMessagingService.subscribeTopic(this,"HelpMessage")

        // Call findViewById on the DrawerLayout
        drawerLayout = findViewById(R.id.drawerLayout)

        // Pass the ActionBarToggle action into the drawerListener
        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(actionBarToggle)

        // Display the hamburger icon to launch the drawer
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        //supportActionBar?.setDisplayShowHomeEnabled(true);

        // Call syncState() on the action bar so it'll automatically change to the back button when the drawer layout is open
        actionBarToggle.syncState()
        if(savedInstanceState==null)
        {
            replaceFragment(HomeFragment(),"SG Safety")
        }
        mainManager.startLocationService()


        // Call findViewById on the NavigationView
        navView = findViewById(R.id.navView)

        // Call setNavigationItemSelectedListener on the NavigationView to detect when items are clicked
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                    replaceFragment(HomeFragment(),"SG Safety")
                    true
                }
                R.id.nav_aed -> {
                    Toast.makeText(this, "AED Location", Toast.LENGTH_SHORT).show()
                    replaceFragment(AEDFragment(),menuItem.title.toString())
                    true
                }
                R.id.nav_guide -> {
                    Toast.makeText(this, "Guide", Toast.LENGTH_SHORT).show()
                    replaceFragment(GuideFragment(),menuItem.title.toString())
                    true
                }
                R.id.nav_about -> {
                    Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
                    replaceFragment(AboutFragment(),menuItem.title.toString())
                    true
                }
                R.id.nav_manage_profile -> {
                    Toast.makeText(this, "Manage Profile", Toast.LENGTH_SHORT).show()
                    replaceFragment(ManageProfileFragment(),menuItem.title.toString())
                    true
                }
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

    // override the onBackPressed() function to close the Drawer when the back button is clicked
    override fun onBackPressed() {
        //val drawerLayout:DrawerLayout=findViewById(R.id.drawerLayout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    //function to replace frame layout with fragments
    private fun replaceFragment(fragment : Fragment, title : String){
        val fragmentManager =supportFragmentManager;
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()

        //close drawer
        drawerLayout.closeDrawers()

        //set title when clicked
        setTitle(title)
    }

    override fun onDestroy() {
        //stopService(mServiceIntent);
        val sharedPreference: SharedPreferences = getSharedPreferences("Login", Service.MODE_PRIVATE)
        if(sharedPreference.getBoolean("log in status",true)==false) {
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