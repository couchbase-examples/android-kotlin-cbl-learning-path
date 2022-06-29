package com.couchbase.learningpath.services

import androidx.compose.runtime.mutableStateOf
import com.couchbase.learningpath.data.replicator.ReplicatorConfig
import com.couchbase.lite.ReplicatorChange
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@InternalCoroutinesApi
class ReplicatorServiceMock : ReplicatorService {
    //track replication state
    override var isReplicationStarted = false

    //if your sync gateway server is running on a different IP change it here
    override var replicationConfig = mutableStateOf(
        ReplicatorConfig(
            username = "demo@example.com",
            password = "password",
            endpointUrl = "ws://10.0.2.2:4984/projects",
            replicatorType = "PUSH AND PULL",
            heartBeat = 60L,
            continuous = true,
            selfSignedCert = true)
    )


    override val replicationStatus = mutableStateOf("")
    override val replicationTypes = listOf("PUSH AND PULL", "PUSH", "PULL")
    override val canStartReplication = mutableStateOf(false)

    override fun updateReplicationConfig(replicationConfig: ReplicatorConfig) {
    }

    override fun calculateReplicationStatus() {
    }

    override fun startReplication() {
    }

    override fun stopReplication() {
    }

    override fun updateAuthentication(isReset: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getReplicatorChangeFlow(): Flow<ReplicatorChange>? {
        return null
    }

}