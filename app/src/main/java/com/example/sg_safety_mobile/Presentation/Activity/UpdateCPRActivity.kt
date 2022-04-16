package com.example.sg_safety_mobile.Presentation.Activity



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
import com.example.sg_safety_mobile.Logic.FileReader
import com.example.sg_safety_mobile.Logic.FirebaseManager
import com.example.sg_safety_mobile.Logic.ImageReader
import com.example.sg_safety_mobile.Logic.PDFReader
import com.example.sg_safety_mobile.R
import com.example.sg_safety_mobile.databinding.ActivityUpdateCprBinding
import com.google.firebase.storage.FirebaseStorage
import java.util.*

/**
 *Activity for user to update their CPR certificate and is accessed
 * via Manage Profile Fragment[com.example.sg_safety_mobile.Presentation.Activity.LoginActivity]
 *
 *
 * @since 2022-4-15
 */
class UpdateCPRActivity : AppCompatActivity() {

    /**
     *UI Datepicker for user to choose their CPR certificate expiry date
     */
    private lateinit var datePicker:DatePicker
    /**
     *UI text view that shows PDF
     */
    private lateinit var pdfTextView:TextView
    /**
     *UI text view that show Image
     */
    private lateinit var iv:ImageView
    /**
     *Image URI
     */
    private lateinit var imageUri : Uri
    /**
     *Update CPR binding
     */
    private lateinit var binding : ActivityUpdateCprBinding
    /**
     *Expiry date
     */
    private lateinit var expiry : String
    /**
     *Firebase Manager
     */
    private val firebaseManager = FirebaseManager(this)
    /**
     *PDF reader
     */
    private val pdfReader = FileReader(PDFReader(this))
    /**
     *Image reader
     */
    private  val imageReader = FileReader(ImageReader(this))

    /**
     *Runs when activity is created
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        //initialize current page
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateCprBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewEInitializations()

        //setup support bar and back button
        supportActionBar?.title = "Update CPR"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //initialise Uri and expiry date
        imageUri = Uri.EMPTY
        expiry=""


        val today = Calendar.getInstance()

        //date picket to select the date that that certificate expires in
        datePicker.init(today.get(Calendar.YEAR) , today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)){
                view, year, month, day ->
            val month: Int = month +1
            expiry="$day/$month/$year"
        }

        //Button---------------------------------------------------------------------------------------------------
        //prompt to upload png file
        binding.uploadCpr.setOnClickListener {
            imageReader.readFile()
        }

        //prompt to upload pdf file
        binding.uploadPdfCpr.setOnClickListener {
            pdfReader.readFile()
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

    /**
     *Initialize all the UI views
     */
    private fun viewEInitializations() {
       datePicker = findViewById(R.id.date_Picker)
        pdfTextView = findViewById(R.id.pdfText)
        iv = findViewById(R.id.image_view)

    }


    /**
     *Check if expiry date selected by user is in the past/current date
     */
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


    /**
     *Runs when pdf is uploaded
     */
    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //upload an image
        if(requestCode == 100 && resultCode == RESULT_OK){
            imageUri = data?.data!!
            //display selected file

            pdfTextView.text = null


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

                    iv.setImageURI(null)

                    //display pdf name
                    val displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))

                    pdfTextView.text = displayName

                }
            } finally {
                cursor?.close()
            }

        }
    }


    /**
     *Upload a file
     */
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

                firebaseManager.uploadFileToDB(storeRef.path, expiry)
                Toast.makeText(this , "Successful upload" , Toast.LENGTH_LONG).show()
                if(progressDialog.isShowing) progressDialog.dismiss()

            }

            .addOnFailureListener{
                if (progressDialog.isShowing) progressDialog.dismiss()

                Toast.makeText(this, "Unsuccessful upload" , Toast.LENGTH_SHORT).show()
            }
    }

    /**
     *Do something when a certain item is selected
     *
     * @param item  menu item to be selected
     *
     * @return true when item selected false otherwise
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }



}