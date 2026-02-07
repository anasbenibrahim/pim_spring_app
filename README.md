# PIM Backend - Spring Boot Application

A secure Spring Boot backend application for Personal Information Management (PIM) with user authentication and role-based access control.

## Features

- **User Management**: Registration and login for three user roles (Patient, Volontaire, Family Member)
- **JWT Authentication**: Secure token-based authentication with access and refresh tokens
- **Password Reset**: Email-based OTP password reset flow
- **Password Encryption**: BCrypt password hashing for secure password storage
- **File Upload**: Profile image upload support for all users
- **Database**: PostgreSQL for data persistence
- **Caching**: Redis integration for improved performance
- **Security**: Spring Security with JWT filters

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- PostgreSQL 12+
- Redis 6+

## Setup Instructions

### 1. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE pim_db;
```

Update `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pim_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 2. Redis Setup

Ensure Redis is running on `localhost:6379` (default). Update the configuration if needed:

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### 3. JWT Secret Key

**IMPORTANT**: Change the JWT secret key in `application.properties`:

```properties
jwt.secret=your-secret-key-change-this-in-production-use-long-random-string
```

Generate a secure random string (at least 256 bits) for production use.

### 4. Email Configuration

The application supports password reset via email. Configure email settings using one of the following methods:

#### Option 1: Using .env file (Recommended)

1. Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```

2. Edit `.env` and add your email credentials:
```env
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

**Note**: For Gmail, you need to use an [App Password](https://support.google.com/accounts/answer/185833) instead of your regular password.

#### Option 2: Using MAILER_DSN (Symfony Mailer format)

Set the `MAILER_DSN` environment variable:
```bash
export MAILER_DSN="smtp://username:password@smtp.gmail.com:587?encryption=tls&auth_mode=login"
```

#### Option 3: Using individual environment variables

Set the following environment variables:
```bash
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
```

**Important**: The `.env` file is excluded from git by default. Never commit your actual email credentials to version control.

### 5. File Upload Directory

The application will create an `uploads` directory in the project root for storing profile images. Ensure the application has write permissions.

### 6. Build and Run

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

Or run the JAR file:

```bash
java -jar target/spring-project-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## API Endpoints

### Authentication

- `POST /api/auth/login` - User login
- `POST /api/auth/register/patient` - Register as patient
- `POST /api/auth/register/volontaire` - Register as volontaire
- `POST /api/auth/register/family` - Register as family member
- `POST /api/auth/refresh` - Refresh access token
- `GET /api/auth/me` - Get current user (requires authentication)

### Password Reset

- `POST /api/auth/forgot-password` - Request password reset OTP (sends OTP to email)
- `POST /api/auth/verify-otp` - Verify OTP code
- `POST /api/auth/reset-password` - Reset password with verified OTP

### Request/Response Examples

#### Login
```json
POST /api/auth/login
{
  "email": "user@example.com",
  "password": "password123"
}
```

#### Register Patient (multipart/form-data)
```
POST /api/auth/register/patient
- data: JSON string with patient information
- image: (optional) image file
```

#### Forgot Password
```json
POST /api/auth/forgot-password
{
  "email": "user@example.com"
}
```

#### Verify OTP
```json
POST /api/auth/verify-otp
{
  "email": "user@example.com",
  "otpCode": "123456"
}
```

#### Reset Password
```json
POST /api/auth/reset-password
{
  "email": "user@example.com",
  "otpCode": "123456",
  "newPassword": "newpassword123",
  "confirmPassword": "newpassword123"
}
```

## User Roles

1. **Patient**: 
   - Email, password, nom, prenom, age
   - Date of birth, sobriety date, addiction type
   - Auto-generated referral key

2. **Volontaire**:
   - Email, password, nom, prenom, age

3. **Family Member**:
   - Email, password, nom, prenom
   - Referral key (required, links to patient)

## Security Features

- BCrypt password encryption (10 rounds)
- JWT access tokens (24 hours expiration)
- JWT refresh tokens (3 days expiration)
- Spring Security with JWT authentication filter
- CORS configuration for cross-origin requests

## Project Structure

```
src/main/java/com/example/springproject/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
├── model/          # Entity models
├── repository/     # Data access layer
├── security/       # Security configuration
└── service/        # Business logic layer
```

## Development Notes

- The application uses JPA with Hibernate for database operations
- Entity inheritance is used for User, Patient, Volontaire, and FamilyMember
- Lombok is used to reduce boilerplate code
- Validation is performed using Jakarta Validation annotations

## Troubleshooting

1. **Database Connection Error**: Ensure PostgreSQL is running and credentials are correct
2. **Redis Connection Error**: Ensure Redis server is running
3. **File Upload Error**: Check write permissions for the uploads directory
4. **JWT Errors**: Verify the JWT secret key is set correctly
5. **Email Sending Error**: 
   - Verify email configuration in `.env` or environment variables
   - For Gmail, ensure you're using an App Password, not your regular password
   - Check that SMTP port 587 is not blocked by firewall
   - Verify MAIL_USERNAME matches the email account

## Production Considerations

- Use environment variables for sensitive configuration
- Change default JWT secret to a strong random value
- Enable HTTPS
- Configure proper CORS origins
- Set up database connection pooling
- Implement rate limiting
- Add comprehensive logging
- Set up monitoring and alerting
