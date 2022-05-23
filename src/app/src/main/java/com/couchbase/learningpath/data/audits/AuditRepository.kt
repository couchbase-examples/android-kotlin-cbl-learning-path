package com.couchbase.learningpath.data.audits

import com.couchbase.learningpath.models.Audit
import kotlinx.coroutines.flow.Flow

interface AuditRepository {

    fun getAuditsByProjectId(projectId: String): Flow<List<Audit>>?

    suspend fun getAudit(projectId: String, auditId: String): Audit

    suspend fun saveAudit(audit: Audit)

    suspend fun deleteAudit(auditId: String) : Boolean
}