package com.couchbase.learningpath.ui.developer

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.couchbase.learningpath.data.replicator.ReplicatorConfig
import com.couchbase.learningpath.services.ReplicatorService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReplicatorConfigViewModel (private val replicatorService: ReplicatorService)
    : ViewModel() {

    private val replicatorConfig = replicatorService.replicationConfig.value
    val username: String = replicatorConfig.username
    val password: String = replicatorConfig.password
    val continuousState =  mutableStateOf(replicatorConfig.continuous)
    val selfSignedCertState =  mutableStateOf(replicatorConfig.selfSignedCert)
    val replicatorTypes = replicatorService.replicationTypes
    val replicatorTypeSelected =  mutableStateOf(replicatorConfig.replicatorType)
    val serverUrlState = mutableStateOf(replicatorConfig.endpointUrl)
    val heartBeatState = mutableStateOf(replicatorConfig.heartBeat)

    val onContinuousChanged: (Boolean) -> Unit =  { newValue ->
        continuousState.value = newValue
    }

    val onSelfSignedCertChanged: (Boolean) -> Unit = { newValue ->
        selfSignedCertState.value = newValue
    }

    val onReplicatorTypeChanged: (String) -> Unit = { newValue ->
        replicatorTypeSelected.value = newValue
    }

    val onServerUrlChanged: (String) -> Unit = { newValue ->
        serverUrlState.value = newValue
    }

    val onHeartBeatChanged: (Long) -> Unit = { newValue ->
        heartBeatState.value = newValue
    }

    fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            val config = ReplicatorConfig(
                username = username,
                password = password,
                endpointUrl = serverUrlState.value,
                replicatorType = replicatorTypeSelected.value,
                heartBeat = heartBeatState.value,
                continuous = continuousState.value,
                selfSignedCert = selfSignedCertState.value
            )
            try {
                replicatorService.updateReplicationConfig(config)
            } catch (e: Exception){
                //todo throw update the UI with error that states replication is running can't perform this until it's stopped
            }
        }
    }
}