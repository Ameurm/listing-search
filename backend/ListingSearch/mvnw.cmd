@echo off
setlocal
set "MAVEN_VERSION=3.9.9"
set "WRAPPER_DIR=%USERPROFILE%\.m2\wrapper\dists"
set "MAVEN_HOME=%WRAPPER_DIR%\apache-maven-%MAVEN_VERSION%"
set "MAVEN_BIN=%MAVEN_HOME%\bin\mvn.cmd"
if exist "%MAVEN_BIN%" goto run_maven
if not exist "%WRAPPER_DIR%" mkdir "%WRAPPER_DIR%"
echo Maven %MAVEN_VERSION% is not installed. Downloading Maven distribution...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$ProgressPreference='SilentlyContinue'; $u='https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip'; $z=Join-Path $env:TEMP 'apache-maven-3.9.9-bin.zip'; Invoke-WebRequest -Uri $u -OutFile $z; Expand-Archive -Path $z -DestinationPath '%WRAPPER_DIR%' -Force"
if errorlevel 1 exit /b 1
:run_maven
call "%MAVEN_BIN%" %*
exit /b %ERRORLEVEL%
