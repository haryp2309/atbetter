package com.garasje.atbetter.ui;

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.garasje.atbetter.R
import com.garasje.atbetter.api.EnturApi
import com.garasje.atbetter.core.BusStop
import com.garasje.atbetter.core.UpcomingBus
import java.time.LocalDateTime

const val BUS_STOP_EXTRAS = "com.garasje.atbetter.busstop"

class BusStopInfoActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var bussesRecyclerView: RecyclerView

    private lateinit var busStop: BusStop
    private val busInfoRecyclerViewAdapter = BusInfoRecyclerViewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_stop_info)

        retrieveState()
        injectFields()

        toolbar.title = busStop.name


        EnturApi.getUpcomingBusses(this, busStop.id, {
            it.forEach {
                busInfoRecyclerViewAdapter.updateUpcomingBuses(it)
            }
        }, {
            Toast.makeText(this, "Could not fetch upcoming busses", Toast.LENGTH_SHORT)
        })
    }

    private fun retrieveState() {
        val busStopId = intent.extras?.getString(BUS_STOP_EXTRAS)
        val busStop = busStopId?.let { GlobalState.getBusStop(it) }
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
    }


}
