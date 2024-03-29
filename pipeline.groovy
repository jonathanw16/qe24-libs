@Library('qe24-libs')_

pipeline {
    agent any
    parameters {
        string(name: 'USERNAME', defaultValue: 'pipeline-user', description: 'username')
        password(name: 'PASSWORD', defaultValue: 'Passw0rd$', description: 'use this password')
        string(name: 'CONNECTSTR', defaultValue: 'couchbases://cb.jtsutglfipogkncn.cloud.couchbase.com', description: 'connectionstring')
    }
    stages {
        
        stage('Get AMI name') {
            steps {
                echo 'Starting'
                script {
                    newDoc = bat (script: "python3 main.py -u ${params.USERNAME} -p ${params.PASSWORD} -c ${params.CONNECTSTR} -m amitest", returnStdout: true)
                    echo newDoc
                }
            }
        }
        stage('Update AMI') {
            steps {
                echo 'Starting'
                script {
                    def we = "dev"
                    def aw = "test"
                    def ew = true
                    bat "python3 main.py -u ${params.USERNAME} -p ${params.PASSWORD} -c ${params.CONNECTSTR} -m amitest -a update -k ${we}.${aw} -v ${ew}"
                }
            }
        }
        stage('Get Latest') {
            steps {
                script {
                    echo 'Starting'
                    latest = bat (script: "python3 main.py -u ${params.USERNAME} -p ${params.PASSWORD} -c ${params.CONNECTSTR} -a latest -e dev", returnStdout: true)
                    echo latest
                }
                
            }
        }
        
    }
    
}

