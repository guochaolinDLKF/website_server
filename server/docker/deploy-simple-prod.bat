@echo off
setlocal enabledelayedexpansion
chcp 65001 >nul
echo ========================================
echo  Deploy Script (PROD)
echo ========================================
echo.

if not exist "deploy-config.bat" (
    echo [ERROR] deploy-config.bat not found!
    pause
    exit /b 1
)
call deploy-config.bat

set SERVER_IP=%PROD_SERVER_IP%
set SERVER_PORT=%PROD_SERVER_PORT%
set SERVER_USER=%PROD_SERVER_USER%
set SERVER_PASSWORD=%PROD_SERVER_PASSWORD%
set CONTAINER_NAME=website_server-prod
set IMAGE_NAME=website_server:prod
set JAR_NAME=website_server-1.0.0.jar

if "%SERVER_IP%"=="" (
    echo [ERROR] PROD_SERVER_IP is empty! Check deploy-config.bat
    pause
    exit /b 1
)

echo [1/8] Building jar ...
pushd ..
call mvn clean package -DskipTests
if !errorlevel! neq 0 ( echo [ERROR] Maven build failed! & popd & pause & exit /b 1 )
popd
echo [1/8] OK.

echo [2/8] Copying jar ...
if not exist "..\target\%JAR_NAME%" ( echo [ERROR] Jar not found! & pause & exit /b 1 )
if not exist "jar" mkdir jar
copy "..\target\%JAR_NAME%" "jar\" >nul
if !errorlevel! neq 0 ( echo [ERROR] Jar copy failed! & pause & exit /b 1 )
echo [2/8] OK.

echo [3/8] Checking SSH ...
ssh -o StrictHostKeyChecking=no -o ConnectTimeout=10 -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "echo OK"
if !errorlevel! neq 0 ( echo [ERROR] SSH failed to %SERVER_USER%@%SERVER_IP% & pause & exit /b 1 )
echo [3/8] OK.

echo [4/8] Uploading files ...
ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "mkdir -p /home/docker/website_server"
scp -o StrictHostKeyChecking=no -P %SERVER_PORT% "jar\%JAR_NAME%" %SERVER_USER%@%SERVER_IP%:/home/docker/website_server/
scp -o StrictHostKeyChecking=no -P %SERVER_PORT% Dockerfile %SERVER_USER%@%SERVER_IP%:/home/docker/website_server/
if !errorlevel! neq 0 ( echo [ERROR] Upload failed! & pause & exit /b 1 )
echo [4/8] OK.

echo [5/8] Stopping old container ...
ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "docker stop %CONTAINER_NAME% 2>/dev/null; docker rm %CONTAINER_NAME% 2>/dev/null; echo done"
echo [5/8] OK.

echo [6/8] Building image on server ...
ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "cd /home/docker/website_server && docker build -t %IMAGE_NAME% ."
if !errorlevel! neq 0 ( echo [ERROR] Docker build failed! & pause & exit /b 1 )
echo [6/8] OK.

echo [7/8] Starting container ...
ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "docker run -d --name %CONTAINER_NAME% -p 8660:8660 -v /home/website_server/logs:/home/website_server/logs --restart unless-stopped -e SPRING_PROFILES_ACTIVE=prod %IMAGE_NAME%"
if !errorlevel! neq 0 ( echo [ERROR] Container start failed! & pause & exit /b 1 )
echo [7/8] OK.

echo [8/8] Verifying ...
ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "sleep 8 && docker ps | grep %CONTAINER_NAME% && echo --- && docker logs --tail 20 %CONTAINER_NAME%"
echo.
echo ========================================
echo  Deploy Complete!  URL: http://%SERVER_IP%:8660
echo ========================================
pause
