@echo off
setlocal enabledelayedexpansion

:: Check NVD API Key
if "%NVD_API_KEY%"=="" (
    echo [WARNING] NVD_API_KEY is not defined in current environment.
    set /p CONFIGURE="Do you want to run set-nvd-key.bat? (y/n): "
    if /i "!CONFIGURE!"=="y" (
        call set-nvd-key.bat
    )
)

if "%1"=="scan" goto scan
if "%1"=="report" goto report
if "%1"=="build" goto build
if "%1"=="test" goto test
if "%1"=="open-report" goto open_report
if not "%1"=="" goto custom

:menu
echo ==============================================
echo          Maven ^& NVD Scanner Client          
echo ==============================================
echo 1) Run NVD Vulnerability Scan (mvn dependency-check:check)
echo 2) Generate Report (Report only, no build failure)
echo 3) Open Vulnerability HTML Report
echo 4) Build Project (mvn clean package)
echo 5) Run Tests (mvn test)
echo 6) Exit
echo ==============================================
set /p CHOICE="Enter choice [1-6]: "

if "%CHOICE%"=="1" goto scan
if "%CHOICE%"=="2" goto report
if "%CHOICE%"=="3" goto open_report
if "%CHOICE%"=="4" goto build
if "%CHOICE%"=="5" goto test
if "%CHOICE%"=="6" exit /b 0
echo Invalid choice.
exit /b 1

:scan
shift
echo Running OWASP NVD Dependency Scan...
mvn dependency-check:check %1 %2 %3 %4 %5
goto end

:report
shift
echo Generating report without build failure...
mvn dependency-check:check -DfailBuildOnCVSS=11 %1 %2 %3 %4 %5
goto open_report

:open_report
if exist "target\dependency-check-report.html" (
    start target\dependency-check-report.html
) else (
    echo Report target\dependency-check-report.html not found.
)
goto end

:build
shift
mvn clean package %1 %2 %3 %4 %5
goto end

:test
shift
mvn test %1 %2 %3 %4 %5
goto end

:custom
mvn %*
goto end

:end
endlocal

