package com.garasje.atbetter.core

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class UpcomingBus(
    override val id: String,
    override val busName: String,
    override val busNumber: String,
    var arrivesAt: LocalDateTime,
    var realTime: Boolean
) : Bus(id, busName, busNumber) {

    fun relativeArrivesAt(): Int {
        return ChronoUnit.MINUTES.between(LocalDateTime.now(), arrivesAt).toInt()

    }
}