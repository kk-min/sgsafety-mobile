package com.example.sg_safety_mobile


import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView



class MainActivity : AppCompatActivity() {

    // Initialise the DrawerLayout, NavigationView and ToggleBar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Call findViewById on the DrawerLayout
        drawerLayout = findViewById(R.id.drawerLayout)

        // Pass the ActionBarToggle action into the drawerListener
        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(actionBarToggle)

        // Display the hamburger icon to launch the drawer
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Call syncState() on the action bar so it'll automatically change to the back button when the drawer layout is open
        actionBarToggle.syncState()


        // Call findViewById on the NavigationView
        navView = findViewById(R.id.navView)

        // Call setNavigationItemSelectedListener on the NavigationView to detect when items are clicked
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                    replaceFragment(HomeFragment(),menuItem.title.toString())
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
                else -> {
                    false
                }
            }
        }
    }
    // override the onSupportNavigateUp() function to launch the Drawer when the hamburger icon is clicked
    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.openDrawer(navView)
        return true
    }

    // override the onBackPressed() function to close the Drawer when the back button is clicked
    override fun onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START)
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