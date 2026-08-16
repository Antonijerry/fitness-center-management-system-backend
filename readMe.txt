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


TRAINER ↔ MEMBER ASSIGNMENT:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
used to assign a trainer to a registered member

TrainerProfile
      │
      │
      ▼
TrainerMemberAssignment
      ▲
      │
      │
MemberProfile

an assignment will contain:
id
trainer
member
assignedAt
endedAt
status
notes
primaryTrainer

this allows:
Member A
   │
   ├── Trainer X → ended
   │
   └── Trainer Y → active
instead of destroying historical information.


src/main/java/com/fitnesscenter/assignment/
├── controller/
│   └── TrainerMemberAssignmentController.java
│
├── dto/
│   ├── CreateAssignmentRequest.java
│   ├── AssignmentResponse.java
│   └── UpdateAssignmentRequest.java
│
├── entity/
│   ├── TrainerMemberAssignment.java
│   └── AssignmentStatus.java
│
├── mapper/
│   └── AssignmentMapper.java
│
├── repository/
│   └── TrainerMemberAssignmentRepository.java
│
└── service/
    ├── TrainerMemberAssignmentService.java
    └── TrainerMemberAssignmentServiceImpl.java


Database Migration:
src/main/resources/db/migration/
└── V10__create_trainer_member_assignments.sql

TEST for all controllers endpoints

##git commit for each feature
from intellij view>tool window > terminal > select git bash and run : git init, git status, git add ., and git commit -m "chore: member-profile added to fitness management backend"
This is important because from this point onward we can make each major feature a separate commit.


##ATTENDANCE / CHECK-IN MANAGEMENT:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
The goal is to make it production-ready enough to support:

Member check-in
Member check-out
Duplicate check-in prevention
Membership validation
Attendance history
Daily attendance
Attendance duration
Check-in method
Attendance status
Administrative reporting

MemberProfile
      │
      │ 1
      ▼
Attendance
      │
      ├── checkInTime
      ├── checkOutTime
      ├── date
      ├── status
      ├── method
      └── notes


A member can have many attendance records:

Member
 │
 ├── 2026-08-10 → CHECKED_OUT
 ├── 2026-08-11 → CHECKED_OUT
 ├── 2026-08-12 → CHECKED_IN
 └── ...


 src/main/java/com/fitnesscenter/attendance/
 ├── controller/
 │   └── AttendanceController.java
 │
 ├── dto/
 │   ├── CheckInRequest.java
 │   ├── AttendanceResponse.java
 │   └── AttendanceSummaryResponse.java
 │
 ├── entity/
 │   ├── Attendance.java
 │   ├── AttendanceMethod.java
 │   └── AttendanceStatus.java
 │
 ├── mapper/
 │   └── AttendanceMapper.java
 │
 ├── repository/
 │   └── AttendanceRepository.java
 │
 └── service/
     ├── AttendanceService.java
     └── AttendanceServiceImpl.java


Migration:

src/main/resources/db/migration/
└── V10__create_attendance.sql

test all apis


##git commit for each feature
from intellij view>tool window > terminal > select git bash and run : git init, git status, git add ., and git commit -m "chore: member-profile added to fitness management backend"
This is important because from this point onward we can make each major feature a separate commit.


##TRAINING SESSIONS / PERSONAL TRAINING APPOINTMENTS:::::::::::::::::::::::::::::::::::::::::::::::
A training session represents a scheduled interaction between a trainer and member.
example:
Member: John Doe
Trainer: Michael Smith

Date:       2026-08-15
Start:      10:00
End:        11:00
Type:       PERSONAL_TRAINING
Status:     SCHEDULED
Location:   Gym Floor

Now we build the module that allows a trainer to schedule and manage actual training sessions with members.
Trainer + Member
       │
       ▼
Training Session
       │
       ├── Schedule
       ├── Confirm
       ├── Start
       ├── Complete
       └── Cancel

The system should prevent:

assigning a non-existent trainer
assigning a non-existent member
using an inactive trainer
scheduling with no trainer/member relationship
overlapping trainer sessions
overlapping member sessions
completing an already cancelled session
starting an already completed session
invalid time ranges


src/main/java/com/fitnesscenter/training/
├── controller/
│   └── TrainingSessionController.java
│
├── dto/
│   ├── CreateTrainingSessionRequest.java
│   ├── UpdateTrainingSessionRequest.java
│   └── TrainingSessionResponse.java
│
├── entity/
│   ├── TrainingSession.java
│   ├── TrainingSessionStatus.java
│   └── TrainingSessionType.java
│
├── mapper/
│   └── TrainingSessionMapper.java
│
├── repository/
│   └── TrainingSessionRepository.java
│
└── service/
    ├── TrainingSessionService.java
    └── TrainingSessionServiceImpl.java
Migration:

src/main/resources/db/migration/
└── V12__create_training_sessions.sql


##WORKOUT PROGRAMS & EXERCISES::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
Now we add the actual workout prescription:
Trainer
   │
   ▼
