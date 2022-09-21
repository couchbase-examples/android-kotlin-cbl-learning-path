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
import java.lang.NumberFormatException
import java.util.*

@OptIn(ExperimentalSerializationApi::class)
class AuditEditorViewModel(
    private val auditRepository: AuditRepository
) : ViewModel() {

    private val defaultStockItemText: String = "No Stock Item Selected"
    var auditState = mutableStateOf<Audit?>(null)
    var auditId = mutableStateOf("")
    var projectId = mutableStateOf("")
    var count = mutableStateOf("")
    val stockItemSelectionState = mutableStateOf(defaultStockItemText)
    val errorMessageState = mutableStateOf("")

    var navigateUpCallback: () -> Unit = { }
    var navigateToListSelection: (String, String) -> Unit =
        { _: String, _: String -> }

    fun loadAudit() {
        viewModelScope.launch {
            try {
                val audit = auditRepository.get(projectId.value, auditId.value)
                auditState.value = audit
                //we need to set the auditId away from create if this is a new audit item
                //as the uuid is assigned by the repository
                auditId.value = audit.auditId
                auditState.value?.let {
                    count.value = it.auditCount.toString()
                    if (it.stockItem != null) {
                        it.stockItem?.let { stockItem ->
                            stockItemSelectionState.value = stockItem.name
                        }
                    } else {
                        stockItemSelectionState.value = defaultStockItemText
                    }
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
        }
    }

    fun getAudit(projectId: String, auditId: String) {
        this.projectId.value = projectId
        this.auditId.value = auditId
        viewModelScope.launch {
            if (auditId == "" || auditId == "create") {
                if (projectId != "") {
                    auditState.value = auditRepository.get(
                        projectId = projectId,
                        auditId = UUID.randomUUID().toString()
                    )
                }
            } else {
                loadAudit()
            }
        }
    }

    val onCountChanged: (String) -> Unit = { newValue ->
        if (newValue != "") {
            try {
                auditState.value?.auditCount = newValue.toInt()
                count.value = newValue
            } catch (nfe: NumberFormatException) {
                Log.e("Error", nfe.message.toString())
            }
        }
    }

    val onNotesChanged: (String) -> Unit = { newValue ->
        val a = auditState.value?.copy()
        a?.notes = newValue
        auditState.value = a
    }

    private fun saveAudit() {
        viewModelScope.launch {
            auditState.value?.let {
                try {
                    errorMessageState.value = ""
                    //just in case the project isn't set for some reason
                    it.projectId = projectId.value
                    //trim any notes that were entered
                    it.notes = it.notes.trim()
                    auditRepository.save(it)
                } catch (e: Exception){
                    Log.e(e.message, e.stackTraceToString())
                    errorMessageState.value = e.message.toString()
                }
            }
        }
    }

    val onStockItemSelection: () -> Unit = {
        viewModelScope.launch {
            auditState.value?.let {
                saveAudit()
                withContext(Dispatchers.Main) {
                    navigateToListSelection(it.projectId, it.auditId)
                }
            }
        }
    }

    val onSaveAudit: () -> Unit = {
        viewModelScope.launch {
            if (projectId.value != "") {
                auditState.value?.let {
                    if (it.auditCount <= 0) {
                        errorMessageState.value = "Error: Count must be greater than zero"
                    } else if (it.stockItem == null) {
                        errorMessageState.value = "Error: Must select stock item before saving"
                    } else {
                        errorMessageState.value = ""
                        //clean up data - remove spaces at the end of strings
                        it.notes = it.notes.trim()

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
}