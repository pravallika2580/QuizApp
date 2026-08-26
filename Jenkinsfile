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

        APP_JAR = 'target/quizapp.jar'

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

                    java -version
                    mvn -version

                    echo ==========================================
                    echo STARTING MAVEN BUILD
                    echo ==========================================

                    del /f /q build.log 2>nul

                    call mvn clean package -DskipTests > build.log 2>&1

                    echo MAVEN EXIT CODE: %errorlevel%

                    echo.
                    echo ==========================================
                    echo LAST 50 LINES OF MAVEN BUILD LOG
                    echo ==========================================

                    powershell -Command "Get-Content build.log -Tail 50"

                    if errorlevel 1 (
                        echo.
                        echo ==========================================
                        echo MAVEN BUILD FAILED - CHECK LOG ABOVE
                        echo ==========================================
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo SEARCHING FOR JAR FILES
                    echo ==========================================

                    dir /s /b target\\*.jar 2>nul

                    echo.
                    echo CHECKING QUIZAPP JAR
                    echo ==========================================

                    if exist "target\\quizapp.jar" (
                        echo FOUND: target\\quizapp.jar
                    ) else if exist "target\\quizapp-0.0.1-SNAPSHOT.jar" (
                        echo FOUND: target\\quizapp-0.0.1-SNAPSHOT.jar
                        echo Renaming to quizapp.jar...
                        ren "target\\quizapp-0.0.1-SNAPSHOT.jar" "quizapp.jar"
                    ) else (
                        echo.
                        echo ERROR: No QuizApp JAR found.
                        echo Listing target directory:
                        dir target 2>nul
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo JAR FOUND - BUILD PASSED
                    echo ==========================================
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

                    REM ------------------------------------------------
                    REM CHECK JAR
                    REM ------------------------------------------------

                    echo.
                    echo CHECKING QUIZAPP JAR
                    echo ==========================================

                    if exist "target\\quizapp.jar" (
                        echo QuizApp JAR found: target\\quizapp.jar
                    ) else if exist "target\\quizapp-0.0.1-SNAPSHOT.jar" (
                        echo QuizApp JAR found: target\\quizapp-0.0.1-SNAPSHOT.jar
                        ren "target\\quizapp-0.0.1-SNAPSHOT.jar" "quizapp.jar"
                    ) else (
                        echo ERROR:
                        echo QuizApp JAR not found.
                        echo Expected:
                        echo target\\quizapp.jar
                        dir /s /b target\\*.jar 2>nul
                        exit /b 1
                    )

                    REM ------------------------------------------------
                    REM CHECK PORT 8080
                    REM ------------------------------------------------

                    echo.
                    echo CHECKING PORT 8080
                    echo ==========================================

                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (

                        echo Found process %%a on port 8080

                        echo Stopping process %%a

                        taskkill /F /PID %%a >nul 2>&1

                    )

                    REM ------------------------------------------------
                    REM WAIT
                    REM ------------------------------------------------

                    echo.
                    echo WAITING FOR PORT 8080
                    echo ==========================================

                    ping 127.0.0.1 -n 4 >nul

                    REM ------------------------------------------------
                    REM START BACKEND
                    REM ------------------------------------------------

                    echo.
                    echo STARTING QUIZAPP
                    echo ==========================================

                    set "JAVA_HOME=C:\\Program Files\\Java\\jdk-17.0.2"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"

                    set "JENKINS_NODE_COOKIE=dontKillMe"

                    echo Starting Spring Boot application...

                    start "QuizApp-Backend" /B cmd /c ^
                    "set JENKINS_NODE_COOKIE=dontKillMe && java -jar target\\quizapp.jar > backend.log 2>&1"

                    echo.
                    echo QUIZAPP START COMMAND EXECUTED

                    echo.
                    echo WAITING FOR APPLICATION TO START
                    echo ==========================================

                    ping 127.0.0.1 -n 6 >nul

                    REM ------------------------------------------------
                    REM CHECK PROCESS / LOG
                    REM ------------------------------------------------

                    echo.
                    echo CHECKING BACKEND LOG
                    echo ==========================================

                    if exist backend.log (

                        echo Backend log found.

                        echo.
                        echo Last few lines of backend.log:
                        powershell -Command "Get-Content backend.log -Tail 20"

                    ) else (

                        echo WARNING:
                        echo backend.log not found.

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

                    echo.
                    echo Backend URL:
                    echo %BACKEND_URL%

                    echo.

                    set RETRIES=20

                    :CHECK_BACKEND

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

                    echo.
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

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo DEPLOYING APPZILLON QUIZAPP
                    echo ==========================================

                    REM ------------------------------------------------
                    REM CHECK WAR
                    REM ------------------------------------------------

                    echo.
                    echo CHECKING QUIZAPP WAR
                    echo ==========================================

                    if not exist "%APPZ_ARTIFACTS%\\QuizApp.war" (

                        echo ERROR:
                        echo QuizApp.war not found.

                        echo Expected location:
                        echo %APPZ_ARTIFACTS%\\QuizApp.war

                        exit /b 1
                    )

                    echo QuizApp.war found.

                    REM ------------------------------------------------
                    REM CHECK TOMCAT
                    REM ------------------------------------------------

                    echo.
                    echo TOMCAT HOME
                    echo ==========================================

                    echo %APPZ_HOME%

                    if not exist "%APPZ_HOME%\\bin\\catalina.bat" (

                        echo ERROR:
                        echo catalina.bat not found.

                        echo Expected:
                        echo %APPZ_HOME%\\bin\\catalina.bat

                        exit /b 1
                    )

                    REM ------------------------------------------------
                    REM STOP TOMCAT
                    REM ------------------------------------------------

                    echo.
                    echo ==========================================
                    echo STOPPING TOMCAT
                    echo ==========================================

                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8111 ^| findstr LISTENING') do (

                        echo Stopping Tomcat process %%a

                        taskkill /F /PID %%a >nul 2>&1

                    )

                    echo.
                    echo WAITING FOR TOMCAT TO STOP
                    echo ==========================================

                    ping 127.0.0.1 -n 6 >nul

                    REM ------------------------------------------------
                    REM REMOVE OLD APPLICATION
                    REM ------------------------------------------------

                    echo.
                    echo ==========================================
                    echo REMOVING OLD QUIZAPP
                    echo ==========================================

                    rmdir /S /Q "%APPZ_HOME%\\webapps\\QuizApp" >nul 2>&1

                    del /F /Q "%APPZ_HOME%\\webapps\\QuizApp.war" >nul 2>&1

                    REM ------------------------------------------------
                    REM COPY WAR
                    REM ------------------------------------------------

                    echo.
                    echo ==========================================
                    echo COPYING QUIZAPP.WAR
                    echo ==========================================

                    copy /Y ^
                    "%APPZ_ARTIFACTS%\\QuizApp.war" ^
                    "%APPZ_HOME%\\webapps\\QuizApp.war"

                    if errorlevel 1 (

                        echo.
                        echo ERROR COPYING QuizApp.war

                        exit /b 1
                    )

                    echo.
                    echo QuizApp.war copied successfully.

                    REM ------------------------------------------------
                    REM START TOMCAT
                    REM ------------------------------------------------

                    echo.
                    echo ==========================================
                    echo STARTING TOMCAT
                    echo ==========================================

                    set "JENKINS_NODE_COOKIE=dontKillMe"

                    start "QuizApp-Tomcat" /B cmd /c ^
                    "set JENKINS_NODE_COOKIE=dontKillMe && call %APPZ_HOME%\\bin\\catalina.bat run > %APPZ_HOME%\\logs\\jenkins-run.log 2>&1"

                    echo.
                    echo TOMCAT START COMMAND EXECUTED

                    echo.
                    echo WAITING FOR TOMCAT
                    echo ==========================================

                    ping 127.0.0.1 -n 8 >nul

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

                    echo.
                    echo URL:
                    echo %APPZILLON_URL%

                    echo.

                    set RETRIES=45

                    :CHECK_APPZILLON

                    echo Checking Appzillon...
                    echo Remaining attempts: %RETRIES%

                    curl -s -o nul -w "%%{http_code}" "%APPZILLON_URL%" | findstr "200 302"

                    if not errorlevel 1 (

                        echo.
                        echo ==========================================
                        echo APPZILLON IS RUNNING
                        echo ==========================================

                        echo Appzillon URL:
                        echo %APPZILLON_URL%

                        exit /b 0
                    )

                    echo.
                    echo Appzillon not ready.

                    set /a RETRIES-=1

                    if %RETRIES% LEQ 0 (

                        echo.
                        echo ==========================================
                        echo APPZILLON FAILED TO START
                        echo ==========================================

                        echo.
                        echo TOMCAT LOG
                        echo ==========================================

                        if exist "%APPZ_HOME%\\logs\\jenkins-run.log" (

                            type "%APPZ_HOME%\\logs\\jenkins-run.log"

                        ) else (

                            echo Tomcat log not found.

                        )

                        exit /b 1
                    )

                    echo Waiting 5 seconds before retry...

                    ping 127.0.0.1 -n 6 >nul

                    goto CHECK_APPZILLON
                '''
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
