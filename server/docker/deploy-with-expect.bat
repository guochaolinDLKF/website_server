@echo off
chcp 65001 >nul
echo ========================================
echo Deploy Script (PROD - expect mode)
echo ========================================

if exist "deploy-config.bat" (
    call deploy-config.bat
) else (
    echo [ERROR] deploy-config.bat not found!
    pause
    exit /b 1
)

set SERVER_IP=%PROD_SERVER_IP%
set SERVER_PORT=%PROD_SERVER_PORT%
set SERVER_USER=%PROD_SERVER_USER%
set SERVER_PASSWORD=%PROD_SERVER_PASSWORD%
set CONTAINER_NAME=website_server-prod
set IMAGE_NAME=website_server:prod
set JAR_NAME=website_server-1.0.0.jar

echo Server: %SERVER_USER%@%SERVER_IP%:%SERVER_PORT%
echo.

if not exist jar\%JAR_NAME% (
    echo jar not found! Run build first.
    pause
    exit /b 1
)

echo Uploading via expect...

echo Creating remote dir...
expect -c "
set timeout 30
spawn ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% \"mkdir -p /home/docker/server\"
expect \"password:\"
send \"%SERVER_PASSWORD%\r\"
expect eof
"

echo Uploading jar...
expect -c "
set timeout 60
spawn scp -o StrictHostKeyChecking=no -P %SERVER_PORT% jar/%JAR_NAME% %SERVER_USER%@%SERVER_IP%:/home/docker/server/
expect \"password:\"
send \"%SERVER_PASSWORD%\r\"
expect eof
"

echo Uploading Dockerfile...
expect -c "
set timeout 30
spawn scp -o StrictHostKeyChecking=no -P %SERVER_PORT% Dockerfile %SERVER_USER%@%SERVER_IP%:/home/docker/server/
expect \"password:\"
send \"%SERVER_PASSWORD%\r\"
expect eof
"

echo Building and deploying...
expect -c "
set timeout 300
spawn ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% \"cd /home/docker/server && docker stop %CONTAINER_NAME% 2>/dev/null; docker rm %CONTAINER_NAME% 2>/dev/null; docker rmi %IMAGE_NAME% 2>/dev/null; docker build -t %IMAGE_NAME% . && docker run -d --name %CONTAINER_NAME% -p 8660:8660 -v /home/website_server/logs:/home/website_server/logs --restart unless-stopped -e SPRING_PROFILES_ACTIVE=prod %IMAGE_NAME%\"
expect \"password:\"
send \"%SERVER_PASSWORD%\r\"
expect eof
"

echo Checking status...
expect -c "
set timeout 30
spawn ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% \"docker ps | grep %CONTAINER_NAME%\"
expect \"password:\"
send \"%SERVER_PASSWORD%\r\"
expect eof
"

echo.
echo ========================================
echo Deploy complete!
echo URL: http://%SERVER_IP%:8660
echo ========================================
pause
