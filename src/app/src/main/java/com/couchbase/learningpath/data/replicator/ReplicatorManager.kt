package com.couchbase.learningpath.data.replicator
import com.couchbase.lite.*

//handle pointers to replicator and database associated with it
class ReplicatorManager (var database: Database) {
    var replicator: Replicator? = null
    var replicatorConfiguration: ReplicatorConfiguration? = null
}