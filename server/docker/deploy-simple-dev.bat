@echo off
chcp 65001 >nul
echo ========================================
echo 远程部署脚本（开发环境）
echo ========================================

REM 配置信息 - 部署前请修改为实际值
set SERVER_IP=YOUR_DEV_SERVER_IP
set SERVER_PORT=22
set SERVER_USER=root
set SERVER_PASSWORD=YOUR_PASSWORD
set CONTAINER_NAME=server-dev
set IMAGE_NAME=server:dev
set JAR_NAME=website_server-1.0.0.jar

echo 服务器配置：
echo IP地址: %SERVER_IP%
echo 端口: %SERVER_PORT%
echo 用户名: %SERVER_USER%
echo 容器名称: %CONTAINER_NAME%
echo.

REM 创建jar存放目录
if not exist "jar" mkdir jar

REM 本地构建jar包
echo 正在本地构建jar包...
cd ..
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo Maven构建失败！
    pause
    exit /b 1
)
cd docker

REM 复制jar包到docker目录
echo 复制jar包到docker目录...
call copy ..\target\%JAR_NAME% jar\
if %errorlevel% neq 0 (
    echo 复制jar包失败！请检查 target 目录下是否有 %JAR_NAME%
    pause
    exit /b 1
)

echo jar包存在，继续执行...

REM 检查SSH连接
echo 检查SSH连接...
ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "echo SSH连接成功"
if %errorlevel% neq 0 (
    echo SSH连接失败！请检查服务器配置
    pause
    exit /b 1
)

REM 上传jar包到服务器
echo 正在上传文件到服务器...
ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "mkdir -p /home/docker/server"
scp -o StrictHostKeyChecking=no -P %SERVER_PORT% jar\%JAR_NAME% %SERVER_USER%@%SERVER_IP%:/home/docker/server/
scp -o StrictHostKeyChecking=no -P %SERVER_PORT% Dockerfile %SERVER_USER%@%SERVER_IP%:/home/docker/server/

REM 在服务器上构建和部署
echo 正在在服务器上构建和部署...

echo === 停止并删除旧容器 ===
ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "docker stop %CONTAINER_NAME% 2>/dev/null; docker rm %CONTAINER_NAME% 2>/dev/null; echo '旧容器已清理'"

echo === 删除旧镜像 ===
ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "docker rmi %IMAGE_NAME% 2>/dev/null; echo '旧镜像已清理'"

echo === 构建Docker镜像 ===
ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "cd /home/docker/server && docker build -t %IMAGE_NAME% ."

echo === 启动应用容器 ===
ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "docker run -d --name %CONTAINER_NAME% -p 8000:8000 -v /home/fortune-telling/logs:/home/fortune-telling/logs --restart unless-stopped -e SPRING_PROFILES_ACTIVE=dev %IMAGE_NAME%"

echo === 等待容器启动 ===
ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "sleep 8"

echo === 检查容器状态 ===
ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "docker ps | grep %CONTAINER_NAME%"

echo === 查看启动日志（最后 30 行） ===
ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "docker logs --tail 30 %CONTAINER_NAME%"

echo.
echo ========================================
echo 部署完成！
echo 访问地址: http://%SERVER_IP%:8000
echo Swagger文档: http://%SERVER_IP%:8000/swagger-ui.html
echo.
echo 查看容器状态:
echo   ssh %SERVER_USER%@%SERVER_IP% "docker ps | grep %CONTAINER_NAME%"
echo 查看容器日志:
echo   ssh %SERVER_USER%@%SERVER_IP% "docker logs -f %CONTAINER_NAME%"
echo ========================================
pause
