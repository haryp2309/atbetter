package com.garasje.atbetter.core

import android.location.Location

class StopRegister {
    private val register: MutableList<BusStop> = ArrayList()
    private val listeners: MutableCollection<BusStopCallback> = ArrayList()

    val size: Int
        get() = register.size

    fun getBusStop(id: String): BusStop? = this.register.find { it.id == id }

    fun addBusStop(busStop: BusStop): Boolean =
        if (getBusStop(busStop.id) == null) {
            register += busStop
            fireChangeForBusStop()
            true
        } else false

    fun subscribeToBusStop(onChange: BusStopCallback) {
        listeners += onChange
        onChange(register)
    }

    private fun fireChangeForBusStop() = listeners.forEach { listener -> listener(register) }

    operator fun plusAssign(busStop: BusStop) {
        addBusStop(busStop)
    }

    operator fun get(index: Int) = register[index]

    fun sortByLocation(location: Location) {
        register.sortBy { it.location.distanceTo(location) }
    }

    fun sortByName() {
        register.sortBy { it.name }
    }

    fun indexOf(busStop: BusStop) = register.indexOf(busStop)
    fun removeAt(index: Int) = register.removeAt(index)

    fun copy() = register.toList()

}