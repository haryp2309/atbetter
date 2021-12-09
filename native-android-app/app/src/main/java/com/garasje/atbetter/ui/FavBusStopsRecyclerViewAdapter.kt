package com.garasje.atbetter.ui

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.garasje.atbetter.R
import com.garasje.atbetter.core.BusStop
import kotlin.math.roundToInt

class FavBusStopsRecyclerViewAdapter(onClick: (BusStop) -> Unit, getString: (Int, Int) -> String) : BusStopsRecyclerViewAdapter(
    onClick, getString
) {
    override var currentLocation: Location = Location(NO_LOCATION)
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.fav_bus_stop_card, parent, false)
        return ViewHolder(view)
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun sort() {
        if (currentLocation.provider != NO_LOCATION) {
            this.busStops.sortBy { it.name }
        }
        notifyDataSetChanged()
    }

}