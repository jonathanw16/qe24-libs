 @Grab(group='com.couchbase.client', module='java-client', version='3.3.1')

import static com.couchbase.client.java.kv.ReplaceOptions.replaceOptions;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;

def call(name, key, value) {
    def cluster = Cluster.connect("couchbases://cb.eh32avgkwwptcnks.cloud.couchbase.com", "jonwilcb", "Pixelj_2112")
    def bucket = cluster.bucket("travel-sample")
    def scope = bucket.scope("tenant_agent_00")
    def collection = scope.collection("users")

    try{
        def docAMI = collection.get(name)
        def content = docAMI.contentAsObject()
        content.put(key, value, replaceOptions().cas(docAMI.cas()))
        collection.replace(name, content, replaceOptions().cas(docAMI.cas()))
        
        return 'success'
    }catch(cbex) {
        return 'fail'
    }
    
}