package com.garasje.atbetter

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.children
import androidx.navigation.findNavController
import com.garasje.atbetter.api.EnturApi
import com.garasje.atbetter.core.BusStop
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var textView1: TextView
    private lateinit var cardContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.injectFields()

        this.getLocationAccess { location -> getNearestStops(location) { stops -> addBussStops(stops) } }

    }

    private fun injectFields() {
        textView1 = findViewById(R.id.textView1)
        cardContainer = findViewById(R.id.cardContainer)
    }


    private fun getLocationAccess(onSuccess: (location: Location) -> Unit) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val reqCode = 123 // I don't know what this is (haryp2309)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), reqCode
            )
        }
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    onSuccess(location)
                }
            }

    }


    private fun getNearestStops(
        location: Location,
        onSuccess: (busStops: Collection<BusStop>) -> Unit
    ) {
        EnturApi().getNearestStops(this, location, onSuccess, {
            textView1.text = it.message
        })
    }

    private fun addBussStops(busStops: Collection<BusStop>) {

        for (stop in busStops) {

            LayoutInflater.from(this).inflate(R.layout.card, cardContainer)


            val lo = cardContainer.getChildAt(cardContainer.childCount - 1)
                .findViewById<TextView>(R.id.textView1)
            lo.text = stop.name
        }


    }

}