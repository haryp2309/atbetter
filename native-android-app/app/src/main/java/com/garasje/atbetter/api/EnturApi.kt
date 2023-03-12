package com.garasje.atbetter.api

import android.content.Context
import android.location.Location
import com.android.volley.Response
import com.apollographql.apollo3.ApolloClient
import com.garasje.atbetter.NearestStopsQuery
import com.garasje.atbetter.core.BusStop
import com.garasje.atbetter.core.UpcomingBus
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.ZonedDateTime

object EnturApi {

    private const val JOURNEY_V2_GRAPHQL_API_URL = "https://api.entur.io/journey-planner/v2/graphql"
    private const val JOURNEY_V3_GRAPHQL_API_URL = "https://api.entur.io/journey-planner/v3/graphql"

    private const val JOURNEY_PROVIDER = "ENTUR_JOURNEY_API"


    suspend fun getNearestStops(
        context: Context, location: Location, successCallback: (busStops: BusStop) -> Unit,
        errorCallback: Response.ErrorListener
    ) {

        // TODO

        val apolloClient = ApolloClient.Builder()
            .serverUrl(JOURNEY_V3_GRAPHQL_API_URL)
            .build()

        val response = apolloClient
            .query(NearestStopsQuery(location.latitude, location.longitude))
            .execute()

        val stops = response.data?.nearest?.edges?.toList() ?: ArrayList()

        // TODO



        val query =
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
            """

        RequestHelper.queryGraphQL(context, JOURNEY_V2_GRAPHQL_API_URL, query, { response ->

            val stops = response.data
                ?.getJSONObject("nearest")
                ?.getJSONArray("edges")
                ?: JSONArray()

            for (i in 0 until stops.length()) {
                val place = stops.getJSONObject(i)
                    .getJSONObject("node")
                    .getJSONObject("place")
                val stopId = place.getString("id")

                val stopLocation = Location(JOURNEY_PROVIDER)
                stopLocation.latitude = place.getDouble("latitude")
                stopLocation.longitude = place.getDouble("longitude")

                val url = "https://api.entur.io/stop-places/v1/read/stop-places/${stopId}?"
                RequestHelper.get(context, url, {
                    val name = it.getJSONObject("name").getString("value")
                    val busStop = BusStop(name, stopId, stopLocation)
                    successCallback(busStop)
                }, errorCallback)
            }

        }, errorCallback)

    }


    fun getStops(
        context: Context,
        ids: Collection<String>,
        successCallback: (busStops: Collection<BusStop>) -> Unit,
        errorCallback: Response.ErrorListener
    ) {

        val formattedIds = "\"" + (ids.reduceOrNull { acc, s -> "$acc\", \"$s" } ?: "") + "\""

        val query = """
            {
              stopPlaces(ids: [${formattedIds}]) {
                id
                name
                latitude
                longitude
              }
            }
        """

        RequestHelper.queryGraphQL(context, JOURNEY_V3_GRAPHQL_API_URL, query, { response ->

            val stopPlacesResponse = response.data
                ?.getJSONArray("stopPlaces")
                ?: JSONArray()

            val busStops = ArrayList<BusStop>()

            for (i in 0 until stopPlacesResponse.length()) {
                val place = stopPlacesResponse.getJSONObject(i)

                val stopId = place.getString("id")
                val name = place.getString("name")

                val busStop = BusStop(name, stopId, Location(JOURNEY_PROVIDER))

                busStop.location.latitude = place.getDouble("latitude")
                busStop.location.longitude = place.getDouble("longitude")

                busStops += busStop
            }

            successCallback(busStops)
        }, errorCallback)
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

        RequestHelper.post(context, JOURNEY_V2_GRAPHQL_API_URL, reqObject, {

            val estimatedCalls = it
                ?.getJSONObject("data")
                ?.getJSONArray("stopPlaces")
                ?.getJSONObject(0)
                ?.getJSONArray("estimatedCalls")
                ?: JSONArray()

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
                val id = busName + bus
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