package com.garasje.atbetter.core

import java.util.*
import java.util.stream.Collectors
import kotlin.streams.toList


data class UpcomingBus(
    val busLine: BusLine,
    val busStop: BusStop,
    val busses: Collection<Bus>,
) {
    val sortedArrivesAt: List<ArrivalTime>
    get() = busses.stream()
        .map { it.arrivalTimes.find { time -> time.busStop == busStop } }
        .sorted()
        .toList()
        .filterNotNull()

}