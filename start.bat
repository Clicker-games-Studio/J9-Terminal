@echo off
title J9 Terminal Runner

echo Compiling terminal.java...
java-src\bin\javac.exe src\terminal.java

if %errorlevel% neq 0 (
    echo Compile error!
    pause
    exit /b
)

echo Running J9 Terminal...
java-src\bin\java.exe -cp src terminal
pause
