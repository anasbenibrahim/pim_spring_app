#!/bin/bash

echo "🚀 Starting PIM Backend..."
echo ""

# Check if PostgreSQL is running
if ! pg_isready -U postgres > /dev/null 2>&1; then
    echo "⚠️  PostgreSQL is not running. Please start it first."
    echo "   On macOS: brew services start postgresql@14"
    exit 1
fi

# Check if Redis is running
if ! redis-cli ping > /dev/null 2>&1; then
    echo "⚠️  Redis is not running. Please start it first."
    echo "   On macOS: brew services start redis"
    exit 1
fi

echo "✅ PostgreSQL is running"
echo "✅ Redis is running"
echo ""
echo "📦 Building and starting Spring Boot application..."
echo ""

# Navigate to Spring directory
cd "$(dirname "$0")"

# Run the application
mvn spring-boot:run
