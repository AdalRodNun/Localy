package com.example.proyect

import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.proyect.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener{

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    lateinit var latitud : String
    lateinit var longitud : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        latitud = intent.getStringExtra("latitud")!!
        longitud = intent.getStringExtra("longitud")!!

        /*//how to track live user location
        val request = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10 * 1000
            fastestInterval = 2 * 1000
        }

        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(
              request,
                object : LocationCallback(){
                    override fun onLocationResult(p0: LocationResult) {
                        super.onLocationResult(p0)

                        Log.wtf("UPDATED LOCATION", p0.lastLocation.toString())
                    }
                },
                Looper.myLooper()
            );
        }*/
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(latitud.toDouble(), longitud.toDouble())
        mMap.addMarker(MarkerOptions().position(sydney).title("TEC"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18f))
        mMap.setOnMapClickListener(this)
        //mMap.isMyLocationEnabled = true;
        enableMyLocation()
    }

    fun enableMyLocation(){

        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            // if we dont have the permission request it
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(permissions, 0)
        } else {

            mMap.isMyLocationEnabled = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "PERMISSION GRANTED :D", Toast.LENGTH_SHORT).show()
            mMap.isMyLocationEnabled = true
        } else {
            Toast.makeText(this, "PERMISSION DENIED D:", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapClick(latLng: LatLng) {
        val intent = Intent()
        var latitude = latLng.latitude.toString()
        var longitude = latLng.longitude.toString()

        Log.wtf("DEBUG", "${latitude}, ${longitude}")

        intent.putExtra("latitud", latitude)
        intent.putExtra("longitud", longitude)

        setResult(Activity.RESULT_OK, intent)

        finish()

        /*mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("dynamic marker")
                .alpha(0.5f)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        )*/
    }
}