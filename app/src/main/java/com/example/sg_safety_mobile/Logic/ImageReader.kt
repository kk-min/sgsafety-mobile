package com.example.sg_safety_mobile.Logic

import android.app.Activity
import android.content.Context
import android.content.Intent

class ImageReader(val context: Context): Reader {
    override fun readFile() {
        val pdfIntent = Intent()
        pdfIntent.type = "image/*"
        pdfIntent.action = Intent.ACTION_GET_CONTENT
        val cprActivity = context as Activity
        cprActivity.startActivityForResult(pdfIntent , 100)
    }
}