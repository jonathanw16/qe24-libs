
@Grab(group='com.couchbase.client', module='java-client', version='3.3.1')

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.core.error.DocumentExistsException;
import com.couchbase.client.core.error.DocumentNotFoundException;

import static com.couchbase.client.java.kv.ReplaceOptions.replaceOptions;
import static com.couchbase.client.java.kv.MutateInSpec.upsert;

import java.util.Arrays;

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

pipeline {
    agent any
    parameters {
        string(name: 'USERNAME', defaultValue: 'pipeline-user', description: 'username')
        string(name: 'PASSWORD', defaultValue: 'Passw0rd$', description: 'use this password')
        string(name: 'CONNECTSTR', defaultValue: 'couchbases://cb.7wy-kwqtahmriwk.cloud.couchbase.com', description: 'connectionstring')
    }
    stages {
        stage('Hello') {
            steps {
                script{
                    echo 'Hello World'
                }
            }
        }
        stage('Get AMI name') {
            steps {
                echo 'Starting'
                script {
                    if(checkName("TEST_DOC", params.CONNECTSTR, params.USERNAME, params.PASSWORD) == true) {
                        println('success')
                    } else {
                        println('fail')
                    }
                }
            }
        }
        stage('Update AMI') {
            steps {
                echo 'Starting'
                script {
                   println(updateAmi("TEST_DOC", "dev-pipeline.TEST11", "TEST", params.CONNECTSTR, params.USERNAME, params.PASSWORD))
                }
            }
        }
        stage('TESTS') {
            steps {
                script {
                    updateAmi("TEST_DOC", "dev-pipeline.test33", "test", params.CONNECTSTR, params.USERNAME, params.PASSWORD)
                }
            }
        }
        stage('tests2') {
            steps {
                script {
                    updateAmi("TEST_DOC", "dev-pipeline.test22", "test", params.CONNECTSTR, params.USERNAME, params.PASSWORD)
                }
            }
        }
        
    }
    post {
            success {
                updateAmi("TEST_DOC", "dev-pipeline.TEST10", "SUCCESS", params.CONNECTSTR, params.USERNAME, params.PASSWORD)

            }
            failure {
                updateAmi("TEST_DOC", "dev-pipeline.TEST61", "FAIL", params.CONNECTSTR, params.USERNAME, params.PASSWORD)

            }
        }
}

def checkName(name, connectString, username, password) {
    def cluster = Cluster.connect(connectString, username, password)
    def bucket = cluster.bucket("qe24_status")
    def scope = bucket.scope("_default")
    def collection = scope.collection("_default")

    try{
        def docName = collection.get(name)
        return true 
    }catch(DocumentNotFoundException e) {
        def upsertResult = collection.upsert(name, JsonObject.create()
            .put("dev-pipeline", [:])
            .put("stage-pipeline", [:])
            .put("AMI", name)
            .put("PIPELINE_STATUS", "STARTED"))
        return false
    }

}

def updateAmi(name, key, value, connectString, username, password) {
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
