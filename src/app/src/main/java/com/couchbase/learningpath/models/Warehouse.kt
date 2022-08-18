package com.couchbase.learningpath.models

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class WarehouseDao(var item: Warehouse)

@Keep
@Serializable
data class Warehouse(
    val warehouseId: String,
    val name: String,
    val address1: String,
    val address2: String? = "",
    val city: String,
    val state: String,
    val postalCode: String,
    val salesTax: Double,
    val yearToDateBalance: Double,
    val latitude: Double,
    val longitude: Double,
    val documentType: String,
    val shippingTo: List<String>? = null,
    )
