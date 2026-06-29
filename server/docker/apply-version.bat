@echo off
chcp 65001 >nul

if not exist "deploy-config.bat" goto :err_cfg
call deploy-config.bat
if "%PROJECT_VERSION%"=="" goto :err_ver
if "%PROJECT_ARTIFACT_ID%"=="" goto :err_art

echo Config loaded.
echo   PROJECT_ARTIFACT_ID = %PROJECT_ARTIFACT_ID%
echo   PROJECT_VERSION     = %PROJECT_VERSION%
echo.

set "POM=..\pom.xml"
set "DF=Dockerfile"

if not exist "%POM%" goto :err_pom
echo [1/2] syncing pom.xml ...
REM Use regex \d+\.\d+\.\d+ so that ANY prior version (1.0.0 or 1.0.3 etc) gets
REM replaced. This makes forward bumps AND rolls-back both work in one bat run.
powershell -NoProfile -ExecutionPolicy Bypass -Command "(Get-Content -LiteralPath '%POM%' -Raw -Encoding UTF8) -replace '<artifactId>website_server</artifactId>', '<artifactId>%PROJECT_ARTIFACT_ID%</artifactId>' -replace '<version>\d+\.\d+\.\d+</version>', '<version>%PROJECT_VERSION%</version>' -replace '<name>website_server</name>', '<name>%PROJECT_ARTIFACT_ID%</name>' | Set-Content -LiteralPath '%POM%' -Encoding UTF8 -NoNewline"
if errorlevel 1 goto :err_pwsh
echo [1/2] pom.xml synced.
echo.

if not exist "%DF%" goto :err_df
echo [2/2] syncing Dockerfile ...
REM Same regex approach. Replaces any existing version default with the deploy-config one.
powershell -NoProfile -ExecutionPolicy Bypass -Command "(Get-Content -LiteralPath '%DF%' -Raw -Encoding UTF8) -replace 'ARG BUILD_ARTIFACT_ID=\S+', 'ARG BUILD_ARTIFACT_ID=%PROJECT_ARTIFACT_ID%' -replace 'ARG BUILD_VERSION=\d+\.\d+\.\d+', 'ARG BUILD_VERSION=%PROJECT_VERSION%' | Set-Content -LiteralPath '%DF%' -Encoding UTF8 -NoNewline"
if errorlevel 1 goto :err_pwsh
echo [2/2] Dockerfile synced.
echo.

echo Done.
echo.
pause
exit /b 0

:err_cfg
echo [X] deploy-config.bat not found in server\docker\
pause
exit /b 1
:err_ver
echo [X] PROJECT_VERSION not set in deploy-config.bat
echo.
echo   Please open server\docker\deploy-config.bat and set:
echo.
echo       set PROJECT_VERSION=1.0.0
echo.
echo   under section "1. Project artifactId / version (edit here when releasing)".
pause
exit /b 1
:err_art
echo [X] PROJECT_ARTIFACT_ID not set in deploy-config.bat
echo.
echo   Please open server\docker\deploy-config.bat and set:
echo.
echo       set PROJECT_ARTIFACT_ID=website_server
echo.
echo   under section "1. Project artifactId / version (edit here when releasing)".
pause
exit /b 1
:err_pom
echo [X] pom.xml not found at %POM%
pause
exit /b 1
:err_df
echo [X] Dockerfile not found at %DF%
pause
exit /b 1
:err_pwsh
echo [X] PowerShell sync failed. Check that PowerShell 5.1+ is installed and execution policy allows scripts.
pause
exit /b 1