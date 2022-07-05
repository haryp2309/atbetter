package com.garasje.atbetter.core

import java.time.LocalDateTime

data class ArrivalTime(val arrivesAt: LocalDateTime, val realTime: Boolean, val busStop: BusStop) : Comparable<ArrivalTime> {

    override fun compareTo(other: ArrivalTime): Int = arrivesAt.compareTo(other.arrivesAt)

}