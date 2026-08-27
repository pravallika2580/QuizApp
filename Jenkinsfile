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
            defaultValue: 'C:/Users/pravallika.k/Downloads/apache-tomcat-9.0.53 2/apache-tomcat-9.0.53',
            description: 'Tomcat installation directory'
        )

        string(
            name: 'APPZ_ARTIFACTS',
            defaultValue: 'D:/jenkins-testing',
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
            description: 'Appzillon URL'
        )

        string(
            name: 'WAR_NAME',
            defaultValue: 'QuizApp.war',
            description: 'WAR file name'
        )

        string(
            name: 'APP_CONTEXT',
            defaultValue: 'QuizApp',
            description: 'Tomcat application context'
        )

        booleanParam(
            name: 'RUN_PLAYWRIGHT',
            defaultValue: true,
            description: 'Run Playwright automation tests'
        )

        booleanParam(
            name: 'DEPLOY_APPZILLON',
            defaultValue: true,
            description: 'Deploy WAR to Tomcat'
        )
    }


    environment {

        JAVA_HOME = "${params.JAVA_HOME_PATH}"

        APP_JAR = "${params.APP_JAR_PATH}"

        BACKEND_PORT = "${params.BACKEND_PORT}"

        BACKEND_URL = "${params.BACKEND_URL}"

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
                echo 'STOPPING OLD BACKEND'
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
                echo 'BUILDING SPRING BOOT APPLICATION'
                echo '=========================================='

                bat '''
                    @echo off

                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"

                    java -version

                    mvn -f "%MAVEN_POM%" clean package -DskipTests

                    if errorlevel 1 (
                        echo BACKEND BUILD FAILED
                        exit /b 1
                    )
                '''


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
                        echo ERROR: JAR NOT FOUND
                        echo %APP_JAR%
                        exit /b 1
                    )


                    echo QuizApp JAR found.


                    echo.
                    echo ==========================================
                    echo CHECKING BACKEND PORT
                    echo ==========================================

                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%BACKEND_PORT% ^| findstr LISTENING') do (
                        echo Stopping process %%a
                        taskkill /F /PID %%a >nul 2>&1
                    )


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


                    echo BACKEND START COMMAND EXECUTED

                    echo WAITING FOR BACKEND

                    ping 127.0.0.1 -n 7 >nul


                    echo.
                    echo ==========================================
                    echo BACKEND LOG
                    echo ==========================================

                    if exist backend.log (
                        powershell -Command "Get-Content backend.log -Tail 30"
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
                    echo BACKEND HEALTH CHECK
                    echo ==========================================

                    echo URL:
                    echo %BACKEND_URL%

                    set RETRIES=20


                    :CHECK_BACKEND

                    echo.
                    echo Checking backend...
                    echo Attempts remaining: %RETRIES%


                    curl -s -o nul -w "%%{http_code}" "%BACKEND_URL%" | findstr "200 201"


                    if not errorlevel 1 (

                        echo.
                        echo ==========================================
                        echo BACKEND IS RUNNING
                        echo ==========================================

                        exit /b 0
                    )


                    set /a RETRIES-=1


                    if %RETRIES% LEQ 0 (

                        echo.
                        echo ==========================================
                        echo BACKEND FAILED
                        echo ==========================================

                        if exist backend.log (
                            type backend.log
                        )

                        exit /b 1
                    )


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
                    echo DEPLOYING APPZILLON
                    echo ==========================================


                    echo CHECKING WAR

                    if not exist "%APPZ_ARTIFACTS%\\%WAR_NAME%" (

                        echo ERROR:
                        echo %WAR_NAME% NOT FOUND

                        exit /b 1
                    )


                    echo WAR FOUND.


                    echo.
                    echo ==========================================
                    echo CHECKING TOMCAT
                    echo ==========================================

                    if not exist "%APPZ_HOME%\\bin\\catalina.bat" (

                        echo ERROR:
                        echo catalina.bat NOT FOUND

                        exit /b 1
                    )


                    echo TOMCAT FOUND.


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
                    echo REMOVING OLD APPLICATION
                    echo ==========================================

                    rmdir /S /Q "%APPZ_HOME%\\webapps\\%APP_CONTEXT%" >nul 2>&1

                    del /F /Q "%APPZ_HOME%\\webapps\\%WAR_NAME%" >nul 2>&1


                    echo.
                    echo ==========================================
                    echo COPYING WAR
                    echo ==========================================

                    copy /Y "%APPZ_ARTIFACTS%\\%WAR_NAME%" "%APPZ_HOME%\\webapps\\%WAR_NAME%"


                    if errorlevel 1 (

                        echo ERROR COPYING WAR

                        exit /b 1
                    )


                    echo WAR COPIED.


                    echo.
                    echo ==========================================
                    echo STARTING TOMCAT
                    echo ==========================================

                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"
                    set "CATALINA_HOME=%APPZ_HOME%"
                    set "JENKINS_NODE_COOKIE=dontKillMe"


                    "%APPZ_HOME%\\bin\\catalina.bat" start


                    echo TOMCAT START COMMAND EXECUTED


                    echo WAITING FOR TOMCAT

                    ping 127.0.0.1 -n 16 >nul


                    echo.
                    echo ==========================================
                    echo TOMCAT PORT
                    echo ==========================================

                    netstat -ano | findstr :%TOMCAT_PORT% | findstr LISTENING
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
                    echo APPZILLON HEALTH CHECK
                    echo ==========================================

                    echo URL:
                    echo %APPZILLON_URL%

                    set RETRIES=30


                    :CHECK_APPZILLON

                    echo.
                    echo Checking Appzillon...
                    echo Attempts remaining: %RETRIES%


                    curl -s -o nul -w "%%{http_code}" "%APPZILLON_URL%" | findstr "200 302 404"


                    if not errorlevel 1 (

                        echo.
                        echo ==========================================
                        echo APPZILLON IS RUNNING
                        echo ==========================================

                        exit /b 0
                    )


                    set /a RETRIES-=1


                    if %RETRIES% LEQ 0 (

                        echo.
                        echo ==========================================
                        echo APPZILLON FAILED
                        echo ==========================================

                        netstat -ano | findstr :%TOMCAT_PORT%

                        exit /b 1
                    )


                    ping 127.0.0.1 -n 6 >nul

                    goto CHECK_APPZILLON
                '''
            }
        }


        // ============================================================
        // JAVA PLAYWRIGHT TESTS
        // ============================================================

        stage('Java Playwright Tests') {

            when {

                expression {
                    params.RUN_PLAYWRIGHT && params.DEPLOY_APPZILLON
                }
            }

            steps {

                echo '=========================================='
                echo 'STARTING JAVA PLAYWRIGHT AUTOMATION'
                echo '=========================================='


                bat '''
                    @echo off

                    echo.
                    echo ==========================================
                    echo PLAYWRIGHT CONFIGURATION
                    echo ==========================================

                    set "PLAYWRIGHT_BASE_URL=%APPZILLON_URL%"

                    set "PLAYWRIGHT_HEADLESS=false"


                    echo PLAYWRIGHT_BASE_URL:
                    echo %PLAYWRIGHT_BASE_URL%

                    echo.

                    echo PLAYWRIGHT_HEADLESS:
                    echo %PLAYWRIGHT_HEADLESS%


                    echo.
                    echo ==========================================
                    echo CHECKING CHROMIUM
                    echo ==========================================

                    if not exist "%LOCALAPPDATA%\\ms-playwright" (

                        echo Playwright browsers directory not found.

                        echo Installing Chromium...

                        mvn -f "%MAVEN_POM%" ^
                        -Dexec.mainClass=com.microsoft.playwright.CLI ^
                        -Dexec.classpathScope=test ^
                        exec:java ^
                        "-Dexec.args=install chromium"

                        if errorlevel 1 (

                            echo ERROR:
                            echo Chromium installation failed.

                            exit /b 1
                        )
                    )


                    echo.
                    echo ==========================================
                    echo RUNNING EXAMPLETEST
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
            echo 'QUIZAPP DEPLOYMENT SUCCESSFUL'
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

            echo 'Check the failed stage in the Jenkins console.'

            echo '=========================================='
        }
    }
}