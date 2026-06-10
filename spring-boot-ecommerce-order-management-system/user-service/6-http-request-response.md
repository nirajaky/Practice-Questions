## Explain how http request is processed in Spring Boot application when Spring Security is enabled?

# Spring Boot Request Flow Notes

## Example Request

```http
GET /api/v1/users/me
Authorization: Bearer eyJ...
```

---

# 1. Request Reaches Tomcat

Spring Boot starts an embedded Tomcat server.

```java
@SpringBootApplication
public class UserServiceApplication
```

Flow:

```text
Browser/Postman
      |
      v
Tomcat (Port 8081)
```

Tomcat responsibilities:

* Accept HTTP requests
* Create `HttpServletRequest`
* Create `HttpServletResponse`
* Pass request into Servlet pipeline

---

# 2. Tomcat Executes Filters

Before any controller is called, Tomcat executes registered Servlet Filters.

```text
Tomcat
   |
Filter 1
Filter 2
Filter 3
   |
Servlet
```

Spring Security registers:

```text
DelegatingFilterProxy
```

Flow:

```text
Tomcat
   |
DelegatingFilterProxy
```

---

# 3. How DelegatingFilterProxy Gets Registered

Dependency added:

```xml
spring-boot-starter-security
```

Spring Boot Security Auto Configuration registers:

```java
DelegatingFilterProxy
```

with Tomcat.

Tomcat knows:

```text
For every request
execute DelegatingFilterProxy
```

---

# 4. DelegatingFilterProxy Purpose

Tomcat only understands:

* Servlets
* Filters

Tomcat does NOT understand Spring Beans.

`DelegatingFilterProxy` acts as a bridge.

Its job:

```text
Find Spring Bean
named springSecurityFilterChain
and execute it
```

---

# 5. How SecurityFilterChain Bean Is Picked Up

Our configuration:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http)
        throws Exception {

    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
                .anyRequest()
                .authenticated());

    return http.build();
}
```

During application startup:

Spring executes:

```java
securityFilterChain(http)
```

and creates:

```java
DefaultSecurityFilterChain
```

Spring Security collects all `SecurityFilterChain` beans and stores them inside:

```java
FilterChainProxy
```

Internally represented as:

```text
springSecurityFilterChain
```

Flow:

```text
SecurityConfig
      |
      v
SecurityFilterChain Bean
      |
      v
FilterChainProxy
      |
      v
springSecurityFilterChain
```

---

# 6. DelegatingFilterProxy Invokes FilterChainProxy

Flow:

```text
Tomcat
   |
DelegatingFilterProxy
   |
FilterChainProxy
```

`FilterChainProxy` is the master Spring Security filter.

---

# 7. FilterChainProxy Selects Matching SecurityFilterChain

If application has multiple chains:

```java
@Bean
SecurityFilterChain apiChain()

@Bean
SecurityFilterChain adminChain()
```

Spring selects the matching one.

In our project:

```text
Only one SecurityFilterChain exists
```

So it is selected.

---

# 8. Security Filters Execute

Simplified filter order:

```text
SecurityContextHolderFilter
      |
JwtAuthenticationFilter
      |
UsernamePasswordAuthenticationFilter
      |
AuthorizationFilter
```

---

# 9. JwtAuthenticationFilter Executes

Custom filter:

```java
public class JwtAuthenticationFilter
       extends OncePerRequestFilter
```

Responsibilities:

### Read Header

```http
Authorization: Bearer eyJ...
```

### Extract Token

```java
String token = authHeader.substring(7);
```

### Extract Username

```java
jwtService.extractUsername(token);
```

### Load User

```java
userDetailsService.loadUserByUsername(email);
```

### Create Authentication Object

```java
UsernamePasswordAuthenticationToken
```

### Store Authentication

```java
SecurityContextHolder
        .getContext()
        .setAuthentication(authToken);
```

Now Spring knows:

```text
Current User = niraj@gmail.com
Role = USER
```

---

# 10. AuthorizationFilter Executes

Configured rule:

```java
.anyRequest()
.authenticated()
```

Spring checks:

```java
SecurityContextHolder
        .getContext()
        .getAuthentication()
```

If authentication exists:

```text
Allow Request
```

Otherwise:

```text
401 Unauthorized
```

---

# 11. Request Reaches DispatcherServlet

Security processing completes.

Flow:

```text
Tomcat
   |
Security Filters
   |
DispatcherServlet
```

`DispatcherServlet` is Spring MVC's Front Controller.

---

# 12. DispatcherServlet Finds Controller

Request:

```http
GET /api/v1/users/me
```

Controller:

```java
@RestController
@RequestMapping("/api/v1/users")
```

Method:

```java
@GetMapping("/me")
```

DispatcherServlet asks:

```text
Which controller handles
/api/v1/users/me ?
```

Finds:

```java
UserController#getProfile()
```

---

# 13. Method Argument Resolution

Method:

```java
@GetMapping("/me")
public User getProfile(
        @AuthenticationPrincipal User user) {

    return user;
}
```

Spring gets authenticated user from:

```java
SecurityContextHolder
```

and injects it.

---

# 14. Controller Executes

```java
return user;
```

Controller returns object.

---

# 15. Response Conversion

Spring uses:

```java
MappingJackson2HttpMessageConverter
```

to convert:

```java
User
```

into JSON.

Example:

```json
{
  "id": 1,
  "email": "niraj@gmail.com",
  "role": "USER"
}
```

---

# 16. Response Sent Back

Flow:

```text
Controller
   |
DispatcherServlet
   |
Tomcat
   |
Browser/Postman
```

---

# Complete End-to-End Flow

```text
HTTP Request
      |
      v
Tomcat
      |
      v
DelegatingFilterProxy
      |
      v
FilterChainProxy
      |
      v
SecurityFilterChain
      |
      v
JwtAuthenticationFilter
      |
      v
AuthorizationFilter
      |
      v
DispatcherServlet
      |
      v
HandlerMapping
      |
      v
UserController
      |
      v
Service
      |
      v
Repository
      |
      v
Database
      |
      v
Controller Response
      |
      v
Jackson JSON Converter
      |
      v
Tomcat
      |
      v
Client
```

---

# Important Interview Questions

### What is Tomcat?

A Servlet Container that:

* Accepts HTTP requests
* Creates request/response objects
* Executes filters and servlets

---

### What is DelegatingFilterProxy?

A bridge between Tomcat Filters and Spring Beans.

Purpose:

```text
Find springSecurityFilterChain bean
and execute it
```

---

### What is FilterChainProxy?

Master Spring Security filter.

Responsible for:

* Holding SecurityFilterChains
* Selecting appropriate chain
* Executing security filters

---

### What is SecurityFilterChain?

Defines:

* Authentication rules
* Authorization rules
* Session management
* CSRF settings
* Custom filters

---

### Why use JwtAuthenticationFilter?

To:

* Read JWT token
* Validate JWT
* Load user
* Store Authentication in SecurityContext

---

### What is SecurityContextHolder?

Stores currently authenticated user information.

Example:

```java
SecurityContextHolder
        .getContext()
        .getAuthentication();
```

---

### What is DispatcherServlet?

Front Controller of Spring MVC.

Responsibilities:

* Find controller
* Invoke controller method
* Return response

---

### Difference Between Filter and DispatcherServlet

**Filter**

```text
Runs before Spring MVC
```

Used for:

* Security
* Logging
* Auditing

---

**DispatcherServlet**

```text
Runs after filters
```

Used for:

* Controller mapping
* Request handling
* Response generation

Flow:

```text
Tomcat
   |
Filters
   |
DispatcherServlet
   |
Controller
```
