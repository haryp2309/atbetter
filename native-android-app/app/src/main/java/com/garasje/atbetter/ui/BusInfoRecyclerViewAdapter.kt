package com.garasje.atbetter.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.garasje.atbetter.R
import com.garasje.atbetter.helpers.StringFormatter

class BusInfoRecyclerViewAdapter(val context: Context) :
    RecyclerView.Adapter<BusInfoRecyclerViewAdapter.ViewHolder>() {

    private val upcomingBuses = ArrayList<BusLineAtStop>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val busNumber: TextView = itemView.findViewById(R.id.busNumber)
        val busName: TextView = itemView.findViewById(R.id.busName)
        val busArrivalTime: TextView = itemView.findViewById(R.id.busArrivalTime)
        val upcomingBus1: TextView = itemView.findViewById(R.id.upcomingBus1)
        val upcomingBus2: TextView = itemView.findViewById(R.id.upcomingBus2)
        val upcomingBus3: TextView = itemView.findViewById(R.id.upcomingBus3)
        val currentlyAtString: TextView = itemView.findViewById(R.id.currentlyAtString)
        private val expandedLayout: ConstraintLayout = itemView.findViewById(R.id.expandedLayout)
        private val cardConstraintLayout: ConstraintLayout =
            itemView.findViewById(R.id.cardConstraintLayout)
        private val cardView: CardView = itemView.findViewById(R.id.cardView)

        var expanded = false
        private val transitionDurationMs = 200L
        private var targetHeight = expandedLayout.measuredHeight

        init {
            cardView.setOnClickListener { toggle() }
        }

        private fun toggle() {
            if (expanded) {
                close()
            } else {
                open()
            }
        }

        private fun close() {
            targetHeight = expandedLayout.measuredHeight
            val animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                    expandedLayout.layoutParams.height = if (interpolatedTime == 1F) 1 else {
                        targetHeight - (interpolatedTime * targetHeight).toInt()
                    }
                    expandedLayout.alpha = 1 - interpolatedTime
                    expandedLayout.requestLayout()
                }
            }

            animation.duration = transitionDurationMs
            animation.fillAfter = true

            //expandedLayout.visibility = View.GONE
            expanded = false

            expandedLayout.startAnimation(animation)
        }

        private fun measureHeight() {
            expandedLayout.measure(
                View.MeasureSpec.makeMeasureSpec(
                    0,
                    View.MeasureSpec.UNSPECIFIED
                ), View.MeasureSpec.makeMeasureSpec(
                    0,
                    View.MeasureSpec.UNSPECIFIED
                )
            )
            targetHeight = expandedLayout.measuredHeight
        }

        private fun open() {
            if (targetHeight == 0) {
                measureHeight()
            }
            Log.println(Log.INFO, null, "✅ ${expandedLayout.measuredHeight}")
            val animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {

                    expandedLayout.layoutParams.height = when (interpolatedTime) {
                        1F -> ConstraintLayout.LayoutParams.WRAP_CONTENT
                        else -> (interpolatedTime * targetHeight).toInt()
                    }

                    if (expandedLayout.layoutParams.height == 0) {
                        expandedLayout.layoutParams.height = 1
                    }

                    expandedLayout.alpha = interpolatedTime
                    expandedLayout.requestLayout()
                }
            }

            animation.duration = transitionDurationMs
            animation.fillAfter = true

            expandedLayout.visibility = View.VISIBLE
            expanded = true

            expandedLayout.startAnimation(animation)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateUpcomingBuses(busLineAtStops: Collection<BusLineAtStop>) {
        this.upcomingBuses.clear()
        this.upcomingBuses.addAll(busLineAtStops)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.bus_info_card, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bus = upcomingBuses[position]
        holder.busName.text = bus.busLine.busName
        holder.busNumber.text = bus.busLine.busNumber

        val arrivesAtIterator = bus.sortedArrivesAt.iterator()

        listOf(
            holder.busArrivalTime,
            holder.upcomingBus1,
            holder.upcomingBus2,
            holder.upcomingBus3
        ).forEach { textField ->
            if (arrivesAtIterator.hasNext()) {
                textField.text =
                    StringFormatter.timeToRelativeTime(context, arrivesAtIterator.next().arrivesAt)
            }
        }

        holder.currentlyAtString.text =
            "Currently at: ${bus.currentlyAt?.name ?: "Unknown Location"}"

    }

    override fun getItemCount(): Int {
        return upcomingBuses.size
    }


}