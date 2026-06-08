
## Final Architecture

```text
ecommerce-platform

├── api-gateway
├── user-service
├── product-service
├── order-service
├── notification-service

Infrastructure
├── mysql
├── redis
├── kafka
└── docker
```

For now, we'll build only **User Service**, but we'll structure it as if it is going to be deployed in production.

---

# Phase 1: User Service

## Responsibilities

User Service should handle:

```text
Register User
Login User
Generate JWT
Validate JWT
Role Management
User Profile
```

---

# Tech Stack

### Core

* Spring Boot 3.x
* Java 21 (or 17)
* Spring Security
* JWT
* Hibernate
* MySQL

### Future Integration

* Kafka
* Redis
* Docker
* Kubernetes

Don't integrate Kafka/Redis yet.

---

# User Service Folder Structure

```text
user-service

src/main/java

com.company.userservice

├── config
│   ├── SecurityConfig
│   └── JwtConfig
│
├── controller
│   ├── AuthController
│   └── UserController
│
├── service
│   ├── AuthService
│   ├── UserService
│   └── JwtService
│
├── repository
│   └── UserRepository
│
├── entity
│   ├── User
│   └── Role
│
├── dto
│   ├── LoginRequest
│   ├── RegisterRequest
│   ├── LoginResponse
│   └── UserResponse
│
├── mapper
│   └── UserMapper
│
├── security
│   ├── JwtAuthenticationFilter
│   └── CustomUserDetailsService
│
├── exception
│   ├── GlobalExceptionHandler
│   └── UserNotFoundException
│
└── UserServiceApplication
```

---

# Database Design

## users

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE,
    email VARCHAR(100) UNIQUE,
    password VARCHAR(255),
    role VARCHAR(20),
    created_at TIMESTAMP
);
```

---

# Entity

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}
```

---

# Role Enum

```java
public enum Role {

    USER,
    ADMIN
}
```

---

# APIs

## Register

```http
POST /api/v1/auth/register
```

Request

```json
{
  "username":"niraj",
  "email":"niraj@gmail.com",
  "password":"password123"
}
```

Response

```json
{
  "message":"User registered successfully"
}
```

---

## Login

```http
POST /api/v1/auth/login
```

Request

```json
{
  "email":"niraj@gmail.com",
  "password":"password123"
}
```

Response

```json
{
   "token":"jwt-token"
}
```

---

## Get Profile

```http
GET /api/v1/users/me
```

Header

```text
Authorization: Bearer <token>
```

Response

```json
{
  "id":1,
  "username":"niraj",
  "email":"niraj@gmail.com",
  "role":"USER"
}
```

---

# Security Flow

```text
Login
 |
 | verify password
 |
Generate JWT
 |
Return Token
 |
Client sends JWT
 |
JwtAuthenticationFilter
 |
SecurityContext
 |
Controller
```

This flow is asked in almost every Spring Security interview.

---

# Interview Concepts Covered in User Service

## Dependency Injection

Use constructor injection everywhere.

Questions:

* Why constructor injection?
* Why not field injection?

---

## Bean Lifecycle

Questions:

* What is a Bean?
* What is ApplicationContext?
* What is IOC Container?

---

## Spring Security

Questions:

* Authentication vs Authorization?
* SecurityFilterChain?
* UserDetailsService?
* PasswordEncoder?

---

## JWT

Questions:

* JWT structure?
* Claims?
* Expiration?
* Refresh token?

JWT structure:

```text
header.payload.signature
```

---

## JPA

Questions:

* save() vs saveAndFlush()
* Entity lifecycle
* Persistence context
* First level cache

---

## Validation

Add DTO validation:

```java
@NotBlank
@Email
@Size(min=8)
```

Questions:

* @Valid vs @Validated

---

## Exception Handling

Implement:

```java
@RestControllerAdvice
```

Questions:

* Why centralized exception handling?
* Custom exceptions?

---

## Logging

Use:

```java
@Slf4j
```

Never:

```java
System.out.println()
```

Questions:

* Log levels
* MDC
* Correlation IDs

---

# Deliverables for Phase 1

By the end of User Service you should have:

✅ Register API

✅ Login API

✅ JWT Authentication

✅ Spring Security

✅ Role-Based Authorization

✅ DTO Validation

✅ Global Exception Handling

✅ Logging

✅ MySQL Integration

✅ Unit Tests (later)

---

### Suggested implementation order

1. Create Spring Boot project.
2. Configure MySQL.
3. Create User entity and repository.
4. Build Register API.
5. Add password encryption (`BCryptPasswordEncoder`).
6. Build Login API.
7. Generate JWT.
8. Create `JwtAuthenticationFilter`.
9. Secure `/users/me`.
10. Add validation and exception handling.
11. Add logging.

When you've completed steps 1–3 (project setup, database configuration, and User entity/repository), share your code structure or screenshots, and I can review it like an interviewer and guide you through the next steps.
Our Future PORTs

8080 -> API Gateway
8081 -> User Service
8082 -> Product Service
8083 -> Order Service
8084 -> Notification Service