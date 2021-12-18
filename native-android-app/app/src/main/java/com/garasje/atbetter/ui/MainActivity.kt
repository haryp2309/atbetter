package com.garasje.atbetter.ui

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.garasje.atbetter.R
import com.garasje.atbetter.api.EnturApi
import com.garasje.atbetter.core.BusStop
import com.garasje.atbetter.helpers.GlobalPreferencesHelpers
import com.garasje.atbetter.helpers.LocationHelpers


class MainActivity : AppCompatActivity() {

    private lateinit var busStopsRecyclerView: RecyclerView
    private lateinit var favBusStopsRecyclerView: RecyclerView
    private lateinit var rootLayout: DraggableConstraintLayout
    private lateinit var nearestStopDrawer: LinearLayout


    private val busStopsRecyclerViewAdapter =
        BusStopsRecyclerViewAdapter({ onBusStopClick(it) },
            { resId, formatArg1 ->
                getString(resId, formatArg1)
            })

    private val favBusStopsRecyclerViewAdapter =
        FavBusStopsRecyclerViewAdapter({ onBusStopClick(it) },
            { resId, formatArg1 ->
                getString(resId, formatArg1)
            })


    private fun onBusStopClick(busStop: BusStop) {
        val intent = Intent(this, BusStopInfoActivity::class.java)
        GlobalState.addBusStop(busStop)
        intent.putExtra(BUS_STOP_EXTRAS, busStop.id)
        startActivity(intent)

    }

    override fun onResume() {
        super.onResume()

        this.loadLocationBasedStopsAndData()
        this.loadFavourites()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.injectFields()
        this.initialUIUpdates()
    }

    private fun loadFavourites() {
        val favouriteIds = GlobalPreferencesHelpers(this).getFavouriteStopIds()
        favBusStopsRecyclerViewAdapter.filterAwayStops { id ->
            favouriteIds.contains(id)
        }
        favouriteIds.forEach { busStopId ->
            if (!favBusStopsRecyclerViewAdapter.containsBusStopId(busStopId)) {
                // Favorite added
                EnturApi.getStop(this, busStopId, {
                    val name = it.getJSONObject("name").getString("value")
                    val busStop =
                        BusStop(name, busStopId, Location(BusStopsRecyclerViewAdapter.NO_LOCATION))
                    favBusStopsRecyclerViewAdapter.addBusStop(busStop)
                }, {
                    Toast.makeText(
                        this,
                        "Could not fetch favourite $busStopId",
                        Toast.LENGTH_SHORT
                    ).show()
                })
            }
        }
    }


    private fun injectFields() {
        busStopsRecyclerView = findViewById(R.id.busStopsRecyclerView)
        favBusStopsRecyclerView = findViewById(R.id.favBusStopsRecyclerView)
        nearestStopDrawer = findViewById(R.id.nearestStopDrawer)
        rootLayout = findViewById(R.id.rootLayout)
    }

    private fun initialUIUpdates() {
        busStopsRecyclerView.adapter = busStopsRecyclerViewAdapter
        busStopsRecyclerView.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        favBusStopsRecyclerView.adapter = favBusStopsRecyclerViewAdapter
        favBusStopsRecyclerView.layoutManager = object : GridLayoutManager(this, 2) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        rootLayout.setDrawer(nearestStopDrawer)
    }


    private fun loadLocationBasedStopsAndData() {
        LocationHelpers.onLocationAccess(this) { location ->
            favBusStopsRecyclerViewAdapter.currentLocation = location
            busStopsRecyclerViewAdapter.currentLocation = location

            EnturApi.getNearestStops(this, location, { busStop ->
                if (!busStopsRecyclerViewAdapter.containsBusStopId(busStop.id)) {
                    busStopsRecyclerViewAdapter.addBusStop(busStop)
                }
            }, {
                Toast.makeText(this, "Could not find nearest stops", Toast.LENGTH_SHORT).show()
            })
        }
    }


}