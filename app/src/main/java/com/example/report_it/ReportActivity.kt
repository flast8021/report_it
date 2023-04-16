package com.example.report_it

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import android.provider.Settings
import android.widget.Toast
import com.example.report_it.databinding.ActivityLoginBinding
import com.example.report_it.databinding.ActivityRegisterBinding
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_report.*

class ReportActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ReportActivity
    private lateinit var firebaseAuth: FirebaseAuth
    private val currentLocation = "Nil"
    private val incidentLocation = "Nil"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        firebaseAuth = FirebaseAuth.getInstance()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        bottom_navigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_exit -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_donation -> {
                    val intent = Intent(this, DonationActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_help -> {
                    val phoneNum = "tel:112"
                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse(phoneNum))
                    startActivity(dialIntent)
                    true
                }R.id.navigation_info -> {
                val intent = Intent(this, AppInfoActivity::class.java)
                startActivity(intent)
                true
                }
                else -> false
            }
        }
        saveButton.setOnClickListener{
            //getCurrentLocation()
        }
    }
    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user and ask for permission
                Snackbar.make(
                    mapView,
                    "Location permission is required for this app to function properly",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("OK") {
                    // Request the permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_LOCATION_PERMISSION
                    )
                }.show()
            } else {
                // Request the permission without explanation
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            }
        } else {
            // Permission already granted
            getCurrentLocation()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                getCurrentLocation()
            } else {
                // Permission denied
                Snackbar.make(
                    mapView,
                    "Location permission is required for this app to function properly",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Settings") {
                    // Direct the user to the app settings to grant the permission
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }.show()
            }
        }
    }


    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)

                    val userDocRef = FirebaseFirestore.getInstance().collection("users").document(firebaseAuth.currentUser!!.uid)

                    userDocRef.update("Current Location", latLng)
                        .addOnSuccessListener {
                        }
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            }
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {

                    val circle = googleMap.addCircle(
                        CircleOptions()
                            .center(LatLng(location.latitude, location.longitude))
                            .radius(1000.0)
                            .strokeColor(0x50465e7d)
                            .fillColor(0x3013AED4)
                    )
                    val latLng = LatLng(location.latitude, location.longitude)
                    val markerOptions = MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .title("Current Location")
                    googleMap.addMarker(markerOptions)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))

                } else {
                    // Location is null
                    Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 123
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
            return
        }else{
            getCurrentLocation()
        }

        // Get user's current location
       val currentLocation=  fusedLocationClient.lastLocation
            currentLocation.addOnSuccessListener { location ->
                val results = FloatArray(1)
                location?.let {

                    val latLng = LatLng(it.latitude, it.longitude)
                    var isMarkerDropped = false


                    Location.distanceBetween(location.latitude,location.longitude,latLng.latitude,latLng.longitude,results)

                    val distanceInMeters = results[0]

                    if (distanceInMeters > 1000){
                        Toast.makeText(this, "Marker is too far from current location", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                    googleMap.setOnMapLongClickListener { latLng ->
                        if(!isMarkerDropped){
                            if(distanceInMeters > 1000){
                                isMarkerDropped=false
                            }else{
                                val markerOptions = MarkerOptions().position(latLng).title(latLng.toString()).draggable(true)
                                googleMap.addMarker(markerOptions)
                                val userDocRefrence = FirebaseFirestore.getInstance().collection("users").document(firebaseAuth.currentUser!!.uid)
                                saveButton.setOnClickListener{
                                    Toast.makeText(this@ReportActivity, "Successfully Saved!", Toast.LENGTH_SHORT).show()
                                        if(distanceInMeters<=1000){
                                            userDocRefrence.update("Incident Location", latLng)
                                                .addOnSuccessListener {
                                                    val intent=Intent(this,DetailedActivity::class.java)
                                                    startActivity(intent)
                                                    finish()
                                                }
                                        }else{
                                            userDocRefrence.update("UTI distance", distanceInMeters.toString())
                                            Toast.makeText(this@ReportActivity, "Marker is too far from current location", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                isMarkerDropped = true
                            }
                        }
                        googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
                            override fun onMarkerDragStart(marker: Marker) {
                            }
                            override fun onMarkerDrag(marker: Marker) {
                            }
                            override fun onMarkerDragEnd(marker: Marker) {
                                // Update the marker's position with the new latitude and longitude
                                val newPosition = marker.position
                                // Calculate distance between new marker position and current location
                                Location.distanceBetween(location.latitude, location.longitude, newPosition.latitude, newPosition.longitude, results)
                                val distanceInMeters = results[0]
                                val userDocRefrence = FirebaseFirestore.getInstance().collection("users").document(firebaseAuth.currentUser!!.uid)
                                if (distanceInMeters > 1000) {
                                    // Remove the marker from the map
                                    marker.remove()
                                    Toast.makeText(this@ReportActivity, "Marker too far from Location.", Toast.LENGTH_SHORT).show()
                                    isMarkerDropped = false
                                } else {
                                    marker.title = newPosition.toString()
                                    Toast.makeText(this@ReportActivity, "Location is taken", Toast.LENGTH_SHORT).show()
                                    userDocRefrence.update("UTI distance", distanceInMeters.toString())
                                }
                            }
                        })
                    }
                    val cameraPosition = CameraUpdateFactory.newLatLngZoom(latLng, 14f)
                    googleMap.moveCamera(cameraPosition)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
