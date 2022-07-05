package com.garasje.atbetter.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

object RequestHelper {

    fun get(
        context: Context,
        url: String,
        successCallback: Response.Listener<JSONObject>,
        errorCallback: Response.ErrorListener
    ) {

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)

        // Request a string response from the provided URL.
        val stringRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            successCallback,
            errorCallback
        )

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun post(
        context: Context,
        url: String,
        body: JSONObject,
        successCallback: Response.Listener<JSONObject>,
        errorCallback: Response.ErrorListener
    ) {

        val queue = Volley.newRequestQueue(context)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, body,
            successCallback,
            errorCallback
        )

        queue.add(jsonObjectRequest)

    }

    fun queryGraphQL(
        context: Context,
        url: String, query: String,
        errorCallback: Response.ErrorListener,
        successCallback: Response.Listener<JSONObject>,
    ) {

        val reqBody = JSONObject()
        reqBody.put("query", query.trimIndent())

        post(context, url, reqBody, successCallback, errorCallback)

    }

}