package com.example.sg_safety_mobile



import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment

import com.google.android.material.navigation.NavigationView

//Activity class should only content UI and those func interact with user
class MainActivity : AppCompatActivity() {

    // Initialise the DrawerLayout, NavigationView and ToggleBar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView

    var locationServiceIntent: Intent? = null
    private var locationService: LocationService? = null
    lateinit var locationReceiver: LocationReceiver;

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
        Log.d("LocationService", "LocationService Starting...")
        locationService = LocationService()
        locationServiceIntent = Intent(this, locationService!!.javaClass)
        if (!isMyServiceRunning(locationService!!.javaClass)) {
            startService(locationServiceIntent)
        }




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
                    val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)

                    alertDialog.setTitle("Sign Out")
                    alertDialog.setMessage("Are You sure")
                    alertDialog.setPositiveButton(
                        "Yes"
                    ) { _, _ ->
                        Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show()
                        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
                        val editor: SharedPreferences.Editor=sharedPreference.edit()
                        editor.clear()
                        editor.commit()
                        MyFirebaseMessagingService.unsubscribeTopic(this,"HelpMessage")

                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent)


                    }
                    //cancel the alert button
                    alertDialog.setNegativeButton(
                        "No"
                    ) { _, _ -> }
                    val alert: AlertDialog = alertDialog.create()
                    alert.setCanceledOnTouchOutside(false)
                    alert.show()
                    true
                }


                else -> {

                    false
                }
            }
        }
    }
    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("Service status", "Running")
                return true
            }
        }
        Log.i("Service status", "Not running")
        return false
    }
    // override the onSupportNavigateUp() function to launch the Drawer when the hamburger icon is clicked
    override fun onSupportNavigateUp(): Boolean {
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
}