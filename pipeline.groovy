@Library('qe24-libs')_

pipeline {
    agent any
    parameters {
        string(name: 'USERNAME', defaultValue: 'pipeline-user', description: 'username')
        password(name: 'PASSWORD', defaultValue: 'Passw0rd$', description: 'use this password')
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
                   println(updateAmi("TEST_DOC", "dev-pipeline.TEST11", "TEST", params.CONNECTSTR, params.USERNAME, Secret.toString(params.PASSWORD)))
                }
            }
        }
        stage('TESTS') {
            steps {
                script {
                    updateAmi("TEST_DOC", "dev-pipeline.test33", "test", params.CONNECTSTR, params.USERNAME, Secret.toString(params.PASSWORD))
                }
            }
        }
        stage('tests2') {
            steps {
                script {
                    updateAmi("TEST_DOC", "dev-pipeline.test22", "test", params.CONNECTSTR, params.USERNAME, Secret.toString(params.PASSWORD))
                }
            }
        }
        
    }
    post {
            success {
                updateAmi("TEST_DOC", "dev-pipeline.TEST10", "SUCCESS", params.CONNECTSTR, params.USERNAME, Secret.toString(params.PASSWORD))

            }
            failure {
                updateAmi("TEST_DOC", "dev-pipeline.TEST61", "FAIL", params.CONNECTSTR, params.USERNAME, Secret.toString(params.PASSWORD))

            }
        }
}

