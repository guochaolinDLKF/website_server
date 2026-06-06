@echo off
chcp 65001 >nul
echo ========================================
echo  部署脚本（开发环境）
echo ========================================
echo.

if not exist "deploy-config.bat" (
    echo [错误] 找不到 deploy-config.bat！
    pause
    exit /b 1
)
call deploy-config.bat
echo 配置加载成功，服务器 IP: %DEV_SERVER_IP%

set SERVER_IP=%DEV_SERVER_IP%
set SERVER_PORT=%DEV_SERVER_PORT%
set SERVER_USER=%DEV_SERVER_USER%
set SERVER_PASSWORD=%DEV_SERVER_PASSWORD%
set CONTAINER_NAME=website_server-dev
set IMAGE_NAME=website_server:dev
set JAR_NAME=website_server-1.0.0.jar

if "%SERVER_IP%"=="" (
    echo [错误] DEV_SERVER_IP 为空，请检查 deploy-config.bat！
    pause
    exit /b 1
)

echo.
echo [1/8] 正在构建 jar 包...
pushd ..
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo [错误] Maven 构建失败！
    popd
    pause
    exit /b 1
)
popd
echo [1/8] jar 构建完成。

echo.
echo [2/8] 正在复制 jar 包...
if not exist "..\target\%JAR_NAME%" (
    echo [错误] jar 包不存在: ..\target\%JAR_NAME%
    pause
    exit /b 1
)
if not exist "jar" mkdir jar
copy "..\target\%JAR_NAME%" "jar\" >nul
if %errorlevel% neq 0 (
    echo [错误] jar 复制失败！
    pause
    exit /b 1
)
echo [2/8] jar 复制完成。

echo.
echo [3/8] 正在检查 SSH 连接...
ssh -o StrictHostKeyChecking=no -o ConnectTimeout=10 -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "echo OK"
if %errorlevel% neq 0 (
    echo [错误] SSH 连接失败！目标: %SERVER_USER%@%SERVER_IP%:%SERVER_PORT%
    echo 请确认:
    echo   1. deploy-config.bat 中的服务器 IP 正确
    echo   2. 已配置 SSH 密钥: ssh %SERVER_USER%@%SERVER_IP%
    echo   3. 服务器防火墙已开放 %SERVER_PORT% 端口
    pause
    exit /b 1
)
echo [3/8] SSH 连接正常。

echo.
echo [4/8] 正在上传文件到服务器...
ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "mkdir -p /home/docker/website_server"
scp -o StrictHostKeyChecking=no -P %SERVER_PORT% "jar\%JAR_NAME%" %SERVER_USER%@%SERVER_IP%:/home/docker/website_server/
if %errorlevel% neq 0 ( echo [错误] jar 上传失败！ & pause & exit /b 1 )
scp -o StrictHostKeyChecking=no -P %SERVER_PORT% Dockerfile %SERVER_USER%@%SERVER_IP%:/home/docker/website_server/
if %errorlevel% neq 0 ( echo [错误] Dockerfile 上传失败！ & pause & exit /b 1 )
echo [4/8] 文件上传完成。

echo.
echo [5/8] 正在停止旧容器...
ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "docker stop %CONTAINER_NAME% 2>/dev/null; docker rm %CONTAINER_NAME% 2>/dev/null; echo done"
echo [5/8] 旧容器已清理。

echo.
echo [6/8] 正在构建 Docker 镜像...
ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "cd /home/docker/website_server && docker build -t %IMAGE_NAME% ."
if %errorlevel% neq 0 ( echo [错误] Docker 镜像构建失败！ & pause & exit /b 1 )
echo [6/8] 镜像构建完成。

echo.
echo [7/8] 正在启动容器...
ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "docker run -d --name %CONTAINER_NAME% -p 8660:8660 -v /home/website_server/logs:/home/website_server/logs --restart unless-stopped -e SPRING_PROFILES_ACTIVE=dev %IMAGE_NAME%"
if %errorlevel% neq 0 ( echo [错误] 容器启动失败！ & pause & exit /b 1 )
echo [7/8] 容器已启动。

echo.
echo [8/8] 正在验证部署结果...
ssh -o StrictHostKeyChecking=no -p %SERVER_PORT% %SERVER_USER%@%SERVER_IP% "sleep 8 && docker ps | grep %CONTAINER_NAME% && echo --- && docker logs --tail 20 %CONTAINER_NAME%"

echo.
echo ========================================
echo  部署完成！
echo  访问地址: http://%SERVER_IP%:8660
echo  Swagger:  http://%SERVER_IP%:8660/swagger-ui.html
echo ========================================
pause