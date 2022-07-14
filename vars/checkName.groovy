 @Grab(group='com.couchbase.client', module='java-client', version='3.3.1')

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.core.error.DocumentExistsException;
import com.couchbase.client.core.error.DocumentNotFoundException;

import com.couchbase.client.java.AsyncCollection;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.ReactiveCollection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.kv.PersistTo;
import com.couchbase.client.java.kv.ReplicateTo;

def call(name, connectString, username, password) {
    def cluster = Cluster.connect(connectString, username, password)
    def bucket = cluster.bucket("travel-sample")

    def scope = bucket.scope("tenant_agent_00")
    def collection = scope.collection("users")

    try{
        def docName = collection.get(name)
        return true 
    }catch(DocumentNotFoundException e) {
        def upsertResult = collection.upsert(name, JsonObject.create().put("AMI", name).put("PIPELINE_STATUS", "STARTED"))
        return false
    }

}