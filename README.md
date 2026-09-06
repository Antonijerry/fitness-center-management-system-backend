# 🏋️ Fitness Center Management System — Backend

A **production-oriented fitness center management backend** built with Java 21 and Spring Boot, providing a secure, modular REST API for managing members, memberships, payments, attendance, trainers, classes, bookings, workouts, notifications, and reporting.

The system was designed to solve the operational challenges of managing a modern fitness center through a centralized backend platform, with a strong focus on **security, modular architecture, data integrity, API design, automated testing, database migrations, and containerized deployment**.

---

## 🎯 Problem Solved

Fitness centers often rely on disconnected processes for managing members, subscriptions, payments, attendance, trainers, and classes, making it difficult to maintain accurate and consistent operational data.

This system provides a centralized backend that **automates core fitness-center operations, enforces secure access, maintains reliable transactional data, and exposes a scalable REST API for web and mobile clients.**

---

# 💡 Technical Problems Solved

This project was built around real backend engineering problems rather than simply implementing CRUD operations.

### 1. Managing Complex Fitness Operations

**Problem:**
A fitness center has multiple interconnected business processes—memberships, payments, attendance, classes, bookings, trainers, and workouts.

**Solution:**
The backend is organized into independent business modules with clearly defined responsibilities.

```text
Authentication
      │
      ├── Users
      ├── Members
      ├── Memberships
      ├── Payments
      ├── Attendance
      ├── Trainers
      ├── Classes
      ├── Bookings
      ├── Workouts
      ├── Notifications
      └── Reports
```

**Engineering benefit:**
The modular structure makes the system easier to maintain, test, extend, and eventually evolve into independently deployable services if required.

---

### 2. Securing Sensitive APIs

**Problem:**
Fitness-center systems contain sensitive user information and financial data that should not be accessible to unauthorized users.

**Solution:**
Spring Security and JWT-based authentication protect API endpoints and enforce role-based access control.

Supported roles:

```text
ADMIN
MANAGER
RECEPTIONIST
TRAINER
MEMBER
```

**Engineering benefit:**
Different users receive access appropriate to their responsibilities instead of exposing every operation to every authenticated user.

---

### 3. Managing Authentication Tokens

**Problem:**
Long-lived access tokens increase security risk, while extremely short-lived tokens can negatively affect user experience.

**Solution:**
The application implements an access-token and refresh-token strategy.

```text
Access Token
    │
    ├── Short-lived
    └── API authentication

Refresh Token
    │
    ├── Longer-lived
    └── Access-token renewal
```

This provides a balance between security and usability.

---

### 4. Maintaining Database Consistency

**Problem:**
Manually modifying production database schemas makes deployments unreliable and difficult to reproduce.

**Solution:**
Flyway is used for version-controlled database migrations.

```text
V1__initial_schema.sql
V2__add_memberships.sql
V3__add_payments.sql
V4__add_attendance.sql
...
```

**Engineering benefit:**
Database changes become reproducible, traceable, and consistent across development, testing, and deployment environments.

---

### 5. Handling Financial Transactions

**Problem:**
Payment operations must maintain accurate financial records and avoid inconsistent database state.

**Solution:**
Payment-related business operations are handled through transactional service boundaries and persistent payment records.

The architecture separates payment processing from the rest of the business logic, allowing external payment providers to be integrated without tightly coupling provider-specific logic to core domain operations.

---

### 6. Preventing Invalid Business Data

**Problem:**
REST APIs can receive malformed or invalid data from clients.

**Solution:**

* Request validation
* DTO-based API contracts
* Service-layer business validation
* Consistent exception handling
* Structured API responses

This prevents invalid data from unnecessarily reaching the persistence layer.

---

### 7. Maintaining Consistent Error Responses

**Problem:**
Without centralized exception handling, every controller can return different error formats.

**Solution:**
A global exception-handling strategy provides consistent API error responses.

Conceptually:

```text
Controller
     │
     ▼
Service
     │
     ├── Business Exception
     ├── Validation Exception
     └── Resource Not Found
             │
             ▼
    Global Exception Handler
             │
             ▼
       Consistent JSON
```

This makes the API easier for frontend developers and external clients to consume.

---

### 8. Testing Database-Dependent Functionality

**Problem:**
Mocking a database completely may hide integration problems involving JPA, Hibernate, SQL, constraints, and migrations.

**Solution:**
The project uses **Testcontainers** to execute integration tests against a real containerized database environment.

This provides stronger confidence that application code and database behavior work together correctly.

---

# 🏗️ Architecture

The backend follows a **modular layered architecture**.

```text
                    CLIENT
                      │
                      ▼
               REST Controllers
                      │
                      ▼
                 DTO / Validation
                      │
                      ▼
                Service Layer
                      │
          ┌───────────┼───────────┐
          │           │           │
          ▼           ▼           ▼
      Repository   Security    Integration
          │
          ▼
      Hibernate/JPA
          │
          ▼
        MySQL
```

Business functionality is separated into modules:

```text
src/main/java/com/fitnesscenter/

├── auth/
├── user/
├── member/
├── membership/
├── payment/
├── attendance/
├── trainer/
├── classmanagement/
├── booking/
├── workout/
├── notification/
├── report/
└── config/
```

