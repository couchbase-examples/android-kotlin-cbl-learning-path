package com.couchbase.learningpath.models

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class LocationDao(var item: Location)

@Keep
@Serializable
data class Location (
    val locationId: String,
    val name: String,
    val address1: String,
    val address2: String? = "",
    val city: String,
    val state: String? = "",
    val country: String,
    val postalCode: String,
    val latitude: Double,
    val longitude: Double,
    val type: String
)