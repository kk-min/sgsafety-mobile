package com.example.sg_safety_mobile.Logic

import android.app.Activity
import android.content.Context
import android.content.Intent


class FileManager(val context: Context) {
    fun getImageFile() {
        val pdfIntent = Intent()
        pdfIntent.type = "image/*"
        pdfIntent.action = Intent.ACTION_GET_CONTENT
        val cprActivity = context as Activity
        cprActivity.startActivityForResult(pdfIntent , 100)
    }

    fun getPDFFile() {
        val pdfIntent = Intent()
        pdfIntent.type = "application/pdf"
        pdfIntent.action = Intent.ACTION_GET_CONTENT
        val cprActivity = context as Activity
        cprActivity.startActivityForResult(pdfIntent , 200)
    }
}