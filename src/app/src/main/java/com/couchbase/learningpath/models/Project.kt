package com.couchbase.learningpath.models

import androidx.annotation.Keep
import com.couchbase.learningpath.util.DateSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

@Keep
@Serializable
data class ProjectDTO(var item: Project)

@Keep
@Serializable
data class Project (
    var projectId: String = "",
    var name: String = "",
    var description: String = "",
    var isComplete: Boolean = false,
    var type: String = "",
    @Serializable(with = DateSerializer::class)
    var dueDate: Date? = null,
    var location: Location? = null,

    //security tracking
    var team: String = "",
    var createdBy: String = "",
    var modifiedBy: String = "",
    @Serializable(with = DateSerializer::class)
    var createdOn: Date? = null,
    @Serializable(with = DateSerializer::class)
    var modifiedOn: Date? = null
){

    fun isOverDue(): Boolean {
        if (Date() > dueDate)
            return true
        return false
    }

    fun getDueDateString():String {
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val calendar: Calendar = Calendar.getInstance()
        dueDate?.let {

            calendar.timeInMillis = it.time
            return formatter.format(calendar.time)
        }
        return ""
    }

    fun toJson(): String {
        return Json.encodeToString(this)
    }

}