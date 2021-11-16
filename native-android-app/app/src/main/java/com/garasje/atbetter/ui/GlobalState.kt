package com.garasje.atbetter.ui

import com.garasje.atbetter.core.BusStop

object GlobalState {
    private val busStops = HashMap<String, BusStop>()

    fun getBusStop(id: String): BusStop? {
        return busStops[id]
    }

    fun addBusStop(busStop: BusStop) {
        busStops[busStop.id] = busStop
    }
}