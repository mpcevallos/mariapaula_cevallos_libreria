def call(boolean abortPipeline = false) {
    // Lanza el sonar-scanner con la configuración por defecto
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

    // Evalua el QualityGate de SonarQube y decide si abortar el pipeline o no
    timeout(time: 5, unit: 'MINUTES') {
        if (abortPipeline) {
            error 'El QualityGate ha fallado. Cerrando el pipeline.'
        } else {
            echo 'El QualityGate ha pasado. Continuando con el pipeline.'
        }
    }
}
