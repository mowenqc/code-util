@echo off
set projectDir=%1
set repository=%2
set projectType=%3
set vcs=%4
set module=%5
set gitBranch=%6
set env=%7
set clientModule=%8

cd /d %projectDir%

if %vcs% EQU 1   (
svn checkout %repository%/%module%@HEAD --username  --password ct2017
svn checkout %repository%/%clientModule%@HEAD --username  --password ct2017
)
if %vcs% EQU 2   (
git init
git config core.sparseCheckout true
echo /%module%/* >> .git/info/sparse-checkout
echo /%clientModule%/* >> .git/info/sparse-checkout
git remote add origin %repository%
git checkout -b %gitBranch%
git pull origin %gitBranch%
)