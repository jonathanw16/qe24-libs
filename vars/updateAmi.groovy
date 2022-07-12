 @Grab(group='com.couchbase.client', module='java-client', version='3.3.1')

 

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
    }catch(CouchbaseException cbex) {
        return 'fail'
    }
    
}