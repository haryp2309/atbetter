package com.garasje.atbetter.api

import android.content.Context
import android.location.Location
import android.widget.Toast
import com.android.volley.Response
import com.garasje.atbetter.core.BusStop
import com.garasje.atbetter.core.UpcomingBus
import org.json.JSONObject
import java.lang.Error
import java.time.LocalDateTime
import java.time.ZonedDateTime

object EnturApi {

    val API_BASE_URL = "https://api.entur.io/stop-places/v1/read"
    val GRAPHQL_BASE_URL = "https://api.entur.io/journey-planner/v2/graphql"


    fun getNearestStops(
        context: Context, location: Location, successCallback: (busStops: BusStop) -> Unit,
        errorCallback: Response.ErrorListener
    ) {
        val reqBody = JSONObject()
        reqBody.put(
            "query",
            """
                {
                    nearest(
                        latitude: ${location.latitude},
                        longitude: ${location.longitude},
                        maximumDistance: 2000,
                        maximumResults: 20,
                        filterByPlaceTypes: [
                            stopPlace,
                        ],
                        filterByModes: bus,
                        filterByInUse: false,
                        multiModalMode: parent
                    ) {
                        edges {
                            node {
                                distance
                                place {
                                    __typename
                                    id
                                    latitude
                                    longitude
                                }
                            }
                        }
                    }
                }
            """.trimIndent()
        )

        RequestHelper.post(context, GRAPHQL_BASE_URL, reqBody, { response ->

            val stops = response
                .getJSONObject("data")
                .getJSONObject("nearest")
                .getJSONArray("edges")



            for (i in 0 until stops.length()) {
                val stopId = stops.getJSONObject(i)
                    .getJSONObject("node")
                    .getJSONObject("place")
                    .getString("id")

                getStop(context, stopId, {
                    val name = it.getJSONObject("name").getString("value")
                    val busStop = BusStop(name, stopId)
                    successCallback(busStop)

                }, errorCallback)
            }

        }, errorCallback)

    }

    fun getStop(
        context: Context, id: String, successCallback: Response.Listener<JSONObject>,
        errorCallback: Response.ErrorListener
    ) {
        val url = "https://api.entur.io/stop-places/v1/read/stop-places/${id}?"
        RequestHelper.get(context, url, successCallback, errorCallback)
    }

    fun getUpcomingBusses(
        context: Context,
        busStopId: String,
        successCallback: (upcomingBusses: Collection<UpcomingBus>) -> Unit,
        errorCallback: Response.ErrorListener
    ) {
        val reqObject = JSONObject()
        reqObject.put(
            "query", """
            {
              stopPlaces(
                ids: ["$busStopId"]
              ) {
                id
                name
                estimatedCalls(
                  startTime: "${LocalDateTime.now()}"
                ) {
                  destinationDisplay {
                    frontText
                  }
                  aimedArrivalTime
                  realtime
                  serviceJourney {
                    line {
                      id
                      publicCode
                    }
                  }
                }
              }
            }

        """.trimIndent()
        )

        RequestHelper.post(context, GRAPHQL_BASE_URL, reqObject, {

            val estimatedCalls = it
                .getJSONObject("data")
                .getJSONArray("stopPlaces")
                .getJSONObject(0)
                .getJSONArray("estimatedCalls")

            val upcomingBusses = ArrayList<UpcomingBus>()

            for (busIndex in 0 until estimatedCalls.length()) {
                val bus = estimatedCalls.getJSONObject(busIndex)
                val busName = bus
                    .getJSONObject("destinationDisplay")
                    .getString("frontText")
                val busNumber = bus
                    .getJSONObject("serviceJourney")
                    .getJSONObject("line")
                    .getString("publicCode")
                val id = busName+bus
                    .getJSONObject("serviceJourney")
                    .getJSONObject("line")
                    .getString("id")

                val realtime = bus.getBoolean("realtime")
                val arrivesAtString = bus.getString("aimedArrivalTime")
                val cleanedArrivesAtString = "" +
                    arrivesAtString.subSequence(0, 22) + ":" + arrivesAtString.subSequence(
                        22,
                        arrivesAtString.length
                    )
                val arrivesAt = ZonedDateTime.parse(cleanedArrivesAtString).toLocalDateTime()

                upcomingBusses += UpcomingBus(id, busName, busNumber, arrivesAt, realtime)

            }

            successCallback(upcomingBusses)
        }, errorCallback)
    }
}