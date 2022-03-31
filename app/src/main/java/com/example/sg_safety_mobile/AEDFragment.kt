package com.example.sg_safety_mobile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL



class AEDFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val intent = Intent(Intent.ACTION_VIEW)
        // intent.data = Uri.parse("https://data.gov.sg/api/action/datastore_search?resource_id=cdab7435-7bf0-4fa4-a8bd-6cfd231ca73a")
        // Log.i("","${intent.data}")
        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO) {
                // network call
//                val api = URL("https://data.gov.sg/api/action/datastore_search?resource_id=cdab7435-7bf0-4fa4-a8bd-6cfd231ca73a").readText()
                val result = URL("https://data.gov.sg/api/action/datastore_search?resource_id=cdab7435-7bf0-4fa4-a8bd-6cfd231ca73a").readText()
                val parser: Parser = Parser()
                val stringBuilder: StringBuilder = StringBuilder(result)
                val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                // println("Time : ${json.string("time")}, Since epoch : ${json.long("milliseconds_since_epoch")}, Date : ${json.string("date")}, ")
                val apiResponse = URL("https://data.gov.sg/api/action/datastore_search?resource_id=cdab7435-7bf0-4fa4-a8bd-6cfd231ca73a").readText()

                Log.i("This is the retrieve aed","${json.toJsonString()}")
                val aedtext: TextView =requireView().findViewById(R.id.aedText)
                aedtext.text=json.toJsonString()

            }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aed, container, false)
    }


}
