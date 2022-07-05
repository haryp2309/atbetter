package com.garasje.atbetter.core

open class BusLine(open val busName: String, open val busNumber: String) {
    val id: String
        get() = "$busNumber|$busName"

    override fun equals(other: Any?): Boolean {

        if (other is BusLine) {
            return this.id == other.id
        }

        return super.equals(other)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}