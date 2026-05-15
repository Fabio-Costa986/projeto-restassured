@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup batch script
@REM ----------------------------------------------------------------------------
@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __ MVNW_CMD__=
@SETLOCAL

@SET MAVEN_PROJECTBASEDIR=%~dp0
@IF "%MAVEN_PROJECTBASEDIR:~-1%"=="\" SET MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

@SET WRAPPER_DIR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper
@SET WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar
@SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

@SET DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

@IF EXIST %WRAPPER_JAR% (
    @SET MVNW_VERBOSE=false
) ELSE (
    @ECHO Downloading Maven Wrapper JAR...
    @powershell -Command "& {Invoke-WebRequest -Uri %DOWNLOAD_URL% -OutFile '%WRAPPER_JAR%'}"
)

@SET MAVEN_USER_HOME=%USERPROFILE%\.m2
@SET MAVEN_WRAPPER_JAR=%WRAPPER_JAR%

@java -classpath %WRAPPER_JAR% "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" %WRAPPER_LAUNCHER% %MAVEN_CONFIG% %*
@SET MVNW_RET_CODE=%ERRORLEVEL%

@ENDLOCAL
@EXIT /B %MVNW_RET_CODE%