This approach reduces coupling between unrelated business capabilities while keeping the application manageable as a modular monolith.

---

# 🔐 Authentication & Authorization

The application uses **Spring Security + JWT** for API authentication and role-based authorization.

### Supported Roles

| Role         | Typical Responsibility           |
| ------------ | -------------------------------- |
| ADMIN        | Full system administration       |
| MANAGER      | Fitness-center operations        |
| RECEPTIONIST | Member and attendance operations |
| TRAINER      | Training and workout management  |
| MEMBER       | Member-facing functionality      |

### Authentication Flow

```text
Client
  │
  │ Credentials
  ▼
Authentication API
  │
  ▼
Spring Security
  │
  ▼
User Authentication
  │
  ▼
JWT Generation
  │
  ├── Access Token
  └── Refresh Token
  │
  ▼
Authenticated API Requests
```

### Authentication Endpoints

```http
POST /api/v1/auth/register
POST /api/v1/auth/login
POST /api/v1/auth/refresh
POST /api/v1/auth/logout
```

Protected requests use:

```http
Authorization: Bearer <access-token>
```

---

# 📦 Core Modules

## 👤 User Management

Provides functionality for managing system users and their assigned roles.

## 🧑‍💼 Member Management

Manages fitness-center members and their operational information.

## 🎫 Membership Management

Handles membership plans, subscriptions, status, and membership lifecycle.

## 💳 Payment Management

Maintains payment records and payment-related operations.

## 🕐 Attendance Management

Records member attendance and provides attendance data for operational reporting.

## 🏋️ Trainer Management

Manages trainers and their associated fitness-center activities.

## 📅 Class Scheduling

Provides functionality for creating and managing fitness classes and schedules.

## 📝 Class Booking

Handles member bookings and class participation.

## 💪 Workout Management

Supports workout-related records and training activities.

## 🔔 Notifications

Provides notification functionality for communicating important system events.

## 📊 Reporting

Aggregates operational information for fitness-center reporting and decision-making.

---

# 🌐 REST API

All APIs are versioned under:

```text
/api/v1
```

### Example endpoints

```text
/api/v1/users
/api/v1/members
/api/v1/memberships
/api/v1/payments
/api/v1/attendance
/api/v1/trainers
/api/v1/classes
/api/v1/bookings
/api/v1/workouts
/api/v1/notifications
/api/v1/reports
```

Versioned APIs make future API evolution easier without immediately breaking existing clients.

---

# 📚 API Documentation

The API is documented using **OpenAPI/Swagger**.

When running locally:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI specification:

```text
http://localhost:8080/v3/api-docs
```

Swagger provides an interactive interface for exploring and testing available endpoints.

---

# 🛠️ Technology Stack

### Backend

* **Java 21**
* **Spring Boot 3.5.15**
* Spring Security
* Spring Data JPA
* Hibernate
* Maven

### Security

* JWT authentication
* Role-based authorization
* Password hashing
* Authentication filters
* Refresh tokens
* CORS configuration

### Database

* MySQL 8.4
* Hibernate/JPA
* Flyway migrations

### Testing

* JUnit 5
* Mockito
* Spring Boot Test
* Testcontainers

### API

* REST
* OpenAPI
* Swagger UI

### DevOps

* Docker
* Docker Compose
* Environment-based configuration

---

# 🗄️ Database Architecture

The system uses **MySQL 8.4** as its relational database.

Major domain areas include:

```text
Users
  │
  ├── Roles
  │
  └── Authentication

Members
  │
  ├── Memberships
  ├── Payments
  ├── Attendance
  ├── Bookings
  └── Workouts

Trainers
  │
  └── Classes
          │
          └── Bookings
```

JPA/Hibernate handles object-relational mapping while Flyway manages schema evolution.

Database:

```text
fitness_center_db
```

---

# 🔄 Transaction Management

Business operations that modify multiple related records are designed around transactional service boundaries.

For example:

```text
Membership Operation
       │
       ├── Update Membership
       ├── Update Related State
       └── Persist Changes
              │
              ▼
        Transaction Commit
```

If a failure occurs during the transaction, changes can be rolled back to prevent partial database updates.

This helps maintain **data integrity across related operations**.

---

# 🐳 Docker Architecture

The application supports containerized deployment using Docker Compose.

```text
             Docker Compose
                   │
        ┌──────────┴──────────┐
        │                     │
        ▼                     ▼
 Fitness Backend           MySQL
 Spring Boot               Database
        │                     │
        └──────────┬──────────┘
                   │
                   ▼
              Persistent
                Volume
```

Start the application:

```bash
docker compose up -d --build
```

View containers:

```bash
docker ps
```

View backend logs:

```bash
docker logs fitness-backend
```

View database logs:

```bash
docker logs fitness-mysql
```

Stop services:

```bash
docker compose down
```

To remove the database volume:

```bash
docker compose down -v
```

> Use `docker compose down -v` carefully because removing the volume deletes persisted MySQL data.

---

# ⚙️ Configuration & Secrets

