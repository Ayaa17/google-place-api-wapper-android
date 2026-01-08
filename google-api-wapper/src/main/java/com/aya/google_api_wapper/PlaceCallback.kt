package com.aya.google_api_wapper

interface PlaceCallback {
    fun onSuccess(response: Any): Int
    fun onError(error: Throwable): Int
}