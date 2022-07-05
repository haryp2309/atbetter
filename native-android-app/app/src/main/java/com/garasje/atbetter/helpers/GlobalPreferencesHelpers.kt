package com.garasje.atbetter.helpers

import android.content.Context
import android.widget.Toast
import com.garasje.atbetter.constants.GlobalPreferences
import org.json.JSONArray

class GlobalPreferencesHelpers(val context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences(GlobalPreferences.GLOBAL_PREFERENCES, Context.MODE_PRIVATE)

    fun getFavouriteStopIds(): Collection<String> {

        val favouritesSerialized =
            sharedPreferences.getString(GlobalPreferences.FAVORITE_STOPS, "[]")
        val favouritesJSONArray = JSONArray(favouritesSerialized)
        var favoriteIds: Collection<String> = ArrayList()
        for (index in 0 until favouritesJSONArray.length()) {
            favoriteIds += favouritesJSONArray.getString(index)
        }
        return ArrayList(favoriteIds)

    }

    fun toggleFavourite(busStopId: String) {
        val favouritesSerialized =
            sharedPreferences.getString(GlobalPreferences.FAVORITE_STOPS, "[]")
        val favourites = JSONArray(favouritesSerialized)
        var alreadyFavIndex = -1
        for (index in 0 until favourites.length()) {
            if (favourites.get(index) == busStopId) {
                alreadyFavIndex = index
                break
            }
        }
        if (alreadyFavIndex == -1) {
            favourites.put(busStopId)
            Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show()
        } else {
            favourites.remove(alreadyFavIndex)
            Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show()
        }
        val editor = sharedPreferences.edit()
        editor.putString(GlobalPreferences.FAVORITE_STOPS, favourites.toString())
        editor.apply()
    }

}