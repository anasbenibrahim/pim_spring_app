@echo off
echo Creating pim_db database...
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -c "DROP DATABASE IF EXISTS pim_db;"
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -c "CREATE DATABASE pim_db;"
echo Database created successfully!
pause
