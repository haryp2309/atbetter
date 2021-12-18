package com.garasje.atbetter.helpers

import android.content.Context

object DisplayHelpers {
    fun dimenInPx(dimen: Int, context: Context): Int {
        return context.resources.getDimension(dimen).toInt()
    }

}