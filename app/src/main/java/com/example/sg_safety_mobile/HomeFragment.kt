package com.example.sg_safety_mobile


import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Switch
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class HomeFragment : Fragment(),View.OnClickListener {

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private lateinit var map : MapView;
    lateinit var locationReceiver: LocationReceiver;
    var locationService: LocationService? = null
    var locationServiceIntent: Intent? = null
    lateinit var lm: LocationManager
    lateinit var loc: Location


    //FOR PAGE VIEW
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v= inflater.inflate(R.layout.fragment_home, container, false)
        var button: Button =v.findViewById(R.id.alert_button)

        Configuration.getInstance().load(v.context , PreferenceManager.getDefaultSharedPreferences(v.context))

        map = v.findViewById<MapView>(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        val mapController = map.controller
        mapController.setZoom(20)
        //mapController.setCenter(startPoint);
        map.maxZoomLevel= 20.0
        map.minZoomLevel=14.0


        if(ActivityCompat.checkSelfPermission(v.context,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            &&  ActivityCompat.checkSelfPermission(v.context,android.Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),111)
        }

        locationReceiver = LocationReceiver(v)
        val filter = IntentFilter("UPDATE_LOCATION+ADDRESS")
        v.context.registerReceiver(locationReceiver, filter); // Register our receiverontext));


        //PROMPT ALERT BOX TO MAKE SURE USER REALLY NEED HELP
        button.setOnClickListener{
            showAlertDialog()
        }
        return v
    }


    //FOR PAGE VIEW
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    //ALERT TO MAKE SURE USER DON'T ACCIDENTALLY PRESS THE SEND HELP BUTTON
    private fun showAlertDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(activity!!)

        alertDialog.setTitle("Are you sure?")
        alertDialog.setMessage("By pressing Yes,help message will be sent to SCDF and Users nearby")
        alertDialog.setPositiveButton(
            "Yes"
        ) { _, _ ->
            //Go to Alert Page Activity which will display No. of helpers accepted to help
            val intent = Intent(activity, AlertPage::class.java)
            startActivity(intent)

        }
        //cancel the alert button
        alertDialog.setNegativeButton(
            "No"
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }


    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
    override fun onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = ArrayList<String>();
        var i = 0;
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i]);
            i++;
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                view?.context as Activity,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    /*private fun requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            permissionsToRequest.add(permission);
        }
    }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }*/
    private fun addMarker(map: MapView?, point: GeoPoint, title: String) {
        val startMarker = Marker(map)
        //Lat ‎23.746466 Lng 90.376015
        startMarker.icon=map?.context?.resources?.getDrawable(R.drawable.userloc)
        startMarker.position = point
        startMarker.title = title
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map?.overlays?.add(startMarker)
        map?.invalidate()
    }


//    private fun addingWaypoints(map: MapView?, startPoint: GeoPoint,endPoint:GeoPoint) {
//        val roadManager = OSRMRoadManager(view?.context,"MYUSERAGENT")
//        roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT)
//        val waypoints = ArrayList<GeoPoint>()
//        waypoints.add(startPoint)
//        //waypoints.add(GeoPoint(23.816237, 90.366725))
//
//        waypoints.add(endPoint)
//
//        MyRoadAsyncTask(roadManager, waypoints).execute()
//
//        Observable.fromCallable {
//            retrievingRoad(roadManager, waypoints)
//        }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//
//            }, {
//
//            }, {
//                map?.invalidate()
//            })
//
//        addMarker(map, endPoint, "End Point")
//    }
//
//    private fun retrievingRoad(roadManager: OSRMRoadManager, waypoints: ArrayList<GeoPoint>) {
//        // Retrieving road
//
//        val road = roadManager.getRoad(waypoints)
//        val roadOverlay = RoadManager.buildRoadOverlay(road)
//        map?.overlays?.add(roadOverlay);
//
//        val nodeIcon = map?.context?.resources?.getDrawable(R.mipmap.ic_launcher)
//        for (i in 0 until road.mNodes.size) {
//            val node = road.mNodes[i]
//            val nodeMarker = Marker(map)
//            nodeMarker.position = node.mLocation
//            nodeMarker.setIcon(nodeIcon)
//            nodeMarker.title = "Step $i"
//            map?.overlays?.add(nodeMarker)
//            nodeMarker.snippet = node.mInstructions;
//            nodeMarker.subDescription = Road.getLengthDurationText(map?.context, node.mLength, node.mDuration);
//        }
//    }
    


//    private inner class MyRoadAsyncTask(val roadManager: OSRMRoadManager,
//                                        val waypoints: ArrayList<GeoPoint>) : AsyncTask<Void, Void, String>() {
//
//        override fun doInBackground(vararg params: Void?): String? {
//            val road = roadManager.getRoad(waypoints)
//            val roadOverlay = RoadManager.buildRoadOverlay(road)
//            map?.overlays?.add(roadOverlay);
//
//            val nodeIcon = map?.context?.resources?.getDrawable(R.mipmap.ic_launcher)
//            for (i in 0 until road.mNodes.size) {
//                val node = road.mNodes[i]
//                val nodeMarker = Marker(map)
//                nodeMarker.position = node.mLocation
//                nodeMarker.setIcon(nodeIcon)
//                nodeMarker.title = "Step $i"
//                map?.overlays?.add(nodeMarker)
//                nodeMarker.snippet = node.mInstructions;
//                nodeMarker.subDescription = Road.getLengthDurationText(map?.context, node.mLength, node.mDuration);
//            }
//
//            return null
//        }
//
//        override fun onPostExecute(result: String?) {
//            map?.invalidate()
//        }
//    }

}
