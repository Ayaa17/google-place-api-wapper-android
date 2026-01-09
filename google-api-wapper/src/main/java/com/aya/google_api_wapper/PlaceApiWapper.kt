package com.aya.google_api_wapper

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.google.android.libraries.places.api.net.SearchNearbyResponse


class PlaceApiWapper(context: Context, key: String, callback: PlaceCallback) {
    var placesClient: PlacesClient
    var cb: PlaceCallback
    var lastToken: AutocompleteSessionToken? = null

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

    fun autocompletePlaces(input: String) {
        // todo: add para for set countries
        if (lastToken == null) {
            lastToken = AutocompleteSessionToken.newInstance()
        }
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(input)
//            .setCountries("TW")
            .setSessionToken(lastToken)
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

    fun fetchPlaceDetails(placeId: String) {
        /***
         * 工作階段符記會透過下列方式終止：
         *
         * 呼叫 Place Details (新版)。
         * 系統會呼叫地址驗證。
         * 工作階段已遭捨棄。
         * ref: https://developers.google.com/maps/documentation/places/android-sdk/place-session-tokens?hl=zh-tw
         * ***/

        val fields = listOf(
            Place.Field.ID,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.LOCATION,
            Place.Field.TYPES
        )

        val builder = FetchPlaceRequest.builder(placeId, fields)

        lastToken?.let {
            builder.setSessionToken(it)
        }

        val request = builder.build()

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->

                val place = response.place

                val result = mapOf(
                    "placeId" to place.id,
                    "address" to place.formattedAddress,
                    "latLng" to place.location?.let {
                        mapOf(
                            "lat" to it.latitude,
                            "lng" to it.longitude
                        )
                    },
                    "types" to place.placeTypes
                )
                cb.onSuccess(result)

            }.addOnFailureListener { e ->
                cb.onError(e)
            }
        lastToken = null
    }

    fun searchNearby(longitude: Pair<Double, Double>, radius: Double, maxResult: Int = 20) {

        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.DISPLAY_NAME,
            Place.Field.LOCATION,
//            Place.Field.PHOTO_METADATAS, // todo: 需搭配 https://developers.google.com/maps/documentation/places/android-sdk/place-photos
            Place.Field.BUSINESS_STATUS,
            Place.Field.FORMATTED_ADDRESS,
        )

        val center = LatLng(longitude.first, longitude.second)
        val circle = CircularBounds.newInstance(center,  /* radius = */radius)

        // todo: add filter as para, ref: https://developers.google.com/maps/documentation/places/android-sdk/place-types?hl=zh-tw#table-a

        // hint: Up to 50 types can be specified for each type restriction category.
        val includedTypes = FoodDrink.toListValue().subList(0, 50)
//        val excludedTypes = listOf("pizza_restaurant", "american_restaurant")

        val builder = SearchNearbyRequest.builder(circle, placeFields)
        builder.setIncludedTypes(includedTypes)
//        builder.setExcludedTypes(excludedTypes)
        builder.setMaxResultCount(maxResult)

        val request = builder.build()
        placesClient.searchNearby(request)
            .addOnSuccessListener(OnSuccessListener { response: SearchNearbyResponse? ->
                // todo: parse response and format to map and return
                response?.getPlaces()?.also { places ->
                    val result = places.map { place ->
                        mapOf(
                            "name" to place.displayName,
                            "placeId" to place.id,
                            "address" to place.formattedAddress,
                            "lat" to place.location?.latitude,
                            "lng" to place.location?.longitude,
                            "businessStatus" to place.businessStatus,
                        )
                    }
                    cb.onSuccess(result)
                }
                //todo: abstract Exception
                cb.onError(Exception("PlaceApiWapper: searchNearby get NULL response."))

            }).addOnFailureListener { e ->
                cb.onError(e)
            }

    }

}