Workout Program
   │
   ├── Day 1
   │     ├── Exercise
   │     ├── Exercise
   │     └── Exercise
   │
   ├── Day 2
   │     ├── Exercise
   │     └── Exercise
   │
   └── Day 3
         └── Exercise
We will build this as a normalized production-ready design, rather than putting exercises into a JSON column.

src/main/java/com/fitnesscenter/workout/
├── controller/
│   ├── ExerciseController.java
│   └── WorkoutProgramController.java
│
├── dto/
│   ├── CreateExerciseRequest.java
│   ├── ExerciseResponse.java
│   ├── CreateWorkoutProgramRequest.java
│   ├── UpdateWorkoutProgramRequest.java
│   ├── WorkoutProgramResponse.java
│   ├── AddWorkoutExerciseRequest.java
│   └── WorkoutExerciseResponse.java
│
├── entity/
│   ├── Exercise.java
│   ├── ExerciseCategory.java
│   ├── WorkoutProgram.java
│   ├── WorkoutProgramDay.java
│   ├── WorkoutExercise.java
│   ├── WorkoutProgramStatus.java
│   └── WorkoutExerciseType.java
│
├── mapper/
│   ├── ExerciseMapper.java
│   └── WorkoutProgramMapper.java
│
├── repository/
│   ├── ExerciseRepository.java
│   ├── WorkoutProgramRepository.java
│   ├── WorkoutProgramDayRepository.java
│   └── WorkoutExerciseRepository.java
│
└── service/
    ├── ExerciseService.java
    ├── ExerciseServiceImpl.java
    ├── WorkoutProgramService.java
    └── WorkoutProgramServiceImpl.java

Migration:

src/main/resources/db/migration/
└── V12__create_workout_programs.sql


##WORKOUT EXECUTION & PROGRESS TRACKING::::::::::::::::::::::::::::::::::::::::::::::::::::::::
record what a member actually did.
We will now build:

Workout Program
      ↓
Workout Day
      ↓
Prescribed Exercise
      ↓
Workout Session
      ↓
Performed Exercise
      ↓
Performed Sets
      ↓
Progress / Volume / PRs

The important distinction is:

WorkoutExercise
= what the trainer prescribed

WorkoutSession
= the member's actual workout visit

WorkoutExerciseLog
= what the member actually performed

WorkoutSetLog
= individual sets performed

src/main/java/com/fitnesscenter/workout/
│
├── controller/
│
├── dto/
│
├── entity/
│
├── mapper/
│
├── repository/
│
└── service/
V13__create_workout_execution_tables.sql
 test all end points. at this point the system now have a complete chain architecture i.e.
 MEMBER
   │
   ▼
 TRAINER ASSIGNMENT
   │
   ▼
 WORKOUT PROGRAM
   │
   ├── Program Day
   │      │
   │      └── Workout Exercise
   │
   ▼
 ACTIVE PROGRAM
   │
   ▼
 WORKOUT SESSION
   │
   ├── Exercise Log
   │      │
   │      ├── Set 1
   │      ├── Set 2
   │      ├── Set 3
   │      └── Set 4
   │
   ▼
 COMPLETED SESSION
   │
   ▼
 PROGRESS ANALYTICS


 PROGRESS ANALYTICS & PERSONAL RECORDS::::::::::::::::::::::::::::::::::::
 We will calculate actual fitness metrics from the data we have just created:

 Total workouts
 Workout completion rate
 Training volume
 Total reps
 Total weight lifted
 Best weight
 Best reps
 Estimated 1RM
 Personal records (PR)
 Exercise progression
 Weekly/monthly progress
 Member fitness dashboard statistics

 For example:

 BENCH PRESS PROGRESS

 Week 1
 60kg × 8

 Week 2
 60kg × 10

 Week 3
 65kg × 8

 Week 4
 67.5kg × 8

 The backend will be able to determine:

 Personal Best Weight = 67.5 kg
 Best Repetitions    = 10
 Estimated 1RM       = ...
 Total Volume        = ...
 Progress            = ...

 That will turn the workout module from simple CRUD into an actual fitness tracking and analytics system.



##ATTENDANCE MANAGEMENT::::::::::::::::::::::::::::::::::::::::
the attendance will flow like this
                 MEMBER
                   │
                   ▼
             CHECK-IN REQUEST
                   │
                   ▼
       ┌─────────────────────────┐
       │ Validate member         │
       │ Validate membership     │
       │ Check duplicate visit   │
       │ Create attendance       │
       └────────────┬────────────┘
                    │
                    ▼
              CHECKED_IN
                    │
                    │
              member leaves
                    │
                    ▼
             CHECK-OUT REQUEST
                    │
                    ▼
       ┌─────────────────────────┐
       │ Find active attendance │
       │ Set checkout time       │
       │ Calculate duration      │
       └────────────┬────────────┘
                    │
                    ▼
              CHECKED_OUT

We will support these attendance methods:

MANUAL
QR_CODE
RFID
BIOMETRIC
MOBILE_APP


