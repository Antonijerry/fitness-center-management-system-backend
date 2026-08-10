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
