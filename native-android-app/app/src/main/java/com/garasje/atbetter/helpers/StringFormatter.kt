package com.garasje.atbetter.helpers

import android.content.Context
import com.garasje.atbetter.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

object StringFormatter {
    fun distanceToFormattedDistance(context: Context, distanceInMeters: Int): String {
        if (distanceInMeters > 400) {
            val distanceInKm = distanceInMeters.toFloat() / 1000
            val distanceAsString = String.format("%.1f", distanceInKm)
            return context.getString(R.string.x_kilometers_short, distanceAsString)
        }
        return context.getString(R.string.x_meters_short, distanceInMeters)

    }

    fun timeToRelativeTime(context: Context, arrivesAt: LocalDateTime): String {
        val relativeMinutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), arrivesAt).toInt()

        if (relativeMinutes > 10) {
            return arrivesAt.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        }

        return context.getString(R.string.x_relative_time_in_minutes, relativeMinutes)
    }
}