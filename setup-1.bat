@echo off
title J9 Terminal Setup
echo ---- J9 Terminal Setup ----
echo Downloading java.zip...

:: Download java.zip
powershell -Command "Invoke-WebRequest -Uri 'https://github.com/Clicker-games-Studio/J9-Terminal/releases/download/Java-src/java.zip' -OutFile 'java.zip'"

echo Unzipping...
powershell -Command "Expand-Archive -Path 'java.zip' -DestinationPath 'java-src' -Force"

echo Cleaning up...
del java.zip

echo Done! Files extracted to java-src
pause
