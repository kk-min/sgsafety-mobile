package com.example.sg_safety_mobile.Logic

import android.app.Activity
import android.content.Context
import android.content.Intent

/**
 *Class uses for reading of image
 *
 * @since 2022-4-15
 */
class ImageReader(val context: Context): Reader {
    /**
     *(context)
     * Application context of the activity of fragment
     */

    /**
     *Read the file that is uploaded by user
     */
    override fun readFile() {
        val pdfIntent = Intent()
        pdfIntent.type = "image/*"
        pdfIntent.action = Intent.ACTION_GET_CONTENT
        val cprActivity = context as Activity
        cprActivity.startActivityForResult(pdfIntent , 100)
    }
}