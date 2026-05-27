@echo off
REM ============================================
REM Deploy config (sensitive, NOT committed to git)
REM Fill in your real server info below
REM ============================================

REM ---------- DEV environment ----------
set DEV_SERVER_IP=122.51.175.128
set DEV_SERVER_PORT=22
set DEV_SERVER_USER=root
set DEV_SERVER_PASSWORD=yd.zzCC#$keji

REM ---------- PROD environment ----------
set PROD_SERVER_IP=47.101.202.247
set PROD_SERVER_PORT=22
set PROD_SERVER_USER=root
set PROD_SERVER_PASSWORD=yd.zzCC#$keji

REM ---------- ARRAIGNMENT environment ----------
set ARRAIGNMENT_SERVER_IP=47.100.111.124
set ARRAIGNMENT_SERVER_PORT=22
set ARRAIGNMENT_SERVER_USER=root
set ARRAIGNMENT_SERVER_PASSWORD=your_real_password