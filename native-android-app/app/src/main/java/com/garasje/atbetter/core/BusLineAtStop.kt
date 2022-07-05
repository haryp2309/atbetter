package com.garasje.atbetter.core

import android.util.Log
import java.time.LocalDateTime
import java.time.ZoneOffset


data class BusLineAtStopOld(
    val busLine: BusLine,
    val busStop: BusStop,
    val busses: Collection<BusJourney>,
) {
    val sortedArrivesAt: List<ArrivalTime>
        get() = busses
            .mapNotNull { bus -> bus.arrivalTimesForStop.find { time -> time.busStop == busStop } }
            .filter { arrivalTime -> arrivalTime.arrivesAt.isAfter(LocalDateTime.now()) }
            .sorted()

    val currentlyAt: BusStop
        get() {
            val nowSeconds = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            val busJourney =
                busses.minByOrNull {
                    val arrivalTimeForThisStop = it.arrivalTimesForStop.find { stop ->
                        Log.println(Log.INFO, null, "🤩 ${stop.busStop.id} ${stop.busStop.name}")
                        Log.println(Log.INFO, null, "🤩 -> ${busStop.id} ${busStop.name}")
                        stop.busStop == busStop
                    }
                    requireNotNull(arrivalTimeForThisStop)
                    arrivalTimeForThisStop.arrivesAt
                }
            requireNotNull(busJourney)
            return busJourney
                .arrivalTimesForStop
                .reduce { arrivesAt1, arrivesAt2 ->
                    val unixTime1 = arrivesAt1.arrivesAt.toEpochSecond(ZoneOffset.UTC) - nowSeconds
                    val unixTime2 = arrivesAt2.arrivesAt.toEpochSecond(ZoneOffset.UTC) - nowSeconds

                    when {
                        unixTime1 < 0 -> arrivesAt1
                        unixTime2 < 0 -> arrivesAt2
                        unixTime1 < unixTime2 -> arrivesAt1
                        else -> arrivesAt2
                    }
                }
                .busStop
        }

}