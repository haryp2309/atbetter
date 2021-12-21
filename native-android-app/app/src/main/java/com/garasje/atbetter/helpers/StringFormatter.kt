package com.garasje.atbetter.helpers

import android.content.Context
import com.garasje.atbetter.R

object StringFormatter {
    fun distanceToFormattedDistance(context: Context, distanceInMeters: Int): String {
        if (distanceInMeters > 400) {
            val distanceInKm = distanceInMeters.toFloat() / 1000
            val distanceAsString = String.format("%.1f", distanceInKm)
            return context.getString(R.string.x_kilometers_short, distanceAsString)
        }
        return context.getString(R.string.x_meters_short, distanceInMeters)

    }
}