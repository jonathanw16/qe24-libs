/*import com.couchbase.client.java.*
import com.couchbase.client.java.kv.*
import com.couchbase.client.java.json.*
import com.couchbase.client.java.query.*
import com.couchbase.client.core.error.CouchbaseException */

def call(name) {
    def cluster = Cluster.connect("couchbases://cb.eh32avgkwwptcnks.cloud.couchbase.com", "jonwilcb", "Pixelj_2112")
    def bucket = cluster.bucket("travel-sample")
    def scope = bucket.scope("tenant_agent_00")
    def collection = scope.collection("users")

    try{
        def docName = collection.get(name)
        return true 
    }catch(docex) {
        def upsertResult = collection.upsert(name, JsonObject.create().put("AMI", name))
        return false
    }
}