##MEMBERSHIP VALIDATION & ACCESS CONTROL::::::::::::::::::::::::::::::::
fitness center should also determine:

A member can enter when:

Member exists
        AND
Member account is active
        AND
Valid membership exists
        AND
Membership is active
        AND
Membership has not expired
        AND
Membership is not suspended
        AND
Required payment condition is satisfied

Otherwise: Access Denied
We will then connect Membership → Payment → Attendance → Access Control, so the system can automatically prevent an expired or suspended member from entering the fitness center.
MEMBER
   │
   ▼
MEMBERSHIP
   │
   ├── ACTIVE?
   ├── EXPIRED?
   ├── SUSPENDED?
   └── PAYMENT STATUS?
          │
          ▼
    ACCESS DECISION
          │
    ┌─────┴─────┐
    ▼           ▼
  ALLOW        DENY
    │
    ▼
CHECK-IN
    │
    ▼
ATTENDANCE
The goal is that attendance can no longer simply check whether a member exists. It must ask whether that member is actually entitled to access the gym



##PAYMENT & MEMBERSHIP AUTOMATION::::::::::::::::::::::::::::::::::::::::::::::

MEMBERSHIP PLAN
      ↓
MEMBERSHIP
      ↓
PAYMENT
      ↓
PAYMENT VERIFIED
      ↓
MEMBERSHIP ACTIVATED
      ↓
MEMBERSHIP VALID
      ↓
ACCESS ALLOWED
      ↓
ATTENDANCE
                 ┌─────────────┐
                 │   PENDING   │
                 └──────┬──────┘
                        │
                 payment verified
                        │
                        ▼
                 ┌─────────────┐
                 │   ACTIVE    │
                 └──────┬──────┘
                        │
                  end date reached
                        │
                        ▼
                 ┌─────────────┐
                 │   EXPIRED   │
                 └─────────────┘

ACTIVE ────────► SUSPENDED
ACTIVE ────────► CANCELLED
The important rule is:

A successful payment should activate a pending membership only after the membership itself has been validated.
NB: do not activate from the frontend:
Frontend
   ↓
Payment
   ↓
Payment verification
   ↓
Backend
   ↓
Membership activation

##COMPLETE PAYMENT INTEGRATION::::::::::::::::::::::::::::::::::::::::::::::
Membership
    ↓
Payment initialization
    ↓
Paystack
    ↓
Payment reference
    ↓
Payment verification
    ↓
Successful payment
    ↓
Membership activation
    ↓
Access control
    ↓
Attendance
Since your project previously included PAYMENT_SECRET_KEY and PAYMENT_PUBLIC_KEY, I'll build this phase around Paystack. Paystack's current documentation confirms that transaction initialization should happen on your server using the secret key, and the returned authorization URL/access code is then used by the frontend. Paystack also explicitly recommends verifying the transaction before delivering value such as activating a membership
When we finish this phase, the backend will support:

POST /api/v1/payments/initialize

to create a Paystack transaction,

GET /api/v1/payments/verify/{reference}

to verify it,

and:

POST /api/v1/payments/webhook

to receive Paystack webhook events.
the complete business flow will be
                    MEMBER
                       │
                       ▼
                MEMBERSHIP
                       │
                       ▼
              PAYMENT REQUEST
                       │
                       ▼
              PAYMENT SERVICE
                       │
                       ▼
                  PAYSTACK
                       │
              ┌────────┴────────┐
              ▼                 ▼
        Authorization       Reference
             URL                │
              │                 │
              ▼                 ▼
           FRONTEND        DATABASE
              │
              ▼
         CUSTOMER PAYS
              │
              ▼
       PAYSTACK SUCCESS
              │
        ┌─────┴──────┐
        ▼            ▼
    VERIFY API    WEBHOOK
        │            │
        └─────┬──────┘
              ▼
       PAYMENT SUCCESSFUL
              │
              ▼
     MEMBERSHIP ACTIVATED
              │
              ▼
       ACCESS ALLOWED

src/main/java/com/fitnesscenter/payment/
│
├── controller/
│   └── PaymentController.java
│
├── dto/
│   ├── PaymentInitializeRequest.java
│   ├── PaymentInitializeResponse.java
│   └── PaymentVerificationResponse.java
│
├── entity/
│   ├── Payment.java
│   ├── PaymentMethod.java
│   └── PaymentStatus.java
│
├── repository/
│   └── PaymentRepository.java
│
├── service/
│   ├── PaymentService.java
│   └── PaymentServiceImpl.java
│
└── client/
    ├── PaystackClient.java
    ├── PaystackInitializeRequest.java
    ├── PaystackInitializeResponse.java
    └── PaystackVerifyResponse.java
For webhook handling:

payment/
└── webhook/
    ├── PaystackWebhookController.java
    └── PaystackWebhookPayload.java







##git commit for each feature
from intellij view>tool window > terminal > select git bash and run : git init, git status, git add ., and git commit -m "chore: member-profile added to fitness management backend"
This is important because from this point onward we can make each major feature a separate commit.






















