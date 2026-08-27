# Automated Fitness Center Management System — Backend

A production-ready RESTful backend for managing the operations of a modern fitness center. The system provides secure APIs for authentication, users, members, memberships, payments, attendance, trainers, classes, bookings, workouts, notifications, and reporting.

## 🚀 Technology Stack

- **Java 21**
- **Spring Boot 3.5.15**
- **Spring Security**
- **Spring Data JPA / Hibernate**
- **MySQL 8.4**
- **Flyway** — Database migration
- **JWT** — Authentication and authorization
- **OpenAPI / Swagger** — API documentation
- **Docker & Docker Compose**
- **JUnit 5** — Testing
- **Testcontainers** — Integration testing
- **Maven** — Build and dependency management

## 📦 Core Modules

The backend is organized around the following major modules:

- 🔐 Authentication & Authorization
- 👤 User Management
- 🧑‍💼 Member Management
- 🎫 Membership Management
- 💳 Payment Management
- 🕐 Attendance Management
- 🏋️ Trainer Management
- 📅 Class Scheduling
- 📝 Class Booking
- 💪 Workout Management
- 🔔 Notifications
- 📊 Reports

## 🔐 Authentication

The application uses **JWT-based authentication** with role-based authorization.

Supported roles include:

- `ADMIN`
- `MANAGER`
- `RECEPTIONIST`
- `TRAINER`
- `MEMBER`

### Authentication Endpoints

```text
POST /api/v1/auth/register
POST /api/v1/auth/login
POST /api/v1/auth/refresh
POST /api/v1/auth/logout

Protected endpoints require a valid JWT access token.

Authorization: Bearer <access-token>
🌐 API

The API uses versioned REST endpoints.

Base URL
http://localhost:8080/api/v1

Examples:

/api/v1/users
/api/v1/members
/api/v1/memberships
/api/v1/payments
/api/v1/attendance
📚 API Documentation

When the application is running, OpenAPI/Swagger documentation is available at:

http://localhost:8080/swagger-ui.html

The OpenAPI specification can also be accessed through:

http://localhost:8080/v3/api-docs
⚙️ Configuration

The application uses environment variables for sensitive configuration such as database credentials, JWT secrets, email credentials, and payment configuration.

Create a .env file locally and provide the required values.

Example:

MYSQL_DATABASE=fitness_center_db
MYSQL_USER=fitness_user
MYSQL_PASSWORD=your_database_password
MYSQL_ROOT_PASSWORD=your_root_password

DB_USERNAME=fitness_user
DB_PASSWORD=your_database_password

MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email
MAIL_PASSWORD=your_app_password

ADMIN_EMAIL=admin@fitnesscenter.com

Important: Never commit .env files, passwords, JWT secrets, API keys, or other credentials to GitHub.

Use .env.example to document required environment variables without exposing real credentials.

🗄️ Database

The application uses:

MySQL 8.4
Spring Data JPA
Hibernate
Flyway

Flyway is used to manage database schema migrations and maintain database version history.

Database name:

fitness_center_db
💻 Running Locally
Prerequisites

Make sure the following are installed:

Java 21
Maven
MySQL 8.4
Git
Docker Desktop (optional but recommended)
1. Clone the repository
git clone https://github.com/YOUR_USERNAME/fitness-management-system.git

Navigate into the project:

cd fitness-management-system
2. Configure environment variables

Create a .env file and configure your database and application credentials.

3. Build the application
mvn clean verify
4. Run the application
mvn spring-boot:run

The backend will start on:

http://localhost:8080
🐳 Running with Docker

The backend can also be run using Docker Compose.

Start the services:

docker compose up -d --build

Check running containers:

docker ps

View backend logs:

docker logs fitness-backend

View MySQL logs:

docker logs fitness-mysql

Stop the services:

docker compose down

To stop the services and remove the database volume:

docker compose down -v

Use docker compose down -v carefully because removing the volume deletes the persisted MySQL database data.

🏥 Health Check

The application exposes an Actuator health endpoint:

GET /actuator/health

Example:

{
  "status": "UP"
}

The health endpoint can be used to verify whether the application and its configured dependencies are available.

🧪 Testing

Run the complete test suite with:

mvn clean verify

Run tests only:

mvn test

The project uses JUnit and Testcontainers for automated and integration testing.

🏗️ Project Structure

The backend follows a modular Spring Boot architecture.

src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── fitnesscenter/
│   │           ├── auth/
│   │           ├── user/
│   │           ├── member/
│   │           ├── membership/
│   │           ├── payment/
│   │           ├── attendance/
│   │           ├── trainer/
│   │           ├── classmanagement/
│   │           ├── booking/
│   │           ├── workout/
│   │           ├── notification/
│   │           ├── report/
│   │           └── config/
│   │
│   └── resources/
│       ├── db/
│       │   └── migration/
│       ├── application.yaml
│       └── application-prod.yaml
│
└── test/
    └── java/
🔒 Security

Security features include:

JWT authentication
Role-based access control
Password hashing
Protected REST endpoints
Authentication filters
Refresh token support
CORS configuration
Input validation
Secure environment-based configuration

Sensitive credentials are stored through environment variables rather than source code.

🚀 Production

For production deployment, the application supports a dedicated Spring profile:

prod

Activate it with:

SPRING_PROFILES_ACTIVE=prod

Production configuration should use environment variables for:

Database credentials
JWT secrets
Email credentials
Payment credentials
Other third-party API keys

Never store production secrets directly in the repository.

📌 Backend Status

The backend provides the core REST API required to operate a complete fitness center management platform.

Current capabilities
Authentication and authorization
User and role management
Member management
Membership management
Payments
Attendance
Trainers
Classes and bookings
Workout management
Notifications
Reporting
Database migrations
API documentation
Docker-based deployment
👨‍💻 Development

This project was developed as a Fitness Center Management System using modern backend engineering practices with Java and Spring Boot.

The backend is designed to provide a scalable REST API that can be consumed by web or mobile frontend applications.

📄 License

This project is intended for educational and academic purposes.


### One important correction to your original README

You had:

```markdown
##run maven validation
```bash
mvn clean verify

Then run:

```bash
mvn clean spring-boot:run

That Markdown is broken because the first code block isn't closed.

It should be:

## Run Maven Validation

```bash
mvn clean verify

Then run the application:

mvn spring-boot:run

Also, **don't put your real `.env` values in the README**. Your GitHub repository should contain something like:

```text
.env.example

but not:

.env

Your .gitignore should include:

# Environment variables
.env
.env.*
!.env.example

# Maven
target/
!.mvn/wrapper/maven-wrapper.jar

# IDE
.idea/
.vscode/
*.iml

# OS
.DS_Store
Thumbs.db

# Logs
*.log

# Java
*.class

# Spring Boot
spring.log

And because you're using the Maven Wrapper, you do not actually need Maven installed globally. Your earlier mvn is not recognized problem is exactly why using:

.\mvnw.cmd clean verify

is preferable on your Windows machine.

For your repository, I'd use .\mvnw.cmd clean verify and .\mvnw.cmd spring-boot:run rather than relying on mvn.
