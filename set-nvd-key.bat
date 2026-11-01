@echo off
setlocal

echo --- NVD API Key Setup (Windows Batch) ---

set /p NVDKEY="Enter your NVD API Key: "

if "%NVDKEY%"=="" (
    echo [ERROR] NVD API Key cannot be empty.
    exit /b 1
)

:: Set for the current session
set "NVD_API_KEY=%NVDKEY%"

:: Persist permanently to the User Environment
setx NVD_API_KEY "%NVDKEY%" >nul 2>&1

if %ERRORLEVEL% equ 0 (
    echo [SUCCESS] NVD_API_KEY saved to User Environment Variables.
    echo Maven can now read this via ${env.NVD_API_KEY}.
    echo (Note: Newly opened terminal windows will automatically load this value.)
) else (
    echo [WARNING] Could not persist environment variable via setx.
    echo Set manually using: set NVD_API_KEY=%NVDKEY%
)

endlocal

