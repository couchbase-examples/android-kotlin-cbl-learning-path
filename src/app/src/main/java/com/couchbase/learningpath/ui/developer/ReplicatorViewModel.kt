package com.couchbase.learningpath.ui.developer

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.couchbase.learningpath.services.ReplicationStatus
import com.couchbase.learningpath.services.ReplicatorService
import com.couchbase.lite.ReplicatorActivityLevel
import com.couchbase.lite.ReplicatorChange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class ReplicatorViewModel(private val replicatorService: ReplicatorService)
    : ViewModel() {

    val logMessages = mutableStateListOf("")
    val replicationProgress = mutableStateOf("Not Started")
    val replicationStatus =  replicatorService.replicationStatus
    val isButtonActive = replicatorService.canStartReplication

    private var replicatorFlow: Flow<ReplicatorChange>? = null

    fun clearLogs() {
        logMessages.clear()
    }

    fun onStopClick() {
        viewModelScope.launch(Dispatchers.IO){
            logMessages.add("INFORMATION:: Stopping Replication...")
            replicatorService.stopReplication()
            logMessages.add("INFORMATION:: Replication Service notified to stop...")
        }
    }

    fun onStartClick () {
        viewModelScope.launch(Dispatchers.IO) {
            when (replicationStatus.value) {
                ReplicationStatus.STOPPED, ReplicationStatus.IDlE, "" -> {

                    logMessages.add("INFORMATION:: Starting Replication...")
                    replicatorService.startReplication()

                    logMessages.add("INFORMATION:: Getting Replication Change Flow...")
                    replicatorFlow = replicatorService.getReplicatorChangeFlow()
                    replicatorFlow?.let { flowChange ->
                        flowChange.collect {  replicatorChange ->
                            logMessages.add("INFORMATION:: Collecting Replication Change Flow...")

                            when (replicatorChange.status.activityLevel){
                                ReplicatorActivityLevel.OFFLINE -> replicationStatus.value = ReplicationStatus.OFFLINE
                                ReplicatorActivityLevel.IDLE -> replicationStatus.value = ReplicationStatus.IDlE
                                ReplicatorActivityLevel.STOPPED -> replicationStatus.value = ReplicationStatus.STOPPED
                                ReplicatorActivityLevel.BUSY -> replicationStatus.value = ReplicationStatus.BUSY
                                ReplicatorActivityLevel.CONNECTING -> replicationStatus.value = ReplicationStatus.CONNECTING
                            }
                            replicatorChange.status.error?.let { error ->
                                logMessages.add("ERROR:: ${error.code} - ${error.message}")
                            }
                            logMessages.add("INFORMATION:: Checking replication progress...")
                            if (replicatorChange.status.progress.completed == replicatorChange.status.progress.total){
                                replicationProgress.value = "Completed"
                            } else {
                                replicationProgress.value = "${replicatorChange.status.progress.total / replicatorChange.status.progress.completed}"
                            }
                        }
                    }
                    logMessages.add("INFORMATION:: Replication Started method completed...")
                }
                else -> {
                    logMessages.add("INFORMATION:: Stopping replication...")
                    replicatorService.stopReplication()
                }
            }
        }
    }
}