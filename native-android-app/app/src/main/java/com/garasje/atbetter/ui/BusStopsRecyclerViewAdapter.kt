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

open class BusStopsRecyclerViewAdapter(
    val onClick: (busStop: BusStop) -> Unit,
    val getString: (resId: Int, formatArg1: Int) -> String
) : RecyclerView.Adapter<BusStopsRecyclerViewAdapter.ViewHolder>() {

    val busStops = ArrayList<BusStop>()

    open var currentLocation: Location = Location(NO_LOCATION)
        set(value) {
            field = value
            sort()
        }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val busStopName: TextView = itemView.findViewById(R.id.busStopName)
        val cardView: CardView = itemView.findViewById(R.id.card1)
        val distanceText: TextView = itemView.findViewById(R.id.distanceText)

    }

    @SuppressLint("NotifyDataSetChanged")
    open fun sort() {
        if (currentLocation.provider != NO_LOCATION) {
            this.busStops.sortBy { it.location.distanceTo(currentLocation) }
        }
        notifyDataSetChanged()
    }

    fun addBusStop(busStop: BusStop) {
        this.busStops.add(busStop)
        notifyItemInserted(busStops.indexOf(busStop))
        sort()
    }

    fun containsBusStopId(busStopId: String): Boolean {
        return this.busStops.map { busStop -> busStop.id }.contains(busStopId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.bus_stop_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.busStopName.text = busStops[position].name
        holder.cardView.setOnClickListener {
            onClick(busStops[position])
        }
        if (currentLocation.provider != NO_LOCATION) {
            val distance = busStops[position].location.distanceTo(currentLocation).roundToInt()
            holder.distanceText.text = getString(R.string.x_meters_short, distance)
            //holder.distanceText.text = "${busStops[position].location.longitude} - ${currentLocation.longitude}"
        } else {
            holder.distanceText.text = ""
        }
    }

    override fun getItemCount(): Int {
        return busStops.size
    }

    companion object {
        const val NO_LOCATION = "NO_LOCATION"
    }

}