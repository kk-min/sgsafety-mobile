package com.example.sg_safety_mobile.Presentation.Activity


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.sg_safety_mobile.R
import java.lang.Exception


class HelperChoiceActivity : AppCompatActivity() {

    private lateinit var cprbutton:Button
    private lateinit var aedbutton:Button
    private lateinit var lm: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helper_choice)
        viewEInitializations()

        cprbutton.setOnClickListener {
            try{
                getCurrentLocation()
                val intent = Intent(this, CPRMapActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            catch(e:Exception){
                Toast.makeText(this, "Please turn on your location!", Toast.LENGTH_SHORT).show()
            }

        }


        aedbutton.setOnClickListener {
            try{
                val intent = Intent(this, AEDMapActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            catch(e:Exception){
                Toast.makeText(this, "Please turn on your location!", Toast.LENGTH_SHORT).show()
            }

        }

    }
    private fun viewEInitializations() {
        cprbutton=findViewById(R.id.cpr)
        aedbutton=findViewById(R.id.aed)
    }
    fun getCurrentLocation(): Location {
        lateinit var loc: Location
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return loc
        }
        lm=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        loc= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
        return loc
    }
}