@echo off
set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
cd /d "%~dp0"
call .\gradlew.bat %* > build_out.log 2>&1
echo BUILD_EXIT_CODE=%ERRORLEVEL% >> build_out.log
