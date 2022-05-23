package com.couchbase.learningpath.ui.audit

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.couchbase.learningpath.data.audits.AuditRepository
import com.couchbase.learningpath.models.Audit
import com.couchbase.learningpath.models.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class AuditListViewModel(
    projectJson: String?,
    private val auditRepository: AuditRepository
): ViewModel()
{
    var project = mutableStateOf(Project())
    // create a flow to return the results dynamically as needed - more information on CoRoutine Flows can be found at
    // https://developer.android.com/kotlin/flow
    var auditFlow: Flow<List<Audit>>? = null

    init {
        projectJson?.let { json ->
            project.value = Json.decodeFromString<Project>(json)
            viewModelScope.launch(Dispatchers.IO) {
                auditFlow = auditRepository.getAuditsByProjectId(project.value.projectId)
            }
        }
    }

    val deleteAudit: (String) -> Boolean = { auditId: String ->
        var didDelete = false
        viewModelScope.launch(Dispatchers.IO) {
            didDelete = auditRepository.deleteAudit(auditId)
        }
        didDelete
    }
}