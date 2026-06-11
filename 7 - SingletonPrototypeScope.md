For Spring Boot interviews, when someone asks:

> "Explain Singleton, Prototype and other bean scopes in Spring"

they are usually referring to **Spring Bean Scopes**, not GoF design patterns.

# What is a Bean Scope?

A bean scope defines **how many instances of a bean Spring should create** and **how long they live**.

---

# 1. Singleton Scope (Default)

```java
@Service
@Scope("singleton")
public class UserService {
}
```

or simply:

```java
@Service
public class UserService {
}
```

because singleton is the default.

### Behavior

Only **one bean instance** is created for the entire Spring Container.

```java
@Autowired
private UserService userService1;

@Autowired
private UserService userService2;
```

Both references point to the same object.

```java
userService1 == userService2
```

Result:

```java
true
```

### Where used?

Almost every Spring component:

```java
@Service
@Repository
@Component
@Controller
```

### Why default?

* Saves memory
* Better performance
* Most services are stateless

---

# 2. Prototype Scope

```java
@Component
@Scope("prototype")
public class Employee {
}
```

### Behavior

Every time Spring is asked for the bean, a new object is created.

```java
Employee e1 =
context.getBean(Employee.class);

Employee e2 =
context.getBean(Employee.class);
```

Result:

```java
e1 == e2
```

```java
false
```

### Use Cases

* Stateful objects
* Temporary processing objects
* Report generators

Example:

```java
@Component
@Scope("prototype")
public class PdfGenerator {
}
```

Every request gets a fresh generator.

---

## Interview Trap

Consider:

```java
@Service
public class UserService {

    @Autowired
    private Employee employee;
}
```

where

```java
@Scope("prototype")
@Component
public class Employee {
}
```

Many people think a new Employee object will be created every time.

Wrong.

Spring injects Employee only once during UserService creation.

So the same Employee instance remains inside UserService.

To get a new prototype bean every time:

```java
@Autowired
private ObjectProvider<Employee> provider;
```

```java
Employee emp = provider.getObject();
```

Now a fresh instance is created.

This is a very common 5+ years interview question.

---

# 3. Request Scope

```java
@Component
@Scope(
 value = WebApplicationContext.SCOPE_REQUEST,
 proxyMode = ScopedProxyMode.TARGET_CLASS
)
public class RequestData {
}
```

### Behavior

One bean per HTTP request.

Request 1:

```java
RequestData -> Object A
```

Request 2:

```java
RequestData -> Object B
```

Different objects.

### Use Cases

Store request-specific information:

```java
requestId
loggedInUser
requestTimestamp
```

---

# 4. Session Scope

```java
@Component
@Scope(
 value = WebApplicationContext.SCOPE_SESSION,
 proxyMode = ScopedProxyMode.TARGET_CLASS
)
public class UserSession {
}
```

### Behavior

One bean per user session.

Example:

User A logs in

```java
UserSession -> Object A
```

User B logs in

```java
UserSession -> Object B
```

Each user gets their own bean.

### Use Cases

* Shopping cart
* User preferences
* Session data

---

# 5. Application Scope

```java
@Component
@ApplicationScope
public class AppConfig {
}
```

### Behavior

One bean per ServletContext.

Shared across the whole web application.

---

# 6. WebSocket Scope

```java
@Scope("websocket")
```

One bean per WebSocket connection.

Rarely asked.

---

# Singleton vs Prototype

| Feature              | Singleton              | Prototype         |
| -------------------- | ---------------------- | ----------------- |
| Instances            | One                    | New every request |
| Default Scope        | Yes                    | No                |
| Memory Usage         | Low                    | Higher            |
| Thread Safety Needed | Yes                    | Usually No        |
| Common Usage         | Services, Repositories | Stateful Objects  |

---

# Bean Lifecycle Difference

### Singleton

Spring manages entire lifecycle.

```text
Create Bean
↓
Inject Dependencies
↓
Use Bean
↓
Destroy Bean
```

Spring calls:

```java
@PostConstruct
@PreDestroy
```

both methods.

---

### Prototype

Spring only creates bean.

```text
Create Bean
↓
Inject Dependencies
↓
Give Bean To User
```

Spring does NOT manage destruction.

```java
@PreDestroy
```

will not be called automatically.

Another frequently asked interview question.

---

# Interview Question

### Is Spring Singleton same as Singleton Design Pattern?

Answer:

No.

**Singleton Design Pattern**

* One object per JVM/ClassLoader.

**Spring Singleton Scope**

* One object per Spring IoC Container.

If multiple Spring containers exist, each can have its own singleton bean.

---

# One-line Interview Answer

> Spring provides bean scopes like Singleton (default, one bean per container), Prototype (new bean every retrieval), Request (one bean per HTTP request), Session (one bean per user session), and Application scope. Singleton beans are most commonly used in Spring Boot services because they are lightweight and efficient.
