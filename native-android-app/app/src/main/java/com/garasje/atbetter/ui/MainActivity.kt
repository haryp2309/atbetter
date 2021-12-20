package com.garasje.atbetter.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
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
    private lateinit var scrollView: ScrollView


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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        }
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
        EnturApi.getStops(this, favouriteIds, {
            // Favorite added
                busStops ->
            busStops.forEach { busStop ->
                if (!favBusStopsRecyclerViewAdapter.containsBusStopId(busStop.id)) {
                    favBusStopsRecyclerViewAdapter.addBusStop(busStop)
                }
            }
        }, {
            Toast.makeText(
                this,
                "Could not fetch favourite $favouriteIds",
                Toast.LENGTH_SHORT
            ).show()
        })
    }


    private fun injectFields() {
        busStopsRecyclerView = findViewById(R.id.busStopsRecyclerView)
        favBusStopsRecyclerView = findViewById(R.id.favBusStopsRecyclerView)
        nearestStopDrawer = findViewById(R.id.nearestStopDrawer)
        rootLayout = findViewById(R.id.rootLayout)
        scrollView = findViewById(R.id.scrollView)
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