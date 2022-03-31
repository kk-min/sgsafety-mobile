package com.example.sg_safety_mobile

//import com.example.sg_safety_mobile.databinding.ActivityUpdateCprBinding
//import com.google.firebase.storage.ktx.storage

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sg_safety_mobile.databinding.ActivityUpdateCprBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class UpdateCPR : AppCompatActivity() {


    lateinit var imageUri : Uri
    lateinit var binding : ActivityUpdateCprBinding
    lateinit var expiry : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateCprBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.title = "Update CPR"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //initialise Uri and expiry date
        imageUri = Uri.EMPTY
        expiry=""

        val datePicker = findViewById<DatePicker>(R.id.date_Picker)
        val today = Calendar.getInstance()

        //date picket to select the date that that certificate expires in
        datePicker.init(today.get(Calendar.YEAR) , today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)){
                view, year, month, day ->
            val month: Int = month +1
            expiry="$day/$month/$year"
        }


        //selecting "Upload Image version of CPR Certificate" button
        binding.uploadCpr.setOnClickListener {
            startImageFileChooser()
        }

        binding.uploadPdfCpr.setOnClickListener {
            startPdfFileChooser()
        }



        //Selecting "Submit" button
        binding.submitting.setOnClickListener {
            if(inPast(datePicker)){
                Toast.makeText(this , "Please Select Valid Expiry Date" , Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            uploadingFile()
        }

    }


    //check if expiry date selected by user is in the past/current date
    private fun inPast(date: DatePicker) : Boolean{

        //initialise today's date
        val currentDate = Calendar.getInstance()
        currentDate.set(currentDate.get(Calendar.YEAR) , currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH))

        //initialise datePicker value
        val selectedDate = Calendar.getInstance()
        selectedDate.set(date.year , date.month , date.dayOfMonth)

        //check if same date
        if(selectedDate.equals(currentDate)){
            return true
        }
        //check if selected date is in the past
        if(selectedDate.before(currentDate)){
            return true
        }

        return false
    }


    private fun startImageFileChooser() {
        val pdfIntent = Intent()
        pdfIntent.type = "image/*"
        pdfIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(pdfIntent, 100)
    }

    private fun startPdfFileChooser() {
        val pdfIntent = Intent()
        pdfIntent.type = "application/pdf"
        pdfIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(pdfIntent , 200)
    }


    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //upload an image
        if(requestCode == 100 && resultCode == RESULT_OK){
            imageUri = data?.data!!
            //display selected file
            val pdfTextView : TextView = findViewById(R.id.pdfText)
            pdfTextView.text = null

            val iv : ImageView = findViewById(R.id.image_view)
            iv.setImageURI(imageUri)

        }

        //upload a pdf
        if(requestCode == 200 && resultCode == RESULT_OK){
            imageUri=data?.data!!

            var cursor: Cursor? = null
            try {
                cursor = this.contentResolver.query(imageUri, null , null, null ,null)
                if (cursor != null && cursor.moveToFirst()) {

                    //remove previously selected image
                    val iv : ImageView = findViewById(R.id.image_view)
                    iv.setImageURI(null)

                    //display pdf name
                    var displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    val pdfTextView : TextView = findViewById(R.id.pdfText)
                    pdfTextView.text = displayName

                }
            } finally {
                cursor?.close()
            }

        }
    }


    private fun uploadingFile(){

        //no image has been selected for submission
        if(imageUri.toString() == ""){
            Toast.makeText(this, "Please Upload A Document" , Toast.LENGTH_SHORT).show()
            return
        }

        //datepicker has not been modified by the user
        if(expiry==""){
            Toast.makeText(this , "Please Select An Expiry Date" , Toast.LENGTH_LONG).show()
            return
        }


        //display progress bar when uploading to database
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading file")
        progressDialog.setCancelable(false)
        progressDialog.show()


        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
        val fileName = sharedPreference.getString("UserID" , null).toString()
        val storeRef = FirebaseStorage.getInstance().getReference("files/$fileName")

        //adding file into Storage, with file name as Document Id
        storeRef.putFile(imageUri)
            .addOnCompleteListener {

                modifyDB(storeRef.path.toString())
                Toast.makeText(this , "Successful upload" , Toast.LENGTH_LONG).show()
                if(progressDialog.isShowing) progressDialog.dismiss()

            }

            .addOnFailureListener{
                if (progressDialog.isShowing) progressDialog.dismiss()

                Toast.makeText(this, "Unsuccessful upload" , Toast.LENGTH_SHORT).show()
            }
    }

    //update relevant information in the database
    private fun modifyDB(link:String) {
        val db = Firebase.firestore
        val sharedPreference: SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
        val docid: String = sharedPreference.getString("UserID" , null).toString()

        //update user's database with relevant file uri and cpr expiry
        db.collection("Users").document(docid).update("file" , link)
        db.collection("Users").document(docid).update("cprExpiry" , expiry)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }



}