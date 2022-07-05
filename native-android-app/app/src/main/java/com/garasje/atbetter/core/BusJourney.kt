package com.garasje.atbetter.core

class BusJourney(
    val journeyId: String,
    busLine: BusLine,
    arrivalTimesForStop: Collection<ArrivalTime>
) {
    private val listeners: MutableCollection<(BusJourney) -> Unit> = ArrayList()

    var busLine: BusLine = busLine
        set(value) {
            fireChange()
            field = value
        }

    var arrivalTimesForStop: Collection<ArrivalTime> = arrivalTimesForStop
        set(value) {
            fireChange()
            field = value
        }

    fun onChange(callback: (BusJourney) -> Unit) {
        listeners += callback
        callback(this)
    }

    private fun fireChange() {
        listeners.forEach { callback -> callback(this) }
    }

    override fun equals(other: Any?): Boolean =
        if (other is BusJourney) other.journeyId == this.journeyId else super.equals(other)

    override fun hashCode(): Int {
        return journeyId.hashCode()
    }
}