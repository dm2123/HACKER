@echo off
set ANDROID_SDK=C:\Users\parmod maurya\AppData\Local\Android\Sdk
set PATH=%ANDROID_SDK%\platform-tools;%ANDROID_SDK%\build-tools\36.1.0;%PATH%
set ANDROID_HOME=%ANDROID_SDK%

echo Building HACKER APK...
cd /d "C:\Users\parmod maurya\OneDrive\Documents\Default Project\HACKER"

echo Cleaning project...
call ./gradlew clean 2>&1

echo Building APK...
call ./gradlew assembleDebug 2>&1

echo.
if exist app\build\outputs\apk\debug\app-debug.apk (
    echo.
    echo ==========================================
    echo APK BUILD SUCCESSFUL!
    echo.
    echo Location: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo To install on phone:
    echo 1. Copy this .apk file to your phone
    echo 2. Enable 'Install from unknown sources' in phone Settings
    echo 3. Tap the .apk to install
    echo ==========================================
) else (
    echo.
    echo Build completed - checking for APK location...
    dir app\build\outputs\apk\debug\ /b 2>&1
)

pause