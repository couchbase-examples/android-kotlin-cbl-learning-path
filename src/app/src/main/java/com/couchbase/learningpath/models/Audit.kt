package com.couchbase.learningpath.models

import androidx.annotation.Keep
import com.couchbase.learningpath.util.DateSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

@Keep
@Serializable
@ExperimentalSerializationApi
data class AuditDao(var item: Audit)

@Keep
@Serializable
@ExperimentalSerializationApi
data class Audit (
    var auditId: String = "",
    var projectId: String = "",
    var stockItem: StockItem? = null,
    var auditCount: Int = 0,
    var documentType: String = "",
    var notes: String = "",
    //security tracking
    var team: String = "",
    var createdBy: String = "",
    var modifiedBy: String = "",
    @Serializable(with = DateSerializer::class)
    var createdOn: Date? = null,
    @Serializable(with = DateSerializer::class)
    var modifiedOn: Date? = null
)
{
    fun toJson(): String {
        return Json.encodeToString(this)
    }
}