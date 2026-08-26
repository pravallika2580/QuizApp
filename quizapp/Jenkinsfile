pipeline {

    agent any

    environment {

        // ============================================================
        // JAVA
        // ============================================================

        JAVA_HOME = 'C:/Program Files/Java/jdk-17.0.2'


        // ============================================================
        // SPRING BOOT
        // ============================================================

        APP_JAR = 'target/quizapp-0.0.1-SNAPSHOT.jar'

        BACKEND_PORT = '8080'

        BACKEND_URL =
            'http://localhost:8080/api/user/quizzes'


        // ============================================================
        // TOMCAT / APPZILLON
        // ============================================================

        APPZ_HOME =
            'C:/Users/pravallika.k/Downloads/apache-tomcat-9.0.53 2/apache-tomcat-9.0.53'

        APPZ_ARTIFACTS =
            'D:/jenkins-testing'

        TOMCAT_PORT = '8111'

        APPZILLON_URL =
            'http://localhost:8111/QuizApp/'
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

                git branch: 'main',
                    url: 'https://github.com/pravallika2580/QuizApp.git'

                echo 'QUIZAPP CHECKOUT SUCCESSFUL'
            }
        }


        // ============================================================
        // BUILD BACKEND
        // ============================================================

        stage('Build Backend Jar') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo BUILDING QUIZAPP BACKEND
                    echo ==========================================

                    set "JAVA_HOME=C:\\Program Files\\Java\\jdk-17.0.2"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"

                    echo.
                    echo JAVA VERSION
                    java -version

                    echo.
                    echo MAVEN VERSION
                    mvn -version

                    echo.
                    echo STARTING MAVEN BUILD
                    echo ==========================================

                    call mvn clean package -DskipTests

                    if errorlevel 1 (
                        echo.
                        echo ==========================================
                        echo MAVEN BUILD FAILED
                        echo ==========================================
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo MAVEN BUILD SUCCESSFUL
                    echo ==========================================

                    echo.
                    echo TARGET DIRECTORY:
                    dir target

                    if not exist "target\\quizapp-0.0.1-SNAPSHOT.jar" (
                        echo.
                        echo ERROR: QuizApp JAR was not created.
                        exit /b 1
                    )

                    echo.
                    echo JAR FOUND:
                    echo target\\quizapp-0.0.1-SNAPSHOT.jar
                '''
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


                    echo.
                    echo CHECKING PORT 8080
                    echo ==========================================

                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (

                        echo Found process %%a on port 8080

                        taskkill /F /PID %%a >nul 2>&1

                    )


                    echo.
                    echo WAITING FOR PORT 8080
                    timeout /t 3 /nobreak >nul


                    echo.
                    echo STARTING QUIZAPP
                    echo ==========================================

                    set "JAVA_HOME=C:\\Program Files\\Java\\jdk-17.0.2"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"

                    set JENKINS_NODE_COOKIE=dontKillMe

                    start "QuizApp-Backend" /B cmd /c ^
                    "java -jar target\\quizapp-0.0.1-SNAPSHOT.jar > backend.log 2>&1"


                    echo.
                    echo QUIZAPP START COMMAND EXECUTED

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

                    echo Checking backend...

                    curl -s -o nul -w "%%{http_code}" "%BACKEND_URL%" | findstr "200 201"

                    if not errorlevel 1 (

                        echo.
                        echo ==========================================
                        echo BACKEND IS RUNNING
                        echo ==========================================

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
                        echo BACKEND LOG:
                        echo ==========================================

                        if exist backend.log (
                            type backend.log
                        ) else (
                            echo backend.log not found
                        )

                        exit /b 1
                    )


                    timeout /t 3 /nobreak >nul

                    goto CHECK_BACKEND
                '''
            }
        }


        // ============================================================
        // DEPLOY APPZILLON
        // ============================================================

        stage('Deploy Appzillon') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo DEPLOYING APPZILLON QUIZAPP
                    echo ==========================================


                    if not exist "%APPZ_ARTIFACTS%\\QuizApp.war" (

                        echo ERROR:
                        echo QuizApp.war not found.

                        echo Expected location:
                        echo %APPZ_ARTIFACTS%\\QuizApp.war

                        exit /b 1
                    )


                    echo QuizApp.war found.


                    echo.
                    echo ==========================================
                    echo STOPPING TOMCAT
                    echo ==========================================

                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8111 ^| findstr LISTENING') do (

                        echo Stopping Tomcat process %%a

                        taskkill /F /PID %%a >nul 2>&1

                    )


                    timeout /t 5 /nobreak >nul


                    echo.
                    echo ==========================================
                    echo REMOVING OLD QUIZAPP
                    echo ==========================================

                    rmdir /S /Q "%APPZ_HOME%\\webapps\\QuizApp" >nul 2>&1

                    del /F /Q "%APPZ_HOME%\\webapps\\QuizApp.war" >nul 2>&1


                    echo.
                    echo ==========================================
                    echo COPYING QUIZAPP.WAR
                    echo ==========================================

                    copy /Y ^
                    "%APPZ_ARTIFACTS%\\QuizApp.war" ^
                    "%APPZ_HOME%\\webapps\\QuizApp.war"


                    if errorlevel 1 (

                        echo ERROR COPYING QuizApp.war

                        exit /b 1
                    )


                    echo.
                    echo ==========================================
                    echo STARTING TOMCAT
                    echo ==========================================

                    set JENKINS_NODE_COOKIE=dontKillMe

                    start "QuizApp-Tomcat" /B cmd /c ^
                    "call %APPZ_HOME%\\bin\\catalina.bat run > %APPZ_HOME%\\logs\\jenkins-run.log 2>&1"


                    echo TOMCAT START COMMAND EXECUTED

                '''
            }
        }


        // ============================================================
        // APPZILLON HEALTH CHECK
        // ============================================================

        stage('Appzillon Health Check') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo CHECKING APPZILLON
                    echo ==========================================

                    echo URL:
                    echo %APPZILLON_URL%

                    set RETRIES=45


                    :CHECK_APPZILLON

                    echo Checking Appzillon...

                    curl -s -o nul -w "%%{http_code}" "%APPZILLON_URL%" | findstr "200 302"

                    if not errorlevel 1 (

                        echo.
                        echo ==========================================
                        echo APPZILLON IS RUNNING
                        echo ==========================================

                        exit /b 0
                    )


                    echo Appzillon not ready.

                    set /a RETRIES-=1


                    if %RETRIES% LEQ 0 (

                        echo.
                        echo ==========================================
                        echo APPZILLON FAILED TO START
                        echo ==========================================

                        echo.
                        echo TOMCAT LOG:
                        echo ==========================================

                        if exist "%APPZ_HOME%\\logs\\jenkins-run.log" (
                            type "%APPZ_HOME%\\logs\\jenkins-run.log"
                        )

                        exit /b 1
                    )


                    timeout /t 5 /nobreak >nul

                    goto CHECK_APPZILLON
                '''
            }
        }
    }


    // ============================================================
    // POST
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

            echo '=========================================='
        }


        failure {

            echo '=========================================='
            echo 'QUIZAPP DEPLOYMENT FAILED'
            echo '=========================================='

            echo 'Check the stage that failed.'

            echo '=========================================='
        }
    }
}