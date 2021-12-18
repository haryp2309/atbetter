package com.garasje.atbetter.helpers

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices

object LocationHelpers {

    const val LOCATION_REFRESH_TIME = 10000L // in ms
    const val LOCATION_REFRESH_DISTANCE = 100F // in meter

    fun onLocationAccess(activity: AppCompatActivity, onSuccess: (location: Location) -> Unit) {

        val coarseLocationPermission = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )


        if (coarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            val reqCode = 123 // I don't know what this is (haryp2309)
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), reqCode
            )
        }


        LocationServices.getFusedLocationProviderClient(activity)
            .lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    onSuccess(location)
                }
            }

        val locationManager = activity.getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            LOCATION_REFRESH_TIME,
            LOCATION_REFRESH_DISTANCE,
            onSuccess
        )

    }
}