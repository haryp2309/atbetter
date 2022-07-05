package com.garasje.atbetter.ui

import android.content.Context
import android.location.Location
import android.widget.Toast
import com.garasje.atbetter.api.EnturApi
import com.garasje.atbetter.core.BusRegister
import com.garasje.atbetter.core.BusStop
import com.garasje.atbetter.core.StopRegister
import com.garasje.atbetter.helpers.GlobalPreferencesHelpers

object GlobalState {

    val busStops = StopRegister()
    val busJourneys = BusRegister()

    fun fetchBusStopTimetable(
        context: Context,
        busStop: BusStop,
    ) = EnturApi.fetchBusStopTimetable(context, busStop, {
        Toast.makeText(context, "Could not fetch upcoming busses", Toast.LENGTH_SHORT).show()
    }, {
        it.forEach { busJourney ->
            busJourneys += busJourney
            busJourney.arrivalTimesForStop.forEach {
                arrivalTime -> busStops += arrivalTime.busStop
            }
        }
    })

    fun fetchStops(
        context: Context,
        ids: Collection<String>,
        responseCallback: (Collection<BusStop>) -> Unit
    ) {
        val favouriteIds = GlobalPreferencesHelpers(context).getFavouriteStopIds()
        EnturApi.getStops(context, ids, {
            Toast.makeText(
                context,
                "Could not fetch favourite $favouriteIds",
                Toast.LENGTH_SHORT
            ).show()
        }, {
            it.forEach { busStop -> busStops += busStop }
            responseCallback(it)
        })
    }

    fun fetchNearestStops(
        context: Context,
        location: Location,
        callback: (BusStop) -> Unit
    ) {
        val favouriteIds = GlobalPreferencesHelpers(context).getFavouriteStopIds()
        EnturApi.getNearestStops(context, location, {
            Toast.makeText(
                context,
                "Could not fetch favourite $favouriteIds",
                Toast.LENGTH_SHORT
            ).show()
        }, { busStop ->
            busStops += busStop

            callback(busStop)
        })
    }

}