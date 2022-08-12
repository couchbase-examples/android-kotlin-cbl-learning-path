package com.couchbase.learningpath.models

import androidx.annotation.Keep
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Keep
@Serializable
@ExperimentalSerializationApi
data class StockItemDao(var item: StockItem)

@Keep
@Serializable
@ExperimentalSerializationApi
data class StockItem (
    var itemId: String = "",
    var name: String = "",
    var price: Float,
    var description: String = "",
    var style: String = "",
    var documentType: String = "item") {

    fun toJson(): String {
        return Json.encodeToString(this)
    }
}