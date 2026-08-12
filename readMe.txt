COMPLETE TECH STACK FOR THE PROJECT::::::::::::::::::::::::::::::::::::::::::::::::

| Technology          | Version/Choice                     |
| ------------------- | ---------------------------------- |
| Java                | **21**                             |
| Spring Boot         | **3.5.15**                         |
| Maven               | 3.9+ recommended                   |
| PostgreSQL          | 16/17                              |
| Spring Web          | Boot managed                       |
| Spring Data JPA     | Boot managed                       |
| Spring Security     | Boot managed                       |
| Validation          | Boot managed                       |
| OpenAPI             | **springdoc 2.8.17**               |
| Database migrations | Flyway                             |
| Authentication      | JWT                                |
| Password hashing    | BCrypt                             |
| Testing             | JUnit 5 + Mockito + Testcontainers |
| Monitoring          | Actuator                           |
| Containerization    | Docker                             |
| API version         | `/api/v1`                          |

initial dependencies to add at the beginning of the project:::::::::::::
Spring Web
Spring Data JPA
MySQL Driver
Spring Security
Validation
Spring Boot Actuator
Lombok
The following were added separately after project creation:
Springdoc OpenAPI / Swagger
Spring Security Test

## Documentation
Swagger UI:http://localhost:8080/swagger-ui/index.html#/health-controller/health
OpenAPI: http://localhost:8080/v3/api-docs
Health: http://localhost:8080/actuator/health

##git commit for each feature
from intellij view>tool window > terminal > select git bash and run : git init, git status, git add ., and git commit -m "chore: initialize fitness management backend"
This is important because from this point onward we can make each major feature a separate commit.

DATABASE FOUNDATION WITH FLYWAY AND MYSQL
##Database + Flyway.::::::::::::::::::::::::::::::::::::::::::::: (proper database foundation)
                    SPRING BOOT
                         │
             ┌───────────┴───────────┐
             │                       │
          Hibernate                Flyway
             │                       │
             │                 migrations
             │                       │
             └───────────┬───────────┘
                         │
                         ▼
                       MySQL
                         │
          ┌──────────────┼──────────────┐
          │              │              │
          ▼              ▼              ▼
       users           roles        user_roles
✅ Flyway manages schema
✅ Hibernate validates schema
add flyway dependencies, create the .sql files at resources/db/migration/ and configure each file with sql codes to create tables in the db.
NB: if you want to edit any of the files in /migration Is either you run this sql in your database

DROP DATABASE IF EXISTS fitness_center_db;
CREATE DATABASE fitness_center_db;
USE fitness_center_db;

confirm at database if all tables created with flyway at success(1) using :::
SELECT installed_rank, version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;

or you create a new V(version)__(name).sql file and your your new sql code to alter the table of any of the previous sql files created

##create a reusable java base entity:: common/entity/BaseEntity class: this class can be extend by other entity classes
Spring Data JPA provides @EnableJpaAuditing specifically to enable this auditing mechanism. so configure /common/config/JpaConfig.java now @created and @LastModifiedDate can be populated automatically.

##create user/entity/User.java, user/entity/Role.java  all for USER extending the BaseEntity class
  create user/repository/UserRepository and RoleRepository interfaces


USER MANAGEMENT:::::::::::::::::::::::::::::::
HTTP Request
     │     DTOs::  records:   requests: user/dto/CreateUserRequest.java, user/dto/UpdateUserRequest.java, user/dto/UpdateUserStatusRequest.java and user/dto/AssignRoleRequest.java
     ▼                        response: user/dto/UserResponse.java
UserController                Add Mapper: user/mapper/UserMapper.java
     │
     ▼
UserService
     │
     ▼
UserRepository
     │
     ▼
   MySQL

AUTHENTICATION + JWT SECURITY:::::::::::::::::::::::::::::::::::
This phase will replace the temporary permitAll() development security with real Spring Security authentication.
                    CLIENT
                       │
             ┌─────────┴─────────┐
             │                   │
          REGISTER              LOGIN
             │                   │
             ▼                   ▼
        AuthService         AuthService
             │                   │
             ▼                   ▼
       BCrypt hash          Verify password
             │                   │
             └─────────┬─────────┘
                       ▼
                  JWT Service
                       │
             ┌─────────┴─────────┐
             ▼                   ▼
        Access Token        Refresh Token
             │                   │
             ▼                   ▼
       API requests        Token renewal
             │
             ▼
    JwtAuthenticationFilter
             │
             ▼
      Spring Security
             │
             ▼
       Controllers

A. ADD JWT dependencies according to the springboot version and create auth folder will have controller, dto, security and service packages
b. in application-dev.yml configure jwt properties, create JwtProperties class at auth/security/ and add @ConfigurationProperties(prefix = "app.jwt") and enable the configuration class at the main project class
   inside common/config/SecurityBeansConfig create a password encoder bean, import it into UserServiceImpl.java and replace the set password with password encoder
c. create auth/security/CustomUserDetailsService.java This converts our database user into Spring Security's UserDetails.
d. create auth/security/AuthenticationConfig.java and auth/security/JwtAuthenticationFilter.java
e. create auth/service/AuthService.java and auth/service/AuthServiceImpl.java
f. create auth/service/AuthController.java
g. create common/config/securityConfig.java to handle all url permission/access
   which shows all auth ports is permitted but users port required jwt
   /api/v1/auth/**        → public

   /api/v1/health        → public

   Swagger                → public

   /api/v1/users/**      → JWT required

   everything else       → JWT required
h. create AuthController and test all api

##Production-Grade Refresh Tokens, Logout, Token Rotation, and RBAC.::::::::::::::::::::
The important change is that refresh tokens will now be stored and revocable in the database, rather than being completely stateless.
LOGIN
  │
  ├── Access Token ───────────────► Protected APIs
  │
  └── Refresh Token
          │
          ▼
    refresh_tokens table
          │
          ├── expires_at
          ├── revoked_at
          └── user_id

REFRESH
  │
  ▼
Validate token
  │
  ▼
Revoke old token
  │
  ▼
Create new refresh token
  │
  ▼
Create new access token

LOGOUT
  │
  ▼
Revoke refresh token

test all api and see try running this sql in your db to test for revoke refresh token
SELECT id,
    revoked_at
FROM refresh_tokens
ORDER BY id;

copying the revoked token, and test using postman will show unauthorized meaning the old token is now useless

We will also establish the role hierarchy:
ADMIN
  │
  └── MANAGER
       │
       ├── TRAINER
       ├── RECEPTIONIST
       └── MEMBER
for role authorization, we need @EnableMethodSecurity in other to use this @PreAuthorize(...) e.g @PreAuthorize("hasRole('ADMIN')") means for admin, @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')") means admin or manager
protect the userController with @PreAuthorize()

##MEMBERSHIP MANAGEMENT::::::::::::::::::::::::::::::::::::::::::::::::::::
ADMIN / MANAGER
       │
       ▼
Membership Plans
       │
       ├── BASIC
       ├── STANDARD
       ├── PREMIUM
       └── CUSTOM
              │
              ▼
           Member
              │
              ▼
         Membership
              │
      ┌───────┼────────┐
      ▼       ▼        ▼
   ACTIVE   EXPIRED  CANCELLED
              │
              ▼
        Access Control
first create src/main/java/com/fitnesscenter/membership/ and it will have the following structure
membership/
├── controller/
│   ├── MembershipController.java
│   └── MembershipPlanController.java
│
├── dto/
│   ├── CreateMembershipPlanRequest.java
│   ├── UpdateMembershipPlanRequest.java
│   ├── MembershipPlanResponse.java
│   ├── CreateMembershipRequest.java
│   ├── UpdateMembershipRequest.java
│   └── MembershipResponse.java
│
├── entity/
│   ├── MembershipPlan.java
│   ├── Membership.java
│   ├── MembershipStatus.java
│   └── MembershipType.java
│
├── mapper/
│   └── MembershipMapper.java
│
├── repository/
│   ├── MembershipPlanRepository.java
│   └── MembershipRepository.java
│
└── service/
    ├── MembershipPlanService.java
    ├── MembershipPlanServiceImpl.java
    ├── MembershipService.java
    └── MembershipServiceImpl.java

NB: at the end of the service and controller, we add a database migration like V5__create_membership_tables.sql, V6__seed_membership_plans.sql(We need initial membership-plans for development/testing.) because we dont want the spring.jpa.hibernate.ddl-auto to update it for us:
test::::
first authenticate as an admin: copy access token from login as a user, assign the user a role as admin, you can use the admin user access token to authenticate any request on membership controller e.g
 GET /api/v1/membership-plans and copy the access token in order to get all the membership plans we populated before
for membership testing::

| Method  | Endpoint                            | Purpose                | Roles                        |
| ------- | ----------------------------------- | ---------------------- | ---------------------------- |
| `POST`  | `/api/v1/memberships`               | Create membership      | ADMIN, MANAGER, RECEPTIONIST |
| `GET`   | `/api/v1/memberships/{id}`          | Get membership         | ADMIN, MANAGER, RECEPTIONIST |
| `GET`   | `/api/v1/memberships`               | Get all                | ADMIN, MANAGER, RECEPTIONIST |
| `GET`   | `/api/v1/memberships/user/{userId}` | Get user's memberships | ADMIN, MANAGER, RECEPTIONIST |
| `PATCH` | `/api/v1/memberships/{id}/activate` | Activate               | ADMIN, MANAGER, RECEPTIONIST |
| `PATCH` | `/api/v1/memberships/{id}/suspend`  | Suspend                | ADMIN, MANAGER               |
| `PATCH` | `/api/v1/memberships/{id}/cancel`   | Cancel                 | ADMIN, MANAGER               |

##git commit for each feature
from intellij view>tool window > terminal > select git bash and run : git init, git status, git add ., and git commit -m "chore: initialize fitness management backend"
This is important because from this point onward we can make each major feature a separate commit.


MEMBER MANAGEMENT & MEMBER PROFILE::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
User A User is a system account.
 │
 │ authentication / authorization
 ▼
MemberProfile      A MemberProfile represents the person's relationship with the fitness center.
 │
 ├── membership history
 ├── emergency contact
 ├── fitness goals
 ├── trainer assignment
 ├── attendance
 └── progress


src/main/java/com/fitnesscenter/member/
├── controller/
│   └── MemberController.java
│
├── dto/
│   ├── CreateMemberProfileRequest.java
│   ├── UpdateMemberProfileRequest.java
│   ├── MemberProfileResponse.java
│   ├── EmergencyContactRequest.java
│   └── EmergencyContactResponse.java
│
├── entity/
│   ├── MemberProfile.java
│   └── MemberStatus.java
│
├── mapper/
│   └── MemberMapper.java
│
├── repository/
│   └── MemberProfileRepository.java
│
└── service/
    ├── MemberService.java
    └── MemberServiceImpl.java

and add: src/main/resources/db/migration/
          └── V8__create_member_profiles.sql

test each controller endpoint using postman/swagger

##git commit for each feature
from intellij view>tool window > terminal > select git bash and run : git init, git status, git add ., and git commit -m "chore: member-profile added to fitness management backend"
This is important because from this point onward we can make each major feature a separate commit.


TRAINER & STAFF MANAGEMENT:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

                         User
                          │
              ┌───────────┴───────────┐
              │                       │
              ▼                       ▼
       MemberProfile            TrainerProfile  A trainer profile should belong to a user who has the: TRAINER role
              │                       │
              ▼                       ▼
         Membership              Assignments
                                      │
                                      ▼
                                  Members

Database:
src/main/resources/db/migration/
└── V9__create_trainer_profiles.sql

TEST: first login with admin user, assign a user the role of a trainer; then create the trainer profile using post and the right url
test for other endpoints

##git commit for each feature
from intellij view>tool window > terminal > select git bash and run : git init, git status, git add ., and git commit -m "chore: member-profile added to fitness management backend"
This is important because from this point onward we can make each major feature a separate commit.











##git commit for each feature
from intellij view>tool window > terminal > select git bash and run : git init, git status, git add ., and git commit -m "chore: member-profile added to fitness management backend"
This is important because from this point onward we can make each major feature a separate commit.

























