package com.garasje.atbetter.core

import android.util.Log

class BusRegister {
    private val register: MutableCollection<BusJourney> = ArrayList()
    private val stopListeners = HashMap<BusStop, MutableCollection<BusJourneysCallback>>()

    private fun updateBus(bus: BusJourney) {
        val existingBus = register.find { it == bus }
        if (existingBus != null) {
            existingBus.busLine = bus.busLine
            existingBus.arrivalTimesForStop = bus.arrivalTimesForStop
        } else {
            register += bus
        }
        fireChangeForBusStop(bus)
    }

    private fun getJourneysForStop(busStop: BusStop) =
        register.filter { it.arrivalTimesForStop.map { time -> time.busStop }.contains(busStop) }

    fun subscribeToBusStop(busStop: BusStop, onChange: BusJourneysCallback) {
        val stopSpecificListeners = stopListeners[busStop] ?: run {
            val listeners: MutableCollection<BusJourneysCallback> = ArrayList()
            stopListeners[busStop] = listeners
            listeners
        }

        stopSpecificListeners += onChange
        onChange(register.filter { busJourney ->
            busJourney.arrivalTimesForStop
                .map { it.busStop }
                .contains(busStop)
        })
    }

    private fun fireChangeForBusStop(bus: BusJourney) = bus.arrivalTimesForStop
        .map { it.busStop }
        .forEach { stop ->
            Log.println(Log.INFO, null, "✅ HEEERRR ${stop.id}")
            stopListeners[stop]?.forEach { fire ->
                fire(getJourneysForStop(stop))
            }
        }

    operator fun plusAssign(bus: BusJourney) = updateBus(bus)

}