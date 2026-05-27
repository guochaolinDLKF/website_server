@echo off
chcp 65001 >nul
echo ========================================
echo 远程部署脚本（生产环境 - expect 方式）
echo ========================================

REM 配置信息 - 部署前请修改为实际值
set SERVER_IP=YOUR_PROD_SERVER_IP
set SERVER_PORT=22
set SERVER_USER=root
set SERVER_PASSWORD=YOUR_PASSWORD
set CONTAINER_NAME=server-prod
set IMAGE_NAME=server:prod
set JAR_NAME=website_server-1.0.0.jar

echo 服务器配置：
echo IP地址: %SERVER_IP%
echo 端口: %SERVER_PORT%
echo 用户名: %SERVER_USER%
echo.

REM 检查jar包
if not exist jar\%JAR_NAME% (
    echo jar包不存在，请先构建项目！
    pause
    exit /b 1
)

echo jar包存在，开始部署...

REM 使用expect脚本处理SSH连接（自动输入密码）
echo 正在连接服务器并部署...

echo 创建远程目录...
expect -c "
set timeout 30
spawn ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% \"mkdir -p /home/docker/server\"
expect \"password:\"
send \"%SERVER_PASSWORD%\r\"
expect eof
"

echo 上传jar包...
expect -c "
set timeout 60
spawn scp -o StrictHostKeyChecking=no -P %SERVER_PORT% jar/%JAR_NAME% %SERVER_USER%@%SERVER_IP%:/home/docker/server/
expect \"password:\"
send \"%SERVER_PASSWORD%\r\"
expect eof
"

echo 上传Dockerfile...
expect -c "
set timeout 30
spawn scp -o StrictHostKeyChecking=no -P %SERVER_PORT% Dockerfile %SERVER_USER%@%SERVER_IP%:/home/docker/server/
expect \"password:\"
send \"%SERVER_PASSWORD%\r\"
expect eof
"

echo 构建和部署Docker镜像...
expect -c "
set timeout 300
spawn ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% \"cd /home/docker/server && docker stop %CONTAINER_NAME% 2>/dev/null; docker rm %CONTAINER_NAME% 2>/dev/null; docker rmi %IMAGE_NAME% 2>/dev/null; docker build -t %IMAGE_NAME% . && docker run -d --name %CONTAINER_NAME% -p 8000:8000 -v /home/fortune-telling/logs:/home/fortune-telling/logs --restart unless-stopped -e SPRING_PROFILES_ACTIVE=prod %IMAGE_NAME%\"
expect \"password:\"
send \"%SERVER_PASSWORD%\r\"
expect eof
"

echo 检查部署状态...
expect -c "
set timeout 30
spawn ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% \"docker ps | grep %CONTAINER_NAME%\"
expect \"password:\"
send \"%SERVER_PASSWORD%\r\"
expect eof
"

echo.
echo ========================================
echo 部署完成！
echo 访问地址: http://%SERVER_IP%:8000
echo ========================================
pause
