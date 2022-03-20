package com.example.sg_safety_mobile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.sg_safety_mobile.databinding.ActivityUpdateCprBinding
//import com.example.sg_safety_mobile.databinding.ActivityUpdateCprBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
//import com.google.firebase.storage.ktx.storage
import java.util.*
import kotlin.math.exp


class UpdateCPR : AppCompatActivity() {


    lateinit var imageUri : Uri
    private lateinit var upload: TextView
    lateinit var binding : ActivityUpdateCprBinding
    lateinit var expiry : String
    lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateCprBinding.inflate(layoutInflater)
        setContentView(binding.root)



        supportActionBar?.title = "Update CPR";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        val datePicker = findViewById<DatePicker>(R.id.date_Picker)


        //date picket to select the date that that certificate expires in
        val today = Calendar.getInstance()
        datePicker.init(today.get(Calendar.YEAR) , today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)){
                view, year, month, day ->
            val month = month +1
            expiry="$day/$month/$year"
        }


        binding.uploadCpr.setOnClickListener {
            startFileChooser()
        }

        binding.submitting.setOnClickListener {
            uploadingFile()
        }

    }


    private fun startFileChooser() {
        val pdfIntent = Intent()
        pdfIntent.type = "image/*"
        pdfIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(pdfIntent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == RESULT_OK){
            imageUri = data?.data!!
            //display selected file
            var iv : ImageView = findViewById(R.id.image_view)
            iv.setImageURI(imageUri)


        }
    }

    private fun uploadingFile(){
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading file")
        progressDialog.setCancelable(false)
        progressDialog.show()

        if(imageUri==null){
            Toast.makeText(this, "Please upload a document" , Toast.LENGTH_SHORT).show()
        }

        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
        val fileName = sharedPreference.getString("UserID" , null).toString()
        val storeRef = FirebaseStorage.getInstance().getReference("files/$fileName")

        //adding file into Storage, with file name as Document Id
        storeRef.putFile(imageUri)
            .addOnCompleteListener {


                var link = storeRef.path.toString()
                modifyDB(link)

                Toast.makeText(this , "Successful upload" , Toast.LENGTH_LONG).show()
                if(progressDialog.isShowing) progressDialog.dismiss()

            }

            .addOnFailureListener{
                if (progressDialog.isShowing) progressDialog.dismiss()

                Toast.makeText(this, "Unsuccessful upload" , Toast.LENGTH_SHORT).show()
            }
    }

    fun modifyDB(link:String) {
        val db = Firebase.firestore
        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
        val docid: String = sharedPreference.getString("UserID" , null).toString()

        //update user's database with relevant file uri and cpr expiry
        db.collection("Users").document(docid).update("file" , link)
        db.collection("Users").document(docid).update("cprExpiry" , expiry)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }



}