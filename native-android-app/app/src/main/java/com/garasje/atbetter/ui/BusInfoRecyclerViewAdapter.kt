package com.garasje.atbetter.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.garasje.atbetter.R
import com.garasje.atbetter.core.UpcomingBus

class BusInfoRecyclerViewAdapter :
    RecyclerView.Adapter<BusInfoRecyclerViewAdapter.ViewHolder>() {

    val upcomingBuses = ArrayList<UpcomingBus>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val busNumber: TextView = itemView.findViewById(R.id.busNumber)
        val busName: TextView = itemView.findViewById(R.id.busName)
        val busArrivalTime: TextView = itemView.findViewById(R.id.busArrivalTime)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateUpcomingBuses(upcomingBus: UpcomingBus) {
        val existingBus =
            this.upcomingBuses.find { existingBus -> upcomingBus.id == existingBus.id }
        if (existingBus != null) {
            existingBus.arrivesAt = upcomingBus.arrivesAt
            existingBus.realTime = upcomingBus.realTime
        } else {
            this.upcomingBuses.add(upcomingBus)
        }

        this.upcomingBuses.sortBy { bus -> bus.arrivesAt }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.bus_info_card, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bus = upcomingBuses[position]
        holder.busName.text = bus.busName
        holder.busNumber.text = bus.busNumber
        holder.busArrivalTime.text = "${bus.relativeArrivesAt()} min"
    }

    override fun getItemCount(): Int {
        return upcomingBuses.size
    }


}