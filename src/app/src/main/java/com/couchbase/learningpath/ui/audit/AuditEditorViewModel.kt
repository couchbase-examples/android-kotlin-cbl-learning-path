package com.couchbase.learningpath.ui.audit

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.couchbase.learningpath.data.audits.AuditRepository
import com.couchbase.learningpath.models.Audit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.lang.NumberFormatException
import java.util.*

@OptIn(ExperimentalSerializationApi::class)
class AuditEditorViewModel(
    private val auditRepository: AuditRepository
) : ViewModel() {
    var audit = mutableStateOf<Audit?>(null)

    var projectId = mutableStateOf("")
    private var auditJson = mutableStateOf("")

    var count = mutableStateOf("")

    var navigateUpCallback: () -> Unit = { }

    fun getAudit(projectId: String, auditJson: String) {
        this.projectId.value = projectId
        this.auditJson.value = auditJson
        viewModelScope.launch {
            if (auditJson == "" || auditJson == "create") {
                if (projectId != "") {
                    audit.value = auditRepository.get(
                        projectId = projectId,
                        auditId = UUID.randomUUID().toString()
                    )
                }
            } else {
                audit.value = Json.decodeFromString<Audit>(auditJson)
                audit?.value?.let {
                    count.value = it.count.toString()
                }
            }
        }
    }

    val onNameChanged: (String) -> Unit = { newValue ->
        val p = audit.value?.copy()
        p?.name = newValue
        audit.value = p
    }

    val onCountChanged: (String) -> Unit = { newValue ->
        if (newValue != "") {
            try {
                audit.value?.count = newValue.toInt()
                count.value = newValue
            } catch (nfe: NumberFormatException){
                Log.e("Error", nfe.message.toString())
            }
        }
    }

    val onNotesChanged: (String) -> Unit = { newValue ->
        val a = audit.value?.copy()
        a?.notes = newValue
        audit.value = a
    }

    val onPartNumberChanged: (String) -> Unit = { newValue ->
        viewModelScope.launch(Dispatchers.Main) {
            val a = audit.value?.copy()
            a?.partNumber = newValue
            audit.value = a
        }
    }

    val onSaveAudit: () -> Unit = {
        viewModelScope.launch {
            if (projectId.value != "") {
                audit?.value?.let {
                    //clean up data - remove spaces at the end of strings
                    it.name = it.name.trim()
                    it.notes?.let { notes ->
                        it.notes = notes.trim()
                    }
                    it.partNumber?.let { partNumber ->
                        it.partNumber = partNumber.trim()
                    }
                    //add in the project of the audit
                    it.projectId = projectId.value
                    auditRepository.save(it)
                    withContext(Dispatchers.Main) {
                        navigateUpCallback()
                    }
                }
            }
        }
    }
}