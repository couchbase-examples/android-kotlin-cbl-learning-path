package com.couchbase.learningpath.data.audits

import com.couchbase.learningpath.data.Repository
import com.couchbase.learningpath.models.Audit
import com.couchbase.learningpath.models.StockItem
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
interface AuditRepository : Repository<Audit>  {

    fun getAuditsByProjectId(projectId: String): Flow<List<Audit>>?
    suspend fun get(projectId: String, auditId: String): Audit
    suspend fun updateAuditStockItem(projectId: String, auditId: String, stockItem: StockItem)
    suspend fun deleteProjectAudits(projectId: String)
}