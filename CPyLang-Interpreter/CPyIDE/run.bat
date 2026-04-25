@echo off
echo ===============================
echo   Building CPy IDE...
echo ===============================

:: create out folder
if not exist out mkdir out

:: delete old sources file if exists
if exist sources.txt del sources.txt

:: create fresh sources.txt
echo Creating sources list...

for /r src %%f in (*.java) do (
    echo %%f >> sources.txt
)

:: check if file created
if not exist sources.txt (
    echo ❌ Failed to create sources.txt
    pause
    exit /b
)

:: compile
javac -d out @sources.txt

if %errorlevel% neq 0 (
    echo.
    echo ❌ Compilation Failed
    pause
    exit /b
)

:: cleanup
del sources.txt

echo.
echo ✅ Build Successful
echo ===============================
echo   Launching CPy IDE...
echo ===============================

java -cp out main.Main

pause