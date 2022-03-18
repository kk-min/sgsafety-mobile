package com.example.sg_safety_mobile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
//import com.example.sg_safety_mobile.databinding.ActivityUpdateCprBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
//import com.google.firebase.storage.ktx.storage
import java.util.*


class UpdateCPR : AppCompatActivity() {


    lateinit var pdfUri : Uri
    private lateinit var upload: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_cpr)
        //var storage = Firebase.storage
        //var storageRef = storage.reference

        supportActionBar?.title = "Update CPR";
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        val datePicker = findViewById<DatePicker>(R.id.date_Picker)
        var upload = findViewById<Button>(R.id.upload_cpr)


        //date picket to select the date that that certificate expires in
        val today = Calendar.getInstance()
        datePicker.init(today.get(Calendar.YEAR) , today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)){
                view, year, month, day ->
            val month = month +1

            val msg = "You Selected: $day/$month/$year"
            Toast.makeText(this , msg , Toast.LENGTH_SHORT).show()

        }






        upload.setOnClickListener(View.OnClickListener {
            startFileChooser()
        })

    }

    private fun startFileChooser() {
        val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
        pdfIntent.type = "application/pdf"
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(pdfIntent, 12)
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            12 -> if (resultCode == RESULT_OK) {

                pdfUri = data?.data!!
                val uri: Uri = data?.data!!
                val uriString: String = uri.toString()
                var pdfName: String? = null
                if (uriString.startsWith("content://")) {
                    var myCursor: Cursor? = null
                    try {
                        // Setting the PDF to the TextView
                        myCursor = applicationContext!!.contentResolver.query(uri, null, null, null, null)
                        if (myCursor != null && myCursor.moveToFirst()) {
                            pdfName = myCursor.getString(myCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                            upload.text = pdfName
                        }
                    } finally {
                        myCursor?.close()
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


    fun onClick(view: View) {
        //to access current date from the datePicker and the upload the pdf
        //button to submit both date and pdf document into the firebase
    }

}