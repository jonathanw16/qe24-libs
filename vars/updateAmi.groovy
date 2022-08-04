@Grab(group='com.couchbase.client', module='java-client', version='3.3.1')

import static com.couchbase.client.java.kv.ReplaceOptions.replaceOptions;
import static com.couchbase.client.java.kv.MutateInSpec.upsert;

import java.util.Arrays;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;

def call(name, key, value, connectString, username, password) {
    def cluster = Cluster.connect(connectString, username, password)
    def bucket = cluster.bucket("qe24_status")
    def scope = bucket.scope("_default")
    def collection = scope.collection("_default")

    try{
        collection.mutateIn(name, Arrays.asList(upsert(key, value)))
        return 'success'
    }catch(e) {
        return 'fail'
    }
}