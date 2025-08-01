@echo off
setlocal
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.2.13-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "ROOT_DIR=%~dp0"
set "DIAMOND_JAR=%ROOT_DIR%Diamond-API\build\libs"
set "KAKUREMBO_JAR=%ROOT_DIR%Kakurembo\build\libs"
set "KAKUREMBO_FAKEJAR=%ROOT_DIR%build\libs"
set "OUTPUT_DIR=%ROOT_DIR%build\libs"
del /Q "%DIAMOND_JAR%\*.jar" >nul 2>&1
del /Q "%KAKUREMBO_JAR%\*.jar" >nul 2>&1
call gradlew build || goto :error
if not exist "%DIAMOND_JAR%\*.jar" goto :error
if not exist "%KAKUREMBO_JAR%\*.jar" goto :error
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"
copy "%DIAMOND_JAR%\*.jar" "%OUTPUT_DIR%" >nul
copy "%KAKUREMBO_JAR%\*.jar" "%OUTPUT_DIR%" >nul
del /Q "%KAKUREMBO_FAKEJAR%\Kakurembo.jar" >nul 2>&1
echo Done
exit /b 0
:error
pause