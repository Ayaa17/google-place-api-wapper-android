package com.aya.google_api_wapper

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient

class PlaceApiWapper(context: Context, key: String, callback: PlaceCallback) {
    var placesClient: PlacesClient
    var cb: PlaceCallback

    init {
        if (!Places.isInitialized()) {
            Places.initialize(
                context,
                key // fixme:建議用 BuildConfig
            )
        }
        placesClient = Places.createClient(context)
        cb = callback
    }

    fun autocompletePlaces(
        input: String,
    ) {
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(input)
            .setCountries("TW")
            .setSessionToken(token)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val results = response.autocompletePredictions.map { prediction ->
                    mapOf(
                        "placeId" to prediction.placeId,
                        "description" to prediction.getFullText(null).toString(),
                        "primaryText" to prediction.getPrimaryText(null).toString(),
                        "secondaryText" to prediction.getSecondaryText(null).toString()
                    )
                }
                cb.onSuccess(results)
            }
            .addOnFailureListener { e ->
                cb.onError(e)
            }
    }

}