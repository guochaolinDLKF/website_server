@echo off
chcp 65001 >nul
REM ============================================================
REM  开发态一键启动：后端(8660) + 前端(9660, 带热更新 HMR)
REM  会打开两个独立命令行窗口，分别运行后端与前端。
REM  前置：MySQL、Redis 已启动；website 库已执行 admin_init.sql。
REM  访问：http://localhost:9660/  （前端开发服务器，/api 已代理到 8660）
REM ============================================================
echo 正在启动后端(8660) 与 前端(9660, HMR)...
start "admin-backend"  cmd /k "cd /d %~dp0server && mvn spring-boot:run"
start "admin-frontend" cmd /k "cd /d %~dp0admin-web && npm run dev"
echo 已在两个新窗口分别启动后端与前端。关闭对应窗口即可停止。
