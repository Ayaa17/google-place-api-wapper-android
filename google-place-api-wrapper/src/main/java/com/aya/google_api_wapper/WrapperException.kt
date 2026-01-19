package com.aya.google_api_wapper

open class WrapperException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)


// 2️⃣ Common Sub Exceptions

// Network related errors
class NetworkException(
    message: String? = "Network error",
    cause: Throwable? = null
) : WrapperException(message, cause)

// API returned an error
class ApiException(
    val code: Int,
    message: String? = "API error",
    cause: Throwable? = null
) : WrapperException(message, cause)

// Invalid parameter / illegal call
class InvalidArgumentException(
    message: String? = "Invalid argument"
) : WrapperException(message)

// Task / operation cancelled
class CancelledException(
    message: String? = "Operation cancelled"
) : WrapperException(message)

// Unauthorized / permission error
class UnauthorizedException(
    message: String? = "Unauthorized"
) : WrapperException(message)

// Unknown / unexpected errors
class UnknownException(
    message: String? = "Unknown error",
    cause: Throwable? = null
) : WrapperException(message)


// Unknown / unexpected errors
class MissingCallbackException(
    message: String? = "Asynchronous calls require setting a callback function first",
    cause: Throwable? = null
) : WrapperException(message)


// Unknown / unexpected errors
class NullResponseException(
    message: String? = "get null response.",
    cause: Throwable? = null
) : WrapperException(message)

// Unknown / unexpected errors
class NullPhotoException(
    message: String? = "get null photo.",
    cause: Throwable? = null
) : WrapperException(message)