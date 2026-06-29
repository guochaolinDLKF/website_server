@echo off
chcp 65001 >nul
REM ============================================================
REM  部署 / 项目配置(项目统一配置中心)
REM
REM  改本本？ 编辑下面的 PROJECT_VERSION, 然后跑.
REM      server\docker\apply-version.bat
REM  自动同步 pom.xml 和 Dockerfile.
REM
REM  然后:
REM      cd server ^&^& mvn clean package -DskipTests
REM
REM  警告: 本文件含服务器码.
REM  建议: 不要提交 git (deploy-config.example.bat 作模板)
REM ============================================================

REM ============================================================
REM  1. 项目 artifactId / 版本 (发版时改)
REM ============================================================
set PROJECT_ARTIFACT_ID=website_server
set PROJECT_VERSION=1.0.1

REM 自动派生的部署变量 (不要手改)
set JAR_NAME=%PROJECT_ARTIFACT_ID%-%PROJECT_VERSION%.jar
set APP_PORT=8660
set CONTAINER_NAME_DEV=%PROJECT_ARTIFACT_ID%-dev
set CONTAINER_NAME_PROD=%PROJECT_ARTIFACT_ID%-prod
set IMAGE_NAME_DEV=%PROJECT_ARTIFACT_ID%:dev
set IMAGE_NAME_PROD=%PROJECT_ARTIFACT_ID%:prod

REM ============================================================
REM  2. Spring Boot / Docker 使议 (很少改)
REM ============================================================
set REMOTE_DEPLOY_DIR=/home/docker/website_server
set REMOTE_APP_LOG_DIR=/home/%PROJECT_ARTIFACT_ID%/logs
set REMOTE_LOG_VOLUME=%REMOTE_APP_LOG_DIR%:%REMOTE_APP_LOG_DIR%
set SPRING_PROFILE_DEV=dev
set SPRING_PROFILE_PROD=prod

REM ============================================================
REM  3. DEV / PROD 服务器连接 (敏感)
REM ============================================================

REM ----- 开发环境 -----
set DEV_SERVER_IP=122.51.175.128
set DEV_SERVER_PORT=22
set DEV_SERVER_USER=root
set DEV_SERVER_PASSWORD=yd.zzCC#$keji

REM ----- 生产环境 -----
set PROD_SERVER_IP=47.100.111.124
set PROD_SERVER_PORT=22
set PROD_SERVER_USER=root
set PROD_SERVER_PASSWORD=yd.zzCC#$keji
