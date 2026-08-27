pipeline {

    agent any

    parameters {

        string(
            name: 'GIT_BRANCH',
            defaultValue: 'main',
            description: 'Git branch to build'
        )

        string(
            name: 'GIT_REPOSITORY',
            defaultValue: 'https://github.com/pravallika2580/QuizApp.git',
            description: 'Git repository URL'
        )

        string(
            name: 'JAVA_HOME_PATH',
            defaultValue: 'C:/Program Files/Java/jdk-17.0.2',
            description: 'JDK installation used by Maven and the application'
        )

        string(
            name: 'BACKEND_PORT',
            defaultValue: '8080',
            description: 'Spring Boot port'
        )

        string(
            name: 'BACKEND_URL',
            defaultValue: 'http://localhost:8080/api/user/quizzes',
            description: 'Backend health-check URL'
        )

        string(
            name: 'MAVEN_POM',
            defaultValue: 'quizapp/pom.xml',
            description: 'Path to the Maven pom.xml'
        )

        string(
            name: 'APP_JAR_PATH',
            defaultValue: 'quizapp/target/quizapp.jar',
            description: 'Path to the Spring Boot JAR'
        )

        string(
            name: 'APPZ_HOME',
            defaultValue: '',
            description: 'Tomcat installation directory; leave blank to skip Appzillon deployment'
        )

        string(
            name: 'APPZ_ARTIFACTS',
            defaultValue: '',
            description: 'Directory containing the WAR file'
        )

        string(
            name: 'TOMCAT_PORT',
            defaultValue: '8111',
            description: 'Tomcat port'
        )

        string(
            name: 'APPZILLON_URL',
            defaultValue: 'http://localhost:8111/QuizApp/',
            description: 'Appzillon health-check URL'
        )

        string(
            name: 'WAR_NAME',
            defaultValue: 'QuizApp.war',
            description: 'WAR file name'
        )

        string(
            name: 'APP_CONTEXT',
            defaultValue: 'QuizApp',
            description: 'Tomcat web application directory name'
        )

        booleanParam(
            name: 'RUN_PLAYWRIGHT',
            defaultValue: true,
            description: 'Run Playwright browser tests'
        )

        booleanParam(
            name: 'DEPLOY_APPZILLON',
            defaultValue: false,
            description: 'Deploy the WAR to Tomcat'
        )
    }


    environment {

        // ============================================================
        // JAVA
        // ============================================================

        JAVA_HOME = "${params.JAVA_HOME_PATH}"


        // ============================================================
        // SPRING BOOT
        // ============================================================

        APP_JAR = "${params.APP_JAR_PATH}"

        BACKEND_PORT = "${params.BACKEND_PORT}"

        BACKEND_URL = "${params.BACKEND_URL}"


        // ============================================================
        // TOMCAT / APPZILLON
        // ============================================================

        APPZ_HOME = "${params.APPZ_HOME}"

        APPZ_ARTIFACTS = "${params.APPZ_ARTIFACTS}"

        TOMCAT_PORT = "${params.TOMCAT_PORT}"

        APPZILLON_URL = "${params.APPZILLON_URL}"

        WAR_NAME = "${params.WAR_NAME}"

        APP_CONTEXT = "${params.APP_CONTEXT}"
    }


    stages {


        // ============================================================
        // CHECKOUT
        // ============================================================

        stage('Checkout') {

            steps {

                echo '=========================================='
                echo 'CHECKING OUT QUIZAPP'
                echo '=========================================='

                git branch: params.GIT_BRANCH,
                    url: params.GIT_REPOSITORY

                echo 'QUIZAPP CHECKOUT SUCCESSFUL'
            }
        }


        // ============================================================
        // BUILD BACKEND
        // ============================================================

        stage('Build Backend Jar') {

            steps {

                echo '=========================================='
                echo 'KILLING OLD PROCESSES'
                echo '=========================================='

                bat '''
                    @echo off

                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%BACKEND_PORT% ^| findstr LISTENING') do (
                        echo Killing process %%a on port %BACKEND_PORT%
                        taskkill /F /PID %%a >nul 2>&1
                    )

                    ping 127.0.0.1 -n 3 >nul
                '''


                echo '=========================================='
                echo 'STARTING MAVEN BUILD'
                echo '=========================================='

                bat 'mvn -f "%MAVEN_POM%" clean package -DskipTests'


                echo '=========================================='
                echo 'CHECKING JAR'
                echo '=========================================='

                bat 'dir quizapp\\target\\*.jar'
            }
        }


        // ============================================================
        // DEPLOY BACKEND
        // ============================================================

        stage('Deploy Backend') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo DEPLOYING QUIZAPP BACKEND
                    echo ==========================================

                    if not exist "%APP_JAR%" (
                        echo ERROR: JAR NOT FOUND: %APP_JAR%
                        exit /b 1
                    )

                    echo QuizApp JAR found.


                    echo.
                    echo ==========================================
                    echo CHECKING PORT %BACKEND_PORT%
                    echo ==========================================

                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%BACKEND_PORT% ^| findstr LISTENING') do (
                        echo Stopping process %%a on port %BACKEND_PORT%
                        taskkill /F /PID %%a >nul 2>&1
                    )

                    echo WAITING FOR PORT %BACKEND_PORT%

                    ping 127.0.0.1 -n 4 >nul


                    echo.
                    echo ==========================================
                    echo STARTING QUIZAPP
                    echo ==========================================

                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"
                    set "JENKINS_NODE_COOKIE=dontKillMe"

                    start "QuizApp-Backend" /B cmd /c ^
                    "set JENKINS_NODE_COOKIE=dontKillMe && java -jar %APP_JAR% > backend.log 2>&1"

                    echo QUIZAPP START COMMAND EXECUTED

                    echo WAITING FOR APPLICATION TO START

                    ping 127.0.0.1 -n 6 >nul


                    echo.
                    echo ==========================================
                    echo BACKEND LOG
                    echo ==========================================

                    if exist backend.log (
                        powershell -Command "Get-Content backend.log -Tail 20"
                    ) else (
                        echo backend.log not found
                    )
                '''
            }
        }


        // ============================================================
        // BACKEND HEALTH CHECK
        // ============================================================

        stage('Backend Health Check') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo CHECKING QUIZAPP BACKEND
                    echo ==========================================

                    echo Backend URL:
                    echo %BACKEND_URL%

                    set RETRIES=20


                    :CHECK_BACKEND

                    echo.
                    echo Checking backend...
                    echo Remaining attempts: %RETRIES%

                    curl -s -o nul -w "%%{http_code}" "%BACKEND_URL%" | findstr "200 201"

                    if not errorlevel 1 (

                        echo.
                        echo ==========================================
                        echo BACKEND IS RUNNING
                        echo ==========================================

                        echo Backend URL:
                        echo %BACKEND_URL%

                        exit /b 0
                    )


                    echo Backend not ready.

                    set /a RETRIES-=1


                    if %RETRIES% LEQ 0 (

                        echo.
                        echo ==========================================
                        echo BACKEND FAILED TO START
                        echo ==========================================

                        echo.
                        echo BACKEND LOG
                        echo ==========================================

                        if exist backend.log (
                            type backend.log
                        ) else (
                            echo backend.log not found
                        )

                        exit /b 1
                    )


                    echo Waiting 3 seconds before retry...

                    ping 127.0.0.1 -n 4 >nul

                    goto CHECK_BACKEND
                '''
            }
        }


        // ============================================================
        // DEPLOY APPZILLON
        // ============================================================

        stage('Deploy Appzillon') {

            when {
                expression {
                    params.DEPLOY_APPZILLON
                }
            }

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo DEPLOYING APPZILLON QUIZAPP
                    echo ==========================================


                    echo CHECKING QUIZAPP WAR

                    if not exist "%APPZ_ARTIFACTS%\\%WAR_NAME%" (
                        echo ERROR: %WAR_NAME% not found at %APPZ_ARTIFACTS%\\%WAR_NAME%
                        exit /b 1
                    )

                    echo QuizApp.war found.


                    echo.
                    echo ==========================================
                    echo CHECKING TOMCAT
                    echo ==========================================

                    echo TOMCAT HOME: %APPZ_HOME%

                    if not exist "%APPZ_HOME%\\bin\\catalina.bat" (
                        echo ERROR: catalina.bat not found
                        exit /b 1
                    )


                    echo.
                    echo ==========================================
                    echo STOPPING TOMCAT
                    echo ==========================================

                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%TOMCAT_PORT% ^| findstr LISTENING') do (
                        echo Killing PID %%a
                        taskkill /F /PID %%a >nul 2>&1
                    )

                    ping 127.0.0.1 -n 4 >nul


                    echo.
                    echo ==========================================
                    echo REMOVING OLD QUIZAPP
                    echo ==========================================

                    rmdir /S /Q "%APPZ_HOME%\\webapps\\%APP_CONTEXT%" >nul 2>&1

                    del /F /Q "%APPZ_HOME%\\webapps\\%WAR_NAME%" >nul 2>&1


                    echo.
                    echo ==========================================
                    echo COPYING QUIZAPP.WAR
                    echo ==========================================

                    copy /Y "%APPZ_ARTIFACTS%\\%WAR_NAME%" "%APPZ_HOME%\\webapps\\%WAR_NAME%"

                    if errorlevel 1 (
                        echo ERROR COPYING %WAR_NAME%
                        exit /b 1
                    )

                    echo QuizApp.war copied.


                    echo.
                    echo ==========================================
                    echo STARTING TOMCAT
                    echo ==========================================

                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"
                    set "CATALINA_HOME=%APPZ_HOME%"
                    set "JENKINS_NODE_COOKIE=dontKillMe"

                    echo Running: "%APPZ_HOME%\\bin\\catalina.bat" start

                    "%APPZ_HOME%\\bin\\catalina.bat" start

                    echo TOMCAT START COMMAND EXECUTED

                    echo WAITING 15 SECONDS FOR TOMCAT TO BOOT

                    ping 127.0.0.1 -n 16 >nul


                    echo.
                    echo ==========================================
                    echo CHECKING PORT %TOMCAT_PORT%
                    echo ==========================================

                    netstat -ano | findstr :%TOMCAT_PORT% | findstr LISTENING

                    if errorlevel 1 (
                        echo WARNING: Port %TOMCAT_PORT% not listening yet
                    ) else (
                        echo Port %TOMCAT_PORT% is listening
                    )


                    echo.
                    echo ==========================================
                    echo TOMCAT LOG
                    echo ==========================================

                    if exist "%APPZ_HOME%\\logs\\catalina.out" (

                        powershell -Command "Get-Content '%APPZ_HOME%\\logs\\catalina.out' -Tail 30"

                    ) else if exist "%APPZ_HOME%\\logs\\jenkins-run.log" (

                        powershell -Command "Get-Content '%APPZ_HOME%\\logs\\jenkins-run.log' -Tail 30"

                    ) else (

                        echo No Tomcat log found

                        dir "%APPZ_HOME%\\logs\\" 2>nul
                    )


                    echo.
                    echo ==========================================
                    echo DEPLOYED APP CONTENT
                    echo ==========================================

                    if exist "%APPZ_HOME%\\webapps\\%APP_CONTEXT%" (

                        dir "%APPZ_HOME%\\webapps\\%APP_CONTEXT%" | findstr /I "index.html WEB-INF"

                    ) else (

                        echo ERROR: deployed application directory was not created
                        exit /b 1
                    )
                '''
            }
        }


        // ============================================================
        // APPZILLON HEALTH CHECK
        // ============================================================

        stage('Appzillon Health Check') {

            when {
                expression {
                    params.DEPLOY_APPZILLON
                }
            }

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo CHECKING APPZILLON
                    echo ==========================================

                    echo URL: %APPZILLON_URL%

                    set RETRIES=30


                    :CHECK_APPZILLON

                    echo.
                    echo Checking... attempts left: %RETRIES%

                    curl -s -o nul -w "%%{http_code}" "%APPZILLON_URL%" | findstr "200 302 404"

                    if not errorlevel 1 (

                        echo.
                        echo ==========================================
                        echo APPZILLON IS RUNNING
                        echo ==========================================

                        echo URL: %APPZILLON_URL%

                        exit /b 0
                    )


                    set /a RETRIES-=1


                    if %RETRIES% LEQ 0 (

                        echo.
                        echo ==========================================
                        echo APPZILLON FAILED TO START
                        echo ==========================================

                        echo PORT %TOMCAT_PORT% STATUS:

                        netstat -ano | findstr :%TOMCAT_PORT%


                        echo.
                        echo TOMCAT LOG:

                        if exist "%APPZ_HOME%\\logs\\catalina.out" (

                            powershell -Command "Get-Content '%APPZ_HOME%\\logs\\catalina.out' -Tail 30"

                        ) else if exist "%APPZ_HOME%\\logs\\jenkins-run.log" (

                            powershell -Command "Get-Content '%APPZ_HOME%\\logs\\jenkins-run.log' -Tail 30"

                        ) else (

                            echo No log found
                        )


                        exit /b 1
                    )


                    ping 127.0.0.1 -n 6 >nul

                    goto CHECK_APPZILLON
                '''
            }
        }


        // ============================================================
        // PLAYWRIGHT AUTOMATION TESTS
        // ============================================================

        stage('Java Playwright Tests') {

            when {
                expression {
                    params.RUN_PLAYWRIGHT && params.DEPLOY_APPZILLON
                }
            }

            steps {

                echo '=========================================='
                echo 'STARTING JAVA PLAYWRIGHT TESTS'
                echo '=========================================='


                bat '''
                    @echo off

                    echo.
                    echo ==========================================
                    echo PLAYWRIGHT CONFIGURATION
                    echo ==========================================

                    echo Base URL:
                    echo %APPZILLON_URL%

                    echo.

                    echo Setting Playwright to VISIBLE MODE

                    set "PLAYWRIGHT_BASE_URL=%APPZILLON_URL%"

                    set "PLAYWRIGHT_HEADLESS=false"


                    echo.
                    echo ==========================================
                    echo PLAYWRIGHT BASE URL
                    echo ==========================================

                    echo %PLAYWRIGHT_BASE_URL%


                    echo.
                    echo ==========================================
                    echo PLAYWRIGHT HEADLESS
                    echo ==========================================

                    echo %PLAYWRIGHT_HEADLESS%


                    echo.
                    echo ==========================================
                    echo INSTALLING / VERIFYING CHROMIUM
                    echo ==========================================

                    mvn -f "%MAVEN_POM%" exec:java "-Dexec.mainClass=com.microsoft.playwright.CLI" "-Dexec.classpathScope=test" "-Dexec.args=install chromium"

                    if errorlevel 1 (
                        echo ERROR: Playwright Chromium installation failed
                        exit /b 1
                    )


                    echo.
                    echo ==========================================
                    echo RUNNING PLAYWRIGHT TEST
                    echo ==========================================

                    set "PLAYWRIGHT_BASE_URL=%APPZILLON_URL%"
                    set "PLAYWRIGHT_HEADLESS=false"

                    mvn -f "%MAVEN_POM%" clean test -Dtest=ExampleTest

                    if errorlevel 1 (
                        echo.
                        echo ==========================================
                        echo PLAYWRIGHT TEST FAILED
                        echo ==========================================
                        exit /b 1
                    )


                    echo.
                    echo ==========================================
                    echo PLAYWRIGHT TEST PASSED
                    echo ==========================================
                '''
            }


            post {

                always {

                    echo 'Archiving Playwright results...'

                    archiveArtifacts artifacts:
                        'quizapp/test-results/**, quizapp/target/surefire-reports/**',
                        allowEmptyArchive: true
                }
            }
        }
    }


    // ============================================================
    // POST ACTIONS
    // ============================================================

    post {

        success {

            echo '=========================================='
            echo 'QUIZAPP DEPLOYMENT + PLAYWRIGHT SUCCESSFUL'
            echo '=========================================='

            echo 'Backend:'
            echo 'http://localhost:8080'

            echo 'Appzillon:'
            echo 'http://localhost:8111/QuizApp/'

            echo 'Playwright:'
            echo 'Automation tests PASSED'

            echo '=========================================='
        }


        failure {

            echo '=========================================='
            echo 'QUIZAPP DEPLOYMENT OR PLAYWRIGHT FAILED'
            echo '=========================================='

            echo 'Check the stage that failed.'

            echo '=========================================='
        }
    }
}