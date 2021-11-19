package com.garasje.atbetter.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.garasje.atbetter.R
import com.garasje.atbetter.api.EnturApi
import com.garasje.atbetter.helpers.LocationHelpers

class MainActivity : AppCompatActivity() {

    private lateinit var busStopsRecyclerView: RecyclerView

    private val busStopsRecyclerViewAdapter = BusStopsRecyclerViewAdapter {
        val intent = Intent(this, BusStopInfoActivity::class.java)
        GlobalState.addBusStop(it)
        intent.putExtra(BUS_STOP_EXTRAS, it.id)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.injectFields()
        this.loadBusStops()
    }


    private fun injectFields() {
        busStopsRecyclerView = findViewById(R.id.busStopsRecyclerView)
        busStopsRecyclerView.adapter = busStopsRecyclerViewAdapter
        busStopsRecyclerView.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
    }


    private fun loadBusStops() {
        LocationHelpers().onLocationAccess(this) { location ->
            EnturApi.getNearestStops(this, location, {
                  busStopsRecyclerViewAdapter.addBusStop(it)
            }, {
                Toast.makeText(this, "Could not find nearest stops", Toast.LENGTH_SHORT).show()
            })
        }
    }

}