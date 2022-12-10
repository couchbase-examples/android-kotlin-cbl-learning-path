package com.couchbase.learningpath.services

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.couchbase.learningpath.data.DatabaseManager
import com.couchbase.learningpath.data.replicator.ReplicatorConfig
import com.couchbase.learningpath.data.replicator.ReplicatorManager
import com.couchbase.lite.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import java.net.URI

@InternalCoroutinesApi
@OptIn( ExperimentalCoroutinesApi::class)
class ReplicatorServiceDb  (
    private val authenticationService: AuthenticationService,
    private val databaseManager: DatabaseManager
) : ReplicatorService {
    private var replicatorManager : ReplicatorManager? = null
    private var loggedInUser = authenticationService.getCurrentUser()

    //track replication state
    override var isReplicationStarted = false

    //if your sync gateway server is running on a different IP change it here
    override var replicationConfig = mutableStateOf(
        ReplicatorConfig(
        username = loggedInUser.username,
        password = loggedInUser.password,
            endpointUrl = "ws://10.0.2.2:4984/projects",
        replicatorType = "PUSH AND PULL",
        heartBeat = 60L,
        continuous = true,
        selfSignedCert = true)
    )


    override val replicationStatus = mutableStateOf("")
    override val replicationTypes = listOf("PUSH AND PULL", "PUSH", "PULL")
    override val canStartReplication = mutableStateOf(false)

    override fun updateAuthentication(isReset: Boolean) {
        loggedInUser = authenticationService.getCurrentUser()
        if (isReset){
            replicationConfig.value.username = ""
            replicationConfig.value.password = ""

        } else {
            replicationConfig.value.username = loggedInUser.username
            replicationConfig.value.password = loggedInUser.password
            databaseManager.inventoryDatabase?.let {
                replicatorManager = ReplicatorManager(it)
            }
        }
    }

    override fun updateReplicationConfig(replicationConfig: ReplicatorConfig) {
        if ( replicatorManager?.replicatorConfiguration == null ||
            (replicatorManager?.replicator?.status?.activityLevel == ReplicatorActivityLevel.STOPPED ||
                    replicatorManager?.replicator?.status?.activityLevel == ReplicatorActivityLevel.IDLE ||
                    replicatorManager?.replicator?.status?.activityLevel == ReplicatorActivityLevel.OFFLINE)
        ){
            replicatorManager?.let { replicatorResources ->
                val urlEndPoint = URLEndpoint(URI(replicationConfig.endpointUrl)) // 1
                replicatorResources.replicatorConfiguration = ReplicatorConfiguration(replicatorResources.database, urlEndPoint) // 2
                replicatorResources.replicatorConfiguration?.let { replicatorConfiguration -> //3
                    replicatorConfiguration.isContinuous = replicationConfig.continuous // 4

                    when (replicationConfig.replicatorType) { // 5
                        "PULL" -> replicatorConfiguration.type = ReplicatorType.PULL // 5
                        "PUSH" -> replicatorConfiguration.type = ReplicatorType.PUSH // 5
                        else -> replicatorConfiguration.type =  ReplicatorType.PUSH_AND_PULL // 5
                    }
                    val authenticator = BasicAuthenticator( // 6
                        replicationConfig.username, // 6
                        replicationConfig.password.toCharArray() // 6
                    )
                    replicatorConfiguration.setAuthenticator(authenticator) //6
                    replicatorResources.replicator =                            // 7
                        Replicator(replicatorManager?.replicatorConfiguration!!) //7
                }

                canStartReplication.value = true //8
                this.replicationConfig.value = replicationConfig //9
            }
        } else {
            throw Exception("Error: can't update Replicator Config because replication is running")
        }
    }

    override fun calculateReplicationStatus() {
        if (replicatorManager?.replicatorConfiguration == null){
            replicationStatus.value = "${ReplicationStatus.NOCONFIG}, use Cog toolbar icon to set config"
        } else if (replicatorManager?.replicator == null){
            replicationStatus.value = "${ReplicationStatus.UNINITIALIZED}, use Cog toolbar icon to set config and replicator.  THIS SHOULD NEVER BE THE STATE"
        } else {
            replicatorManager?.replicator?.let {
                when(it.status.activityLevel){
                    ReplicatorActivityLevel.OFFLINE -> replicationStatus.value =
                        ReplicationStatus.OFFLINE
                    ReplicatorActivityLevel.IDLE -> replicationStatus.value = ReplicationStatus.IDlE
                    ReplicatorActivityLevel.STOPPED -> replicationStatus.value =
                        ReplicationStatus.STOPPED
                    ReplicatorActivityLevel.BUSY -> replicationStatus.value = ReplicationStatus.BUSY
                    ReplicatorActivityLevel.CONNECTING -> replicationStatus.value =
                        ReplicationStatus.CONNECTING
                }
            }
        }
    }

    override fun startReplication() {
        try {
            if (databaseManager.inventoryDatabase == null){
                databaseManager.initializeDatabases(authenticationService.getCurrentUser())
            }
            replicatorManager?.replicator?.start()
            isReplicationStarted = true
        } catch (e: Exception){
            Log.e(e.message, e.stackTraceToString())
        }
    }

    override fun stopReplication() {
        try {
            replicatorManager?.replicator?.stop()
            isReplicationStarted = false
        } catch (e: Exception){
            Log.e(e.message, e.stackTraceToString())
        }
    }

    override fun getReplicatorChangeFlow(): Flow<ReplicatorChange>? {
        replicatorManager?.replicator?.let {
            return it.replicatorChangesFlow()
        }
        return null
    }
}

object ReplicationStatus {
    const val STOPPED = "Stopped"
    const val OFFLINE = "Offline"
    const val IDlE = "Idle"
    const val BUSY = "Busy"
    const val CONNECTING = "Connecting"
    const val UNINITIALIZED = "Not Initialized"
    const val NOCONFIG = "No Replication Configuration"
}