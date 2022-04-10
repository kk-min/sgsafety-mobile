package com.example.sg_safety_mobile.Logic

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.app.ActivityCompat.startActivityForResult

class FileReader(val reader: Reader) {
    fun readFile(){
        reader.readFile()
    }
}