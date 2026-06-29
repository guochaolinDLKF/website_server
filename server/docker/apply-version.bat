@echo off
REM ============================================================
REM  apply-version.bat
REM  Sync PROJECT_ARTIFACT_ID and PROJECT_VERSION from
REM  deploy-config.bat into pom.xml and Dockerfile.
REM
REM  Usage: apply-version.bat
REM   1. Edit PROJECT_VERSION in deploy-config.bat
REM   2. Run this script to sync pom.xml + Dockerfile
REM   3. Run: cd .. && mvn clean package -DskipTests
REM ============================================================

setlocal enabledelayedexpansion

REM -- Get this script's directory --
set SCRIPT_DIR=%~dp0

REM -- 0. Check deploy-config.bat exists --
if not exist "%SCRIPT_DIR%deploy-config.bat" (
    echo [X] deploy-config.bat not found in "%SCRIPT_DIR%"
    exit /b 1
)

REM -- 1. Load variables from deploy-config.bat --
call "%SCRIPT_DIR%deploy-config.bat"

if "%PROJECT_ARTIFACT_ID%"=="" (
    echo [X] PROJECT_ARTIFACT_ID is empty. Check deploy-config.bat.
    exit /b 1
)
if "%PROJECT_VERSION%"=="" (
    echo [X] PROJECT_VERSION is empty. Check deploy-config.bat.
    exit /b 1
)

echo [*] Syncing: %PROJECT_ARTIFACT_ID% v%PROJECT_VERSION%
echo.

REM -- 2. Update pom.xml (targeted: only the project-level <version>) --
set POM_PATH=%SCRIPT_DIR%..\pom.xml
if not exist "%POM_PATH%" (
    echo [X] pom.xml not found: "%POM_PATH%"
    exit /b 1
)

powershell -NoProfile -Command ^
  "$c = Get-Content -Path '%POM_PATH%' -Raw -Encoding UTF8;" ^
  "$c = $c -replace ('(?<=<artifactId>' + '%PROJECT_ARTIFACT_ID%' + '</artifactId>\s*<version>)[^<]+(?=</version>)'), '%PROJECT_VERSION%';" ^
  "$c | Set-Content -Path '%POM_PATH%' -Encoding UTF8"
if %errorlevel% neq 0 (
    echo [X] Failed to update pom.xml
    exit /b 1
)
echo [1/2] pom.xml        -^> version = %PROJECT_VERSION%

REM -- 3. Update Dockerfile (ARG BUILD_ARTIFACT_ID / BUILD_VERSION) --
set DOCKERFILE_PATH=%SCRIPT_DIR%Dockerfile
powershell -NoProfile -Command ^
  "$c = Get-Content -Path '%DOCKERFILE_PATH%' -Raw;" ^
  "$c = $c -replace 'ARG BUILD_ARTIFACT_ID=\S+', 'ARG BUILD_ARTIFACT_ID=%PROJECT_ARTIFACT_ID%';" ^
  "$c = $c -replace 'ARG BUILD_VERSION=\S+', 'ARG BUILD_VERSION=%PROJECT_VERSION%';" ^
  "$c | Set-Content -Path '%DOCKERFILE_PATH%' -Encoding UTF8"
if %errorlevel% neq 0 (
    echo [X] Failed to update Dockerfile
    exit /b 1
)
echo [2/2] Dockerfile     -^> BUILD_ARTIFACT_ID=%PROJECT_ARTIFACT_ID%  BUILD_VERSION=%PROJECT_VERSION%

echo.
echo [OK] All synced. Next: cd .. ^&^& mvn clean package -DskipTests

endlocal
