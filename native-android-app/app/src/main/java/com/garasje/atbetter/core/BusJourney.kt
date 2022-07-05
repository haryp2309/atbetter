package com.garasje.atbetter.core

data class Bus(
    val journeyId: String,
    val busLine: BusLine,
    val arrivalTimes: Collection<ArrivalTime>
)