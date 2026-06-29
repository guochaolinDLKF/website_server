@echo off
chcp 65001 >nul
echo ========================================
echo  部署脚本（生产环境 - expect 自动输密码模式）
echo ========================================
echo.

if exist "deploy-config.bat" (
    call deploy-config.bat
) else (
    echo [错误] 找不到 deploy-config.bat！
    pause
    exit /b 1
)

set SERVER_IP=%PROD_SERVER_IP%
set SERVER_PORT=%PROD_SERVER_PORT%
set SERVER_USER=%PROD_SERVER_USER%
set SERVER_PASSWORD=%PROD_SERVER_PASSWORD%
set CONTAINER_NAME=%CONTAINER_NAME_PROD%
set IMAGE_NAME=%IMAGE_NAME_PROD%

echo 服务器: %SERVER_USER%@%SERVER_IP%:%SERVER_PORT%
echo.

if not exist jar\%JAR_NAME% (
    echo [错误] 找不到 jar 包，请先跑 mvn clean package
    pause
    exit /b 1
)

echo 正在通过 expect 模式上传...
echo.

echo 创建远程部署目录...
expect -c "
set timeout 30
spawn ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% \"mkdir -p %REMOTE_DEPLOY_DIR%\"
expect \"password:\"
send \"%SERVER_PASSWORD%\r\"
expect eof
"

echo 上传 jar 包...
expect -c "
set timeout 60
spawn scp -o StrictHostKeyChecking=no -P %SERVER_PORT% jar/%JAR_NAME% %SERVER_USER%@%SERVER_IP%:%REMOTE_DEPLOY_DIR%/
expect \"password:\"
send \"%SERVER_PASSWORD%\r\"
expect eof
"

echo 上传 Dockerfile...
expect -c "
set timeout 30
spawn scp -o StrictHostKeyChecking=no -P %SERVER_PORT% Dockerfile %SERVER_USER%@%SERVER_IP%:%REMOTE_DEPLOY_DIR%/
expect \"password:\"
send \"%SERVER_PASSWORD%\r\"
expect eof
"

echo 构建镜像并启动容器...
expect -c "
set timeout 300
spawn ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% \"cd %REMOTE_DEPLOY_DIR% && docker stop %CONTAINER_NAME% 2>/dev/null; docker rm %CONTAINER_NAME% 2>/dev/null; docker rmi %IMAGE_NAME% 2>/dev/null; docker build --build-arg BUILD_ARTIFACT_ID=%PROJECT_ARTIFACT_ID% --build-arg BUILD_VERSION=%PROJECT_VERSION% -t %IMAGE_NAME% . && docker run -d --name %CONTAINER_NAME% -p %APP_PORT%:%APP_PORT% -v %REMOTE_LOG_VOLUME% --restart unless-stopped -e SPRING_PROFILES_ACTIVE=%SPRING_PROFILE_PROD% %IMAGE_NAME%\"
expect \"password:\"
send \"%SERVER_PASSWORD%\r\"
expect eof
"

echo 检查容器状态...
expect -c "
set timeout 30
spawn ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% \"docker ps | grep %CONTAINER_NAME%\"
expect \"password:\"
send \"%SERVER_PASSWORD%\r\"
expect eof
"

echo.
echo ========================================
echo  部署完成！
echo  访问地址: http://%SERVER_IP%:8660
echo ========================================
pause
