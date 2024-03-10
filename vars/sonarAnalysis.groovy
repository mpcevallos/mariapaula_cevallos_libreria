def call(boolean abortPipeline = false, String branchName = env.BRANCH_NAME) {
    
    def scannerHome = tool 'sonar-scanner'
    withEnv(["PATH+SONAR=${scannerHome}/bin"]) {
        sh """
            sonar-scanner \
            -Dsonar.projectKey=sonarqube \
            -Dsonar.sources=. \
            -Dsonar.host.url=http://localhost:9000 \
            -Dsonar.token=sqp_8871e861546564ca35025574380ccc281e056c0c
        """
    }

    
    script {
        timeout(time: 10, unit: 'MINUTES') {
            
            if (shouldAbortPipeline(abortPipeline, branchName)) {
                error 'QualityGate ha fallado. Abortando el pipeline.'
            } else {
                echo 'QualityGate ha sido superado. Continuando con el pipeline.'
            }
        }
    }
}

def shouldAbortPipeline(boolean abortPipeline, String branchName) {
    
    if (branchName == 'master') {
        return true
    }


    if (branchName == 'master' || branchName.startsWith('hotfix/')) {
        return true
    }

    return false
}