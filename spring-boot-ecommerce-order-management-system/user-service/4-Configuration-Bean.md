## What does the @Configuration and @Bean annotation does and how they are different from @Component ?

This is a very important Spring interview topic because it tests whether you understand the **IoC Container** and **Bean creation**.

---

# What is a Bean?

A **Bean** is simply an object managed by the Spring container.

For example:

```java
@Service
public class AuthServiceImpl {
}
```

Spring creates the object:

```java
new AuthServiceImpl()
```

and stores it inside the `ApplicationContext`.

That object becomes a Spring Bean.

---

# What does `@Component` do?

```java
@Component
public class EmailService {
}
```

During component scanning, Spring finds this class and creates a bean automatically.

Equivalent conceptually to:

```java
EmailService emailService = new EmailService();
```

stored inside Spring's container.

---

## Stereotype Annotations

These are all specialized versions of `@Component`:

```java
@Component
@Service
@Repository
@Controller
@RestController
```

Internally:

```java
@Service
```

is basically:

```java
@Component
```

with semantic meaning.

---

# What does `@Configuration` do?

Example:

```java
@Configuration
public class SecurityConfig {
}
```

A `@Configuration` class is used to define beans manually.

Think of it as:

> "This class contains bean creation logic."

---

# What does `@Bean` do?

Example:

```java
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

Spring executes:

```java
passwordEncoder()
```

and registers the returned object as a Bean.

So Spring stores:

```java
BCryptPasswordEncoder
```

inside the container.

Later:

```java
@RequiredArgsConstructor
@Service
public class AuthServiceImpl {

    private final PasswordEncoder passwordEncoder;
}
```

Spring injects the bean automatically.

---

# Why not just use `@Component`?

Because you often don't control the source code.

Example:

```java
public class BCryptPasswordEncoder {
}
```

This class belongs to Spring Security.

You cannot modify it:

```java
@Component
public class BCryptPasswordEncoder
```

because it is in an external library.

Instead:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

You tell Spring how to create it.

---

# Interview Comparison

## Using `@Component`

```java
@Component
public class EmailService {
}
```

Spring discovers it automatically.

---

## Using `@Bean`

```java
@Bean
public EmailService emailService() {
    return new EmailService();
}
```

Spring creates it using your method.

Both result in a Bean.

The difference is **how the Bean gets registered**.

---

# Why is `@Configuration` needed?

Example:

```java
@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

`@Configuration` tells Spring:

> Scan this class for `@Bean` methods.

Without `@Configuration`, Spring won't treat the class as a bean-definition source.

---

# Internal Working

When Spring starts:

1. Scan packages
2. Find:

```java
@Component
@Service
@Repository
@Controller
@Configuration
```

3. Create bean definitions

For `@Configuration`:

```java
@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

Spring executes:

```java
passwordEncoder()
```

and stores the returned object.

---

# Real Project Examples

### `@Component`

```java
@Component
public class JwtAuthenticationFilter {
}
```

Custom class created by you.

---

### `@Service`

```java
@Service
public class AuthServiceImpl {
}
```

Business logic.

---

### `@Repository`

```java
@Repository
public interface UserRepository
        extends JpaRepository<User, Long> {
}
```

Database layer.

---

### `@Configuration`

```java
@Configuration
public class SecurityConfig {
}
```

Configuration class.

---

### `@Bean`

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Manual bean registration.

---

# Advanced Interview Question

**Can I replace `@Configuration` with `@Component`?**

Technically:

```java
@Component
public class AppConfig {
}
```

can contain `@Bean` methods.

But Spring recommends:

```java
@Configuration
```

because it enables additional processing and ensures proper singleton behavior when `@Bean` methods call each other.

---

# Short Interview Answer

| Annotation       | Purpose                                              |
| ---------------- | ---------------------------------------------------- |
| `@Component`     | Automatically register your class as a Spring Bean   |
| `@Service`       | Business layer bean (`@Component` specialization)    |
| `@Repository`    | Persistence layer bean (`@Component` specialization) |
| `@Configuration` | Class that defines bean creation methods             |
| `@Bean`          | Registers the returned object as a Spring Bean       |

A concise interview answer is:

> "`@Component` is used when Spring should instantiate a class through component scanning. `@Bean` is used when I need to manually create and register an object, typically for third-party classes. `@Configuration` marks a class that contains such bean definitions."

