library identifier: 'qe24-libs@main'

pipeline {
    agent any
    parameters {
        string(name: 'USERNAME', defaultValue: 'jwcb', description: 'username')
        string(name: 'PASSWORD', defaultValue: 'Pixelj_2112', description: 'use this password')
        string(name: 'CONNECTSTR', defaultValue: 'couchbases://cb.eh32avgkwwptcnks.cloud.couchbase.com', description: 'connectionstring')
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
                    if(checkName("JENKINS_TEST6.DEV_PIPELINE.TESTING_STAt", params.CONNECTSTR, params.USERNAME, params.PASSWORD) == true) {
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
                   println(updateAmi("JENKINS_TEST6", "DEV_PIPELINE.TESTING_STAt", "TEST", params.CONNECTSTR, params.USERNAME, params.PASSWORD))
                }
            }
        }
    }
}
