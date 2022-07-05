package com.garasje.atbetter.core

import android.location.Location

data class BusStop(val name: String, val id: String, val location: Location) {
    /*override fun equals(other: Any?): Boolean {
        if (other is BusStop) {
            return other.id == this.id
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }*/
}