# Set PostgreSQL password environment variable
$env:PGPASSWORD = "0000"

# Path to psql
$psqlPath = "C:\Program Files\PostgreSQL\18\bin\psql.exe"

# Create database
Write-Host "Creating pim_db database..." -ForegroundColor Yellow

# Connect to default postgres database and create pim_db
& $psqlPath -U postgres -d postgres -c "DROP DATABASE IF EXISTS pim_db;"
& $psqlPath -U postgres -d postgres -c "CREATE DATABASE pim_db;"

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Database pim_db created successfully!" -ForegroundColor Green
} else {
    Write-Host "✗ Failed to create database" -ForegroundColor Red
}

# Clear password from environment
$env:PGPASSWORD = ""

Write-Host ""
Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
