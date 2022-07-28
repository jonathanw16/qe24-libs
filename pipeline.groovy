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
                    newDoc = bat (script: "python3 main.py -u ${params.USERNAME} -p ${params.PASSWORD} -c ${params.CONNECTSTR} -m PYTEST4-${params.USERNAME}", returnStdout: true)
                    echo newDoc
                }
            }
        }
        stage('Update AMI') {
            steps {
                echo 'Starting'
                script {
                    bat "python3 main.py -u ${params.USERNAME} -p ${params.PASSWORD} -c ${params.CONNECTSTR} -m PYTEST3 -a update -k test -v test"
                }
            }
        }
        stage('TESTS') {
            steps {
                script {
                    updateAmi("TEST_DOC", "dev-pipeline.test33", "test", params.CONNECTSTR, params.USERNAME, "${params.PASSWORD}")
                }
            }
        }
        stage('tests2') {
            steps {
                script {
                    updateAmi("TEST_DOC", "dev-pipeline.test22", "test", params.CONNECTSTR, params.USERNAME, "${params.PASSWORD}")
                }
            }
        }
        
    }
    post {
            success {
                updateAmi("TEST_DOC", "dev-pipeline.TEST10", "SUCCESS", params.CONNECTSTR, params.USERNAME, "${params.PASSWORD}")

            }
            failure {
                updateAmi("TEST_DOC", "dev-pipeline.TEST61", "FAIL", params.CONNECTSTR, params.USERNAME, "${params.PASSWORD}")

            }
        }
}

