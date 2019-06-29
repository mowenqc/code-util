@echo off
set projectDir=%1
set repository=%2
set projectType=%3
set vcs=%4
set module=%5
set gitBranch=%6
set env=%7

cd /d %projectDir%
cd /d %module%
if %projectType% EQU 1 mvn clean package -Denv=%env% -U
if %projectType% EQU 2 mvn clean deploy -U