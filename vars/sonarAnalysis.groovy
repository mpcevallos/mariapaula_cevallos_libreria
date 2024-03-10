def call(Map params) {
    // Implementación de la función
    // Accede a los parámetros usando params.abortPipeline y params.branchName

    def abortPipeline = params.abortPipeline ?: false
    def branchName = params.branchName ?: 'null'

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
    if (branchName == 'null') {
        return true
    }

    if (branchName.startsWith('hotfix/')) {
        return true
    }

    return false
}