Sensitive configuration is supplied through environment variables rather than committed source code.

Typical configuration includes:

```text
DB_URL
DB_USERNAME
DB_PASSWORD

JWT_SECRET

MAIL_HOST
MAIL_PORT
MAIL_USERNAME
MAIL_PASSWORD

PAYMENT_API_KEY
PAYMENT_SECRET

PAYMENT_CALLBACK_URL
```

A safe repository should contain:

```text
.env.example
```

but **never real credentials**.

Example:

```env
DB_USERNAME=fitness_user
DB_PASSWORD=your_database_password
JWT_SECRET=your_secret
```

Never commit:

```text
.env
```

or real passwords, API keys, JWT secrets, or third-party credentials.

---

# 🏥 Health Monitoring

Spring Boot Actuator provides an application health endpoint:

```http
GET /actuator/health
```

Example:

```json
{
  "status": "UP"
}
```

This provides a basic mechanism for checking application availability and can be integrated into container orchestration and monitoring systems.

---

# 🧪 Testing Strategy

Testing is implemented at multiple levels.

### Unit Testing

Used for isolated business logic such as:

* Service methods
* Validation
* Authentication logic
* Token operations
* Business rules

### Integration Testing

Used to validate interactions between:

```text
Controller
   ↓
Service
   ↓
Repository
   ↓
Database
```

### Testcontainers

Testcontainers provides disposable containerized infrastructure for integration testing.

This reduces the gap between mocked tests and real database behavior.

Run the complete test suite:

```bash
mvn clean verify
```

Run tests only:

```bash
mvn test
```

### Maven Wrapper

The project includes Maven Wrapper support, so Maven does not need to be globally installed.

Windows:

```powershell
.\mvnw.cmd clean verify
```

Run the application:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

---

# 📁 Project Structure

```text
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
```

---

# 💻 Running Locally

## Prerequisites

* Java 21
* Git
* Docker Desktop
* MySQL 8.4 if running without Docker

### Clone

```bash
git clone https://github.com/YOUR_USERNAME/fitness-management-system.git
cd fitness-management-system
```

### Configure environment

Create:

```text
.env
```

using:

```text
.env.example
```

as a reference.

### Build

Windows:

```powershell
.\mvnw.cmd clean verify
```

Linux/macOS:

```bash
./mvnw clean verify
```

### Run

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

---

# 🐳 Run with Docker Compose

```bash
docker compose up -d --build
```

The backend and MySQL services will start together.

Check:

```bash
docker ps
```

---

# 🚀 Production Profile

The application supports a dedicated Spring production profile:

```text
prod
```

Activate it using:

```text
SPRING_PROFILES_ACTIVE=prod
```

Production deployments should provide secrets through the deployment environment rather than source control.

Production configuration should include:

* Database credentials
* JWT secrets
* Email credentials
* Payment credentials
* Third-party API keys
* Environment-specific URLs

---

# 📈 Future Improvements

Planned areas for further production hardening include:

* CI/CD pipeline
* Centralized application logging
* Distributed tracing
* Metrics and observability
* API rate limiting
* Redis caching
* Advanced audit logging
* Automated database backups
* Security monitoring
* Improved payment reconciliation
* Cloud deployment
* Kubernetes deployment
* Asynchronous event processing

---

# 🎓 Engineering Skills Demonstrated

This project demonstrates practical experience with:

### Backend Engineering

* Java 21
* Spring Boot
* REST API design
* Layered architecture
* Modular monolith architecture
* DTO-based API design
* Business-service design
* Transaction management

### Security

* Spring Security
* JWT
* Role-based access control
* Password hashing
* Authentication filters
* Refresh tokens
* CORS
* Secure configuration

### Database Engineering

* MySQL
* JPA/Hibernate
* Relational database modeling
* Database migrations
* Flyway
* Transactional data operations

### Testing

* JUnit 5
* Mockito
* Integration testing
* Testcontainers
* API testing

### DevOps

* Docker
* Docker Compose
* Environment configuration
* Production profiles
* Maven

### API Engineering

* RESTful API design
* API versioning
* OpenAPI
* Swagger
* Validation
* Global exception handling

---

# 🏆 Project Highlights

| Area              | Implementation            |
| ----------------- | ------------------------- |
| Architecture      | Modular Monolith          |
| Backend           | Java 21 + Spring Boot     |
| Security          | Spring Security + JWT     |
| Authorization     | Role-Based Access Control |
| Database          | MySQL 8.4                 |
| ORM               | JPA / Hibernate           |
| Migrations        | Flyway                    |
| API Documentation | OpenAPI / Swagger         |
| Testing           | JUnit + Testcontainers    |
| Deployment        | Docker + Docker Compose   |
| Configuration     | Environment Variables     |
| Monitoring        | Spring Boot Actuator      |

---

# 👨‍💻 Developer

Built as a backend engineering project demonstrating the design and implementation of a **secure, modular, database-driven business management platform** using modern Java and Spring Boot practices.

The project focuses on solving real operational and engineering challenges rather than implementing simple CRUD functionality.

---

## 📄 License

This project is available for educational, portfolio, and demonstration purposes.
