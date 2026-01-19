package com.aya.google_api_wapper

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchResolvedPhotoUriRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.google.android.libraries.places.api.net.SearchNearbyResponse
import kotlinx.coroutines.tasks.await


class PlaceApiWapper(context: Context, key: String) {
    var placesClient: PlacesClient
    var cb: PlaceCallback? = null
    var lastToken: AutocompleteSessionToken? = null

    init {
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(
                context,
                key, // fixme:建議用 BuildConfig
            )
        }
        placesClient = Places.createClient(context)
    }

    fun setCallback(callback: PlaceCallback) {
        cb = callback
    }

    fun autocompletePlacesAsync(input: String) {
        cb?.let {
            // todo: add para for set countries
            if (lastToken == null) {
                lastToken = AutocompleteSessionToken.newInstance()
            }
            // fixme: remove !!
            val request = getFindAutocompletePredictionsRequest(input, lastToken!!)
            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    val results = mapAutocompletePredictions(response.autocompletePredictions)
                    it.onSuccess(results)
                }
                .addOnFailureListener { e ->
                    it.onError(e)
                }
        } ?: throw MissingCallbackException()
    }

    suspend fun autocompletePlacesSync(input: String): Any {
        if (lastToken == null) {
            lastToken = AutocompleteSessionToken.newInstance()
        }

        // fixme: remove !!
        val request = getFindAutocompletePredictionsRequest(input, lastToken!!)
        val task = placesClient.findAutocompletePredictions(request).await()
        val results = mapAutocompletePredictions(task.autocompletePredictions)
        return results
    }

    private fun getFindAutocompletePredictionsRequest(
        input: String,
        token: AutocompleteSessionToken
    ): FindAutocompletePredictionsRequest {
        return FindAutocompletePredictionsRequest.builder()
            .setQuery(input)
//            .setCountries("TW")
            .setSessionToken(token)
            .build()
    }

    private fun mapAutocompletePredictions(predictions: List<AutocompletePrediction>): Any {
        val results = predictions.map { prediction ->
            mapOf(
                "placeId" to prediction.placeId,
                "description" to prediction.getFullText(null).toString(),
                "primaryText" to prediction.getPrimaryText(null).toString(),
                "secondaryText" to prediction.getSecondaryText(null).toString()
            )
        }
        return results
    }

    fun fetchPlaceDetailsAsync(placeId: String) {
        /***
         * 工作階段符記會透過下列方式終止：
         *
         * 呼叫 Place Details (新版)。
         * 系統會呼叫地址驗證。
         * 工作階段已遭捨棄。
         * ref: https://developers.google.com/maps/documentation/places/android-sdk/place-session-tokens?hl=zh-tw
         * ***/

        cb?.let {
            val fields = listOf(
                Place.Field.ID,
                Place.Field.FORMATTED_ADDRESS,
                Place.Field.LOCATION,
                Place.Field.TYPES
            )

            val request = getFetchPlaceRequest(placeId, fields, lastToken)
            placesClient.fetchPlace(request)
                .addOnSuccessListener { response ->
                    val result = mapPlaceContent(response.place)
                    it.onSuccess(result)
                }.addOnFailureListener { e ->
                    it.onError(e)
                }
            lastToken = null
        } ?: throw MissingCallbackException()
    }

    suspend fun fetchPlaceDetailsSync(placeId: String): Any {
        val fields = listOf(
            Place.Field.ID,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.LOCATION,
            Place.Field.TYPES
        )

        val request = getFetchPlaceRequest(placeId, fields, lastToken)
        val task = placesClient.fetchPlace(request).await()
        val results = mapPlaceContent(task.place)
        return results
    }

    private fun getFetchPlaceRequest(
        placeId: String,
        fields: List<Place.Field>,
        token: AutocompleteSessionToken?
    ): FetchPlaceRequest {
        val builder = FetchPlaceRequest.builder(placeId, fields)
        token?.let {
            builder.setSessionToken(it)
        }
        val request = builder.build()
        return request
    }

    private fun mapPlaceContent(place: Place): Any {
        return mapOf(
            "placeId" to place.id,
            "formattedAddress" to place.formattedAddress,
            "latLng" to place.location?.let { loc ->
                mapOf(
                    "lat" to loc.latitude,
                    "lng" to loc.longitude
                )
            },
            "types" to place.placeTypes
        )
    }

    fun searchNearbyAsync(longitude: Pair<Double, Double>, radius: Double, maxResult: Int = 20) {
        // todo: add Photo
        cb?.let {
            val placeFields = listOf(
                Place.Field.ID,
                Place.Field.LOCATION,
                Place.Field.FORMATTED_ADDRESS,
                // Nearby Search Pro SKU:
                Place.Field.BUSINESS_STATUS,
                Place.Field.DISPLAY_NAME,
//                Place.Field.PHOTO_METADATAS,// todo: 需搭配 https://developers.google.com/maps/documentation/places/android-sdk/place-photos
                // Nearby Search Enterprise SKU:
                Place.Field.USER_RATING_COUNT,
                Place.Field.RATING,
            )

            val request = getSearchNearbyRequest(longitude, radius, maxResult, placeFields)
            placesClient.searchNearby(request)
                .addOnSuccessListener(OnSuccessListener { response: SearchNearbyResponse? ->
                    // todo: parse response and format to map and return
                    response?.places?.also { places ->
                        val result = mapSearchNearbyPlaces(places)
                        it.onSuccess(result)
                    } ?: {
                        it.onError(NullResponseException())
                    }
                }).addOnFailureListener { e ->
                    it.onError(e)
                }
        } ?: throw MissingCallbackException()
    }

    suspend fun searchNearbySync(
        longitude: Pair<Double, Double>,
        radius: Double,
        maxResult: Int = 1
    ): List<Map<String, Any?>> {
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.LOCATION,
            Place.Field.FORMATTED_ADDRESS,
            // Nearby Search Pro SKU:
            Place.Field.BUSINESS_STATUS,
            Place.Field.DISPLAY_NAME,
            Place.Field.PHOTO_METADATAS,
            // Nearby Search Enterprise SKU:
            Place.Field.USER_RATING_COUNT,
            Place.Field.RATING,
        )

        val request = getSearchNearbyRequest(longitude, radius, maxResult, placeFields)
        val task = placesClient.searchNearby(request).await()
        val results =
            mapSearchNearbyPlacesWithPhoto(task.places, 1) //mapSearchNearbyPlaces(task.places)
        return results
    }

    private fun getSearchNearbyRequest(
        longitude: Pair<Double, Double>,
        radius: Double,
        maxResult: Int,
        field: List<Place.Field>
    ): SearchNearbyRequest {
        val center = LatLng(longitude.first, longitude.second)
        val circle = CircularBounds.newInstance(center,  /* radius = */radius)

        // todo: add filter as para, ref: https://developers.google.com/maps/documentation/places/android-sdk/place-types?hl=zh-tw#table-a

        // hint: Up to 50 types can be specified for each type restriction category.
        val includedTypes = FoodDrink.toListValue().subList(0, 50)
//        val excludedTypes = listOf("pizza_restaurant", "american_restaurant")

        val builder = SearchNearbyRequest.builder(circle, field)
        builder.setIncludedTypes(includedTypes)
//        builder.setExcludedTypes(excludedTypes)
        builder.setMaxResultCount(maxResult)

        val request = builder.build()
        return request
    }

    private fun mapSearchNearbyPlaces(places: List<Place>): List<Map<String, Any?>> {
        return places.map { place ->
            mapOf(
                "name" to place.displayName,
                "placeId" to place.id,
                "formattedAddress" to place.formattedAddress,
                "lat" to place.location?.latitude,
                "lng" to place.location?.longitude,
                "businessStatus" to place.businessStatus?.toString(),
                "user_ratings_total" to place.userRatingCount,
                "rating" to place.rating,
            )
        }
    }

    private suspend fun mapSearchNearbyPlacesWithPhoto(
        places: List<Place>,
        limit: Int
    ): List<Map<String, Any?>> {
        return places.map { place ->
            // todo: handel photoMetadatas is null
            val list = fetchPhotoSync(place.photoMetadatas, limit)
            mapOf(
                "name" to place.displayName,
                "placeId" to place.id,
                "formattedAddress" to place.formattedAddress,
                "lat" to place.location?.latitude,
                "lng" to place.location?.longitude,
                "photos" to list,
                "businessStatus" to place.businessStatus?.toString(),
                "user_ratings_total" to place.userRatingCount,
                "rating" to place.rating,
            )
        }
    }

    /***
     * fields trigger the Place Details Essentials SKU+...
     * ***/
    fun fetchDetailsAsync(placeId: String) {
        cb?.let {
            val fields = listOf(
                // Place Details Essentials IDs Only SKU:
                Place.Field.ID,
//            Place.Field.PHOTO_METADATAS, // todo:https://developers.google.com/maps/documentation/places/android-sdk/place-photos

                // Place Details Essentials SKU:
                Place.Field.FORMATTED_ADDRESS,
                Place.Field.LOCATION,
                Place.Field.TYPES,

                // Place Details Pro SKU:
                Place.Field.DISPLAY_NAME,

                // Place Details Enterprise SKU:
                Place.Field.INTERNATIONAL_PHONE_NUMBER,
                Place.Field.RATING,
                Place.Field.USER_RATING_COUNT,
                Place.Field.OPENING_HOURS,
                Place.Field.PRICE_LEVEL,

                // Place Details Enterprise Plus SKU:
                Place.Field.REVIEWS,
            )

            val request = getFetchPlaceRequest(placeId, fields, lastToken)

            placesClient.fetchPlace(request)
                .addOnSuccessListener { response ->

                    val result = mapPlaceContent(response.place)
                    it.onSuccess(result)

                }.addOnFailureListener { e ->
                    it.onError(e)
                }
            lastToken = null
        } ?: throw MissingCallbackException()
    }

    suspend fun fetchDetailsSync(placeId: String): Any {
        val fields = listOf(
            // Place Details Essentials IDs Only SKU:
            Place.Field.ID,
//            Place.Field.PHOTO_METADATAS, // todo:https://developers.google.com/maps/documentation/places/android-sdk/place-photos

            // Place Details Essentials SKU:
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.LOCATION,
            Place.Field.TYPES,

            // Place Details Pro SKU:
            Place.Field.DISPLAY_NAME,

            // Place Details Enterprise SKU:
            Place.Field.INTERNATIONAL_PHONE_NUMBER,
            Place.Field.RATING,
            Place.Field.USER_RATING_COUNT,
            Place.Field.OPENING_HOURS,
            Place.Field.PRICE_LEVEL,

            // Place Details Enterprise Plus SKU:
            Place.Field.REVIEWS,
        )

        val request = getFetchPlaceRequest(placeId, fields, lastToken)

        val task = placesClient.fetchPlace(request).await()
        val result = mapPlaceMoreDetails(task.place)
        return result
    }

    private fun mapPlaceMoreDetails(place: Place): Any {
        return mapOf(
            "name" to place.displayName,
            "placeId" to place.id,
            "formattedAddress" to place.formattedAddress,
            "latLng" to place.location?.let { loc ->
                mapOf(
                    "lat" to loc.latitude,
                    "lng" to loc.longitude
                )
            },
            "types" to place.placeTypes,

//            "photos" to list, //todo:
            "businessStatus" to place.businessStatus?.toString(),
            "user_ratings_total" to place.userRatingCount,
            "rating" to place.rating,
            "price_level" to place.priceLevel,
            "opening_hours" to place.openingHours, // todo: format to string
            "formatted_phone_number" to place.internationalPhoneNumber,
            "reviews" to place.reviews,  // todo: format to string
        )
    }

    fun fetchPhotoAsync(photoMetadata: PhotoMetadata) {
        // todo: set setMaxHeight() or setMaxWidth()
        cb?.let {
            val req = FetchResolvedPhotoUriRequest.builder(photoMetadata).build()
            placesClient.fetchResolvedPhotoUri(req).addOnSuccessListener { response ->
                response.uri?.let { uri -> it.onSuccess(uri) } ?: it.onError(NullPhotoException())
            }.addOnFailureListener { exception ->
                it.onError(exception)
            }
        } ?: throw MissingCallbackException()
    }

    suspend fun fetchPhotoSync(photoMetadatas: List<PhotoMetadata>, limit: Int): List<String> {
        // todo: set setMaxHeight() or setMaxWidth()
        val uriList = mutableListOf<String>()
        val count = photoMetadatas.size.takeIf { n -> n < limit } ?: limit
        for (i in 0 until count) {
            val req = FetchResolvedPhotoUriRequest.builder(photoMetadatas[i]).build()
            val task = placesClient.fetchResolvedPhotoUri(req).await()
            task.uri?.let {
                uriList.add(it.toString())
            }
        }
        return uriList
    }
}