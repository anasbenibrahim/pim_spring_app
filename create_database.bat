@echo off
set PGPASSWORD=0000
"C:\Program Files\PostgreSQL\18\bin\createdb.exe" -U postgres pim_db
if %ERRORLEVEL% EQU 0 (
    echo Database created successfully!
) else (
    echo Failed to create database or it already exists
)
set PGPASSWORD=
pause
