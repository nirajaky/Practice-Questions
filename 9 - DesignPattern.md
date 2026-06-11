For a **5–6 years Spring Boot interview**, don't just explain design patterns theoretically. Explain **where Spring actually uses them**.

# 1. Singleton Pattern

### Idea

Only one instance of an object exists.

### Spring Example

```java
@Service
public class UserService {
}
```

Spring creates only one `UserService` bean by default.

```java
@Autowired
private UserService service1;

@Autowired
private UserService service2;
```

Both references point to the same bean instance.

### Interview Point

Spring Singleton ≠ GoF Singleton

* GoF Singleton → One object per JVM/ClassLoader
* Spring Singleton → One object per IoC Container

---

# 2. Factory Pattern

### Idea

Create objects without exposing object creation logic.

Instead of:

```java
new UserService();
```

Spring does:

```java
applicationContext.getBean(UserService.class);
```

### Where used?

* `BeanFactory`
* `ApplicationContext`

Both are factories that create and manage beans.

Interview answer:

> Spring IoC Container is essentially a Factory Pattern implementation.

---

# 3. Proxy Pattern

### Idea

A proxy object sits between the caller and the real object.

### Where used?

#### @Transactional

```java
@Transactional
public void saveUser() {
}
```

Spring creates:

```text
Client
  ↓
Proxy
  ↓
Real Service
```

Proxy:

1. Starts transaction
2. Calls method
3. Commits/Rolls back

---

#### @Async

```java
@Async
public void sendEmail() {
}
```

Proxy intercepts method and runs it in another thread.

---

#### Spring Security

```java
@PreAuthorize("hasRole('ADMIN')")
```

Proxy checks authorization before calling method.

---

### Most Asked Question

How does `@Transactional` work internally?

Answer:

> Spring creates a proxy around the bean using JDK Dynamic Proxy or CGLIB Proxy.

---

# 4. Template Method Pattern

### Idea

Parent class defines workflow.
Child classes fill specific steps.

### Example

#### JdbcTemplate

You write:

```java
jdbcTemplate.query(sql, rowMapper);
```

Internally Spring handles:

```text
Open Connection
↓
Execute Query
↓
Handle Exception
↓
Close Connection
```

You only provide query logic.

---

Other examples:

* `JdbcTemplate`
* `RestTemplate`
* `RedisTemplate`
* `KafkaTemplate`

---

# 5. Strategy Pattern

### Idea

Multiple algorithms.
Choose one at runtime.

### Example

```java
public interface PaymentStrategy {
    void pay();
}
```

```java
@Service
public class UpiPaymentStrategy
        implements PaymentStrategy {
}
```

```java
@Service
public class CardPaymentStrategy
        implements PaymentStrategy {
}
```

Choose based on request:

```java
paymentStrategy.pay();
```

---

### Spring Internal Example

Authentication

```java
AuthenticationProvider
```

Implementations:

```text
DaoAuthenticationProvider
LdapAuthenticationProvider
JwtAuthenticationProvider
```

Spring selects the appropriate strategy.

---

# 6. Observer Pattern

### Idea

One object publishes an event.
Many listeners receive it.

### Spring Example

Event Publisher:

```java
applicationEventPublisher.publishEvent(
        new UserCreatedEvent(user));
```

Listener:

```java
@EventListener
public void handle(UserCreatedEvent event) {
}
```

When event is published:

```text
Publisher
   ↓
Spring Event Bus
   ↓
Listener 1
Listener 2
Listener 3
```

---

Real examples:

* Spring Events
* Kafka Consumers
* RabbitMQ Listeners

---

# 7. Builder Pattern

### Idea

Construct complex objects step-by-step.

Without Builder:

```java
User user =
    new User("Niraj",
             "Mehta",
             30,
             "Bangalore");
```

Builder:

```java
User user =
    User.builder()
        .name("Niraj")
        .age(30)
        .build();
```

### Where used?

Lombok:

```java
@Builder
```

Spring examples:

```java
ResponseEntity
    .ok()
    .body(user);
```

```java
UriComponentsBuilder
```

```java
SecurityFilterChain Builder
```

---

# 8. Adapter Pattern

### Idea

Convert one interface into another.

### Spring Example

Spring MVC converts:

```java
HttpServletRequest
```

into

```java
@RequestBody UserRequest
```

using adapters and converters.

Another example:

```java
HandlerAdapter
```

inside Spring MVC.

---

# 9. Facade Pattern

### Idea

Provide a simple interface hiding complex operations.

Example:

```java
orderService.placeOrder();
```

Internally:

```text
Inventory Check
↓
Payment Processing
↓
Notification
↓
Shipping
```

User only calls:

```java
placeOrder();
```

Spring itself also provides many façade APIs over complex infrastructure.

---

# 10. Dependency Injection (IoC) Pattern

Not a GoF pattern but very important in Spring.

Instead of:

```java
UserRepository repo =
        new UserRepository();
```

Spring injects:

```java
@Autowired
private UserRepository repo;
```

This reduces coupling.

---

# Most Important Patterns for Spring Boot Interviews

If asked:

> "Which design patterns are used in Spring Boot?"

Answer:

| Pattern              | Spring Usage                            |
| -------------------- | --------------------------------------- |
| Singleton            | Default Bean Scope                      |
| Factory              | BeanFactory, ApplicationContext         |
| Proxy                | @Transactional, @Async, Spring Security |
| Template Method      | JdbcTemplate, RestTemplate              |
| Strategy             | AuthenticationProvider, ViewResolver    |
| Observer             | Application Events                      |
| Builder              | Lombok Builder, Security Config         |
| Adapter              | HandlerAdapter, Message Converters      |
| Facade               | Service Layer APIs                      |
| Dependency Injection | Entire Spring Framework                 |

A concise interview answer is:

> "Spring heavily uses Singleton, Factory, Proxy, Strategy, Template Method, Observer, Builder, and Adapter patterns. The most important ones to understand deeply are Singleton, Factory, Proxy, Strategy, and Template Method because they are used throughout Spring Boot features such as bean management, transactions, security, and JDBC operations."
