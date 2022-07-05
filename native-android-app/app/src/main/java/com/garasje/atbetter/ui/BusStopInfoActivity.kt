package com.garasje.atbetter.ui

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.garasje.atbetter.R
import com.garasje.atbetter.core.BusLineAtStop
import com.garasje.atbetter.core.BusStop
import com.garasje.atbetter.helpers.GlobalPreferencesHelpers

const val BUS_STOP_EXTRAS = "com.garasje.atbetter.busstop"

class BusStopInfoActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var bussesRecyclerView: RecyclerView
    private lateinit var toggleFavorite: Button

    private lateinit var busStop: BusStop
    private val busInfoRecyclerViewAdapter = BusInfoRecyclerViewAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_stop_info)

        retrieveState()
        injectFields()

        toolbar.title = busStop.name

        fetchBusStopTimetable()
    }

    private fun fetchBusStopTimetable() {
        GlobalState.fetchBusStopTimetable(this, busStop)
        GlobalState.busJourneys.subscribeToBusStop(busStop) { busses ->
            val busLineAtStops = busses
                .map { bus -> bus.busLine }
                .distinct()
                .map { busLine ->
                    BusLineAtStop(busLine, busStop, busses.filter { it.busLine == busLine })
                }
            busInfoRecyclerViewAdapter.updateUpcomingBuses(busLineAtStops)
        }

    }

    private fun retrieveState() {
        val busStopId = intent.extras?.getString(BUS_STOP_EXTRAS)
        val busStop = busStopId?.let { GlobalState.busStops.getBusStop(it) }
        if (busStop == null) {
            finish()
        } else {
            this.busStop = busStop
        }
    }

    private fun injectFields() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        bussesRecyclerView = findViewById(R.id.bussesRecyclerView)
        bussesRecyclerView.adapter = busInfoRecyclerViewAdapter
        bussesRecyclerView.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        toggleFavorite = findViewById(R.id.toggleFavorite)

        toggleFavorite.setOnClickListener {
            GlobalPreferencesHelpers(this).toggleFavourite(busStop.id)
        }
    }


}
