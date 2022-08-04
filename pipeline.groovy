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
                    bat "python3 main.py -u ${params.USERNAME} -p ${params.PASSWORD} -c ${params.CONNECTSTR} -m amitest -a update -k ${we}.${aw} -v w"
                }
            }
        }
        stage('Get Latest') {
            steps {
                script {
                    echo 'Starting'
                    latest = bat (script: "python3 main.py -u ${params.USERNAME} -p ${params.PASSWORD} -c ${params.CONNECTSTR} -a latest -e dev", returnStdout: true)
                    echo latest
                    if (latest == "C:\\Users\\Jonathan Wilcox\\AppData\\Local\\Jenkins\\.jenkins\\workspace\\TEST>python3 main.py -u pipeline-user -p Passw0rd$ -c couchbases://cb.7wy-kwqtahmriwk.cloud.couchbase.com -a latest -e dev 
couchbase-serverless-server-7.2.0-1741") {
                        echo 'HILLO'
                    }
                }
                
            }
        }
        
    }
    
}

