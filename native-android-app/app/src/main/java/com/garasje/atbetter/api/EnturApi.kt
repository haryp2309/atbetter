package com.garasje.atbetter.api

import android.content.Context
import android.location.Location
import com.android.volley.Response
import com.garasje.atbetter.core.BusStop
import org.json.JSONObject

class EnturApi {

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

        RequestHelper().post(context, GRAPHQL_BASE_URL, reqBody, { response ->

            val stops = response
                .getJSONObject("data")
                .getJSONObject("nearest")
                .getJSONArray("edges")



            for (i in 0 until stops.length()) {
                val stopId = stops.getJSONObject(i)
                    .getJSONObject("node")
                    .getJSONObject("place")
                    .getString("id")

                EnturApi().getStop(context, stopId, {
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
        RequestHelper().get(context, url, successCallback, errorCallback)
    }
}