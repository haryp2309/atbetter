package com.garasje.atbetter.api

import android.content.Context
import android.location.Location
import com.android.volley.Response
import com.garasje.atbetter.core.*
import com.garasje.atbetter.ui.GlobalState
import java.time.ZonedDateTime

object EnturApi {

    private const val JOURNEY_V2_GRAPHQL_API_URL = "https://api.entur.io/journey-planner/v2/graphql"
    private const val JOURNEY_V3_GRAPHQL_API_URL = "https://api.entur.io/journey-planner/v3/graphql"

    private const val JOURNEY_PROVIDER = "ENTUR_JOURNEY_API"


    fun getNearestStops(
        context: Context, location: Location,
        errorCallback: Response.ErrorListener,
        successCallback: (busStops: BusStop) -> Unit
    ) {

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

        RequestHelper.queryGraphQL(
            context,
            JOURNEY_V2_GRAPHQL_API_URL,
            query,
            errorCallback
        ) { response ->

            val stops = response
                .getJSONObject("data")
                .getJSONObject("nearest")
                .getJSONArray("edges")



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

        }

    }


    fun getStops(
        context: Context,
        ids: Collection<String>,
        errorCallback: Response.ErrorListener,
        successCallback: (busStops: Collection<BusStop>) -> Unit,
    ) {

        val formattedIds = "\"" + ids.reduce { acc, s -> "$acc\", \"$s" } + "\""

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

        RequestHelper.queryGraphQL(
            context,
            JOURNEY_V3_GRAPHQL_API_URL,
            query,
            errorCallback
        ) { response ->

            val stopPlacesResponse = response
                .getJSONObject("data")
                .getJSONArray("stopPlaces")

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
        }
    }

    /*fun getBusStopTimetable(
        context: Context,
        busStop: BusStop,
        errorCallback: Response.ErrorListener,
        successCallback: (busLineAtStopStops: Collection<BusLineAtStop>) -> Unit,
    ) {
        val query = """
            {
              stopPlace(
                id: "${busStop.id}"
              ) {
                estimatedCalls(
                  numberOfDepartures: 100,
                  numberOfDeparturesPerLineAndDestinationDisplay: 5
                ) {
                  serviceJourney{
                    id
                    line {
                      publicCode
                      id
                    }
                    
                    estimatedCalls {
                      expectedArrivalTime
                      quay {
                        stopPlace {
                            id
                        }
                        name
                        longitude
                        latitude
                      }
                      realtime
                    }
                  }
                  destinationDisplay {
                    frontText
                  }
                }
              }
            }
        """
        RequestHelper.queryGraphQL(
            context,
            JOURNEY_V3_GRAPHQL_API_URL,
            query,
            errorCallback
        ) { response ->

            val estimatedCalls = response
                .getJSONObject("data")
                .getJSONObject("stopPlace")
                .getJSONArray("estimatedCalls")


            val bussesAndArrivals = (0 until estimatedCalls.length())
                .map { i -> estimatedCalls.getJSONObject(i) }
                .map { estimatedCall ->
                    val frontText = estimatedCall
                        .getJSONObject("destinationDisplay")
                        .getString("frontText")
                    val publicCode = estimatedCall
                        .getJSONObject("serviceJourney")
                        .getJSONObject("line")
                        .getString("publicCode")
                    val id = frontText + estimatedCall
                        .getJSONObject("serviceJourney")
                        .getJSONObject("line")
                        .getString("id")
                    val journeyId = estimatedCall
                        .getJSONObject("serviceJourney")
                        .getString("id")

                    val busLine = BusLine(frontText, publicCode)

                    val estimatedCallsOfAllStops = estimatedCall
                        .getJSONObject("serviceJourney")
                        .getJSONArray("estimatedCalls")
                    val expectedArrivalOfAllStops = (0 until estimatedCallsOfAllStops.length())
                        .map { estimatedCallsOfAllStops.getJSONObject(it) }
                        .map { estimatedCallForAnotherStop ->
                            val expectedArrivalTimeStringForAnotherStop =
                                estimatedCallForAnotherStop
                                    .getString("expectedArrivalTime")
                            val expectedArrivalTimeForAnotherStop =
                                ZonedDateTime.parse(expectedArrivalTimeStringForAnotherStop)
                                    .toLocalDateTime()

                            val isRealTimeForAnotherStop =
                                estimatedCallForAnotherStop.getBoolean("realtime")

                            val quay = estimatedCallForAnotherStop
                                .getJSONObject("quay")
                            val busStopName = quay
                                .getString("name")
                            val busStopId = quay
                                .getJSONObject("stopPlace")
                                .getString("id")
                            val busStopLocation = Location(JOURNEY_PROVIDER)
                            busStopLocation.latitude = quay
                                .getDouble("latitude")
                            busStopLocation.longitude = quay
                                .getDouble("longitude")
                            val stop = BusStop(busStopId, busStopName, busStopLocation)
                            ArrivalTime(
                                expectedArrivalTimeForAnotherStop,
                                isRealTimeForAnotherStop,
                                stop
                            )
                        }

                    val bus = BusJourney(journeyId, busLine, expectedArrivalOfAllStops)



                    Pair(bus, busLine)
                }

            val upcomingBusses = bussesAndArrivals
                .map { it.second }
                .distinct()
                .map { bus ->
                    val busses = bussesAndArrivals
                        .filter { (_, tmpBusObj) -> tmpBusObj == bus }
                        .map { it.first }

                    BusLineAtStop(bus, busStop, busses)
                }

            successCallback(upcomingBusses)
        }
    }*/

    fun fetchBusStopTimetable(
        context: Context,
        busStop: BusStop,
        errorCallback: Response.ErrorListener,
        successCallback: (Collection<BusJourney>) -> Unit,
    ) {
        val query = """
            {
              stopPlace(
                id: "${busStop.id}"
              ) {
                estimatedCalls(
                  numberOfDepartures: 100,
                  numberOfDeparturesPerLineAndDestinationDisplay: 5
                ) {
                  serviceJourney{
                    id
                    line {
                      publicCode
                      id
                    }
                    
                    estimatedCalls {
                      expectedArrivalTime
                      quay {
                        stopPlace {
                            id
                            parent {
                              id
                            }
                        }
                        name
                        longitude
                        latitude
                      }
                      realtime
                    }
                  }
                  destinationDisplay {
                    frontText
                  }
                }
              }
            }
        """

        RequestHelper.queryGraphQL(
            context,
            JOURNEY_V3_GRAPHQL_API_URL,
            query,
            errorCallback
        ) { response ->

            val estimatedCalls = response
                .getJSONObject("data")
                .getJSONObject("stopPlace")
                .getJSONArray("estimatedCalls")


            val busJourneys = (0 until estimatedCalls.length())
                .map { i -> estimatedCalls.getJSONObject(i) }
                .map { estimatedCall ->
                    val frontText = estimatedCall
                        .getJSONObject("destinationDisplay")
                        .getString("frontText")
                    val publicCode = estimatedCall
                        .getJSONObject("serviceJourney")
                        .getJSONObject("line")
                        .getString("publicCode")
                    val id = frontText + estimatedCall
                        .getJSONObject("serviceJourney")
                        .getJSONObject("line")
                        .getString("id")
                    val journeyId = estimatedCall
                        .getJSONObject("serviceJourney")
                        .getString("id")

                    val busLine = BusLine(frontText, publicCode)

                    val estimatedCallsOfAllStops = estimatedCall
                        .getJSONObject("serviceJourney")
                        .getJSONArray("estimatedCalls")
                    val expectedArrivalOfAllStops = (0 until estimatedCallsOfAllStops.length())
                        .map { estimatedCallsOfAllStops.getJSONObject(it) }
                        .map { estimatedCallForAnotherStop ->
                            val expectedArrivalTimeStringForAnotherStop =
                                estimatedCallForAnotherStop
                                    .getString("expectedArrivalTime")
                            val expectedArrivalTimeForAnotherStop =
                                ZonedDateTime.parse(expectedArrivalTimeStringForAnotherStop)
                                    .toLocalDateTime()

                            val isRealTimeForAnotherStop =
                                estimatedCallForAnotherStop.getBoolean("realtime")

                            val quay = estimatedCallForAnotherStop
                                .getJSONObject("quay")
                            val busStopName = quay
                                .getString("name")
                            val stopPlace = quay.getJSONObject("stopPlace")
                            val busStopId = if (stopPlace.isNull("parent"))
                                stopPlace.getString("id")
                            else stopPlace.getJSONObject("parent").getString("id")

                            val busStopLocation = Location(JOURNEY_PROVIDER)
                            busStopLocation.latitude = quay
                                .getDouble("latitude")
                            busStopLocation.longitude = quay
                                .getDouble("longitude")
                            val stop = BusStop(busStopName, busStopId, busStopLocation)
                            ArrivalTime(
                                expectedArrivalTimeForAnotherStop,
                                isRealTimeForAnotherStop,
                                stop
                            )
                        }

                    BusJourney(journeyId, busLine, expectedArrivalOfAllStops)

                }

            successCallback(busJourneys)
        }
    }
}