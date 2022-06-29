package com.couchbase.learningpath.data.audits

import com.couchbase.learningpath.data.Repository
import com.couchbase.learningpath.models.Audit
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
interface AuditRepository : Repository<Audit>  {

    fun getAuditsByProjectId(projectId: String): Flow<List<Audit>>?
    suspend fun get(projectId: String, auditId: String): Audit
}