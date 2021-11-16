package com.garasje.atbetter

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.children
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.garasje.atbetter.api.EnturApi
import com.garasje.atbetter.core.BusStop
import com.garasje.atbetter.helpers.LocationHelpers
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var busStopsRecyclerView: RecyclerView

    private val busStopsRecyclerViewAdapter = BusStopsRecyclerViewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.injectFields()
        loadBusStops()

    }


    private fun injectFields() {
        busStopsRecyclerView = findViewById(R.id.busStopsRecyclerView)
        busStopsRecyclerView.adapter = busStopsRecyclerViewAdapter
        busStopsRecyclerView.layoutManager = LinearLayoutManager(this)
    }


    private fun loadBusStops() {
        LocationHelpers().onLocationAccess(this) { location ->
            EnturApi().getNearestStops(this, location, {
                busStopsRecyclerViewAdapter.addBusStop(it)
            }, {
                Toast.makeText(this, "Could not find nearest stops", Toast.LENGTH_SHORT).show()
            })
        }
    }

}