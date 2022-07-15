package com.couchbase.learningpath.ui.audit

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.couchbase.learningpath.data.audits.AuditRepository
import com.couchbase.learningpath.models.Audit
import com.couchbase.learningpath.models.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.decodeFromString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.util.Base64

@OptIn(ExperimentalSerializationApi::class)
class AuditListViewModel(
    private val auditRepository: AuditRepository
): ViewModel()
{
    var projectJson: String = ""
    var project = Project()

    // create a flow to return the results dynamically as needed - more information on CoRoutine Flows can be found at
    // https://developer.android.com/kotlin/flow
    private var _auditFlow: Flow<List<Audit>>? = null

    private val _audits: MutableLiveData<List<Audit>> by lazy {
        MutableLiveData<List<Audit>>()
    }
    val audits: LiveData<List<Audit>> get() = _audits

    @SuppressLint("NewApi")
    fun getAudits() {
        _auditFlow = null
        val json = String(Base64.getDecoder().decode(projectJson))
        project = Json.decodeFromString<Project>(json)
        viewModelScope.launch(Dispatchers.IO) {
            _auditFlow = auditRepository.getAuditsByProjectId(project.projectId)
            _auditFlow?.let { f ->
                f.collect {
                    viewModelScope.launch(Dispatchers.Main){
                        _audits.postValue(it)
                    }
                }
            }
        }
    }

    val deleteAudit: (String) -> Boolean = { auditId: String ->
        var didDelete = false
        viewModelScope.launch(Dispatchers.IO) {
            didDelete = auditRepository.delete(auditId)
        }
        didDelete
    }
}