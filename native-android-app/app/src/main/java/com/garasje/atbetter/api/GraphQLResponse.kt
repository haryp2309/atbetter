package com.garasje.atbetter.api

import org.json.JSONObject

class GraphQLResponse(responseBody: JSONObject?) {

    val data = responseBody?.optJSONObject("data")

}