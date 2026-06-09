## Question 1: Why don't we put @Repository on a JpaRepository interface?

Good answer:
```Java
Spring Data JPA automatically detects interfaces extending JpaRepository and creates proxy 
implementations at runtime. These generated implementations are registered as repository beans, 
so adding @Repository is optional and generally unnecessary.
```

## Question 2: What is Stereotype annotation and diff between them and why not use @Component instead?



# What is a Stereotype Annotation?

A stereotype annotation tells Spring:

> "This class is a Spring-managed bean and has a specific role in the application architecture."

The main stereotype annotations are:

```java
@Component
@Service
@Repository
@Controller
@RestController
```

All of them ultimately derive from `@Component`.

---

# Hierarchy

```text
@Component
    |
    +---- @Service
    |
    +---- @Repository
    |
    +---- @Controller
              |
              +---- @RestController
```

For example:

```java
@Service
public class UserService {
}
```

Internally:

```java
@Service
```

is meta-annotated with:

```java
@Component
```

so Spring detects it during component scanning.

---

# Then why not use @Component everywhere?

Technically you can.

This works:

```java
@Component
public class UserService {
}
```

Spring creates the bean successfully.

But it's not recommended.

---

# Example

Suppose you see:

```java
@Component
public class UserService {
}
```

Questions:

* Is it a service?
* Is it a repository?
* Is it a helper class?
* Is it a scheduler?

Not immediately clear.

Now compare:

```java
@Service
public class UserService {
}
```

Instantly obvious.

---

# Purpose of Each Annotation

## @Component

Generic bean.

Used when no special meaning exists.

Example:

```java
@Component
public class JwtUtil {
}
```

Utility/helper classes.

---

## @Service

Business logic layer.

Example:

```java
@Service
public class AuthService {
}
```

Contains:

```java
register()
login()
createUser()
```

Interview answer:

> Indicates business logic and improves readability.

---

## @Repository

Persistence layer.

Example:

```java
@Repository
public class UserJdbcRepository {
}
```

Extra benefit:

Spring translates database exceptions.

Example:

```text
SQLException
        ↓
DataAccessException
```

This feature is specific to repository beans.

---

## @Controller

MVC controller returning views.

Example:

```java
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }
}
```

Returns:

```text
index.html
```

---

## @RestController

REST APIs.

Equivalent to:

```java
@Controller
@ResponseBody
```

Example:

```java
@RestController
public class UserController {

    @GetMapping("/users")
    public List<User> getUsers() {
        return users;
    }
}
```

Returns JSON.

---

# In Our User Service

Use:

```java
@RestController
public class AuthController
```

```java
@Service
public class AuthService
```

```java
public interface UserRepository
        extends JpaRepository<User,Long>
```

No annotation needed because Spring Data creates the implementation.

---

# Interview Question

### Is @Service different from @Component?

Technically:

```text
No
```

Both create Spring beans.

Functional difference:

```text
Almost none
```

Semantic difference:

```text
Huge
```

`@Service` tells developers:

```text
This class contains business logic.
```

---

# Advanced Interview Question

### Why did Spring create @Service and @Repository if @Component already exists?

Answer:

> To clearly separate application layers and allow future framework-specific behavior.

Example:

```java
@Repository
```

already has additional behavior:

```text
Exception Translation
```

which `@Component` does not provide.

---

# Senior-Level Answer

If asked:

> Why don't you use @Component everywhere?

A strong answer is:

> I could use `@Component` everywhere because all stereotype annotations are ultimately Spring components. However, I prefer `@Service`, `@Repository`, and `@RestController` because they clearly communicate the responsibility of each class, improve maintainability, and allow Spring to apply layer-specific behavior such as exception translation for repositories.


## Question 3: DIfference between @Mock and @MockBean in unit test case? 

This is a very common Spring Boot interview question.

The short answer:

| Annotation  | Created By  | Loads Spring Context? | Use Case                 |
| ----------- | ----------- | --------------------- | ------------------------ |
| `@Mock`     | Mockito     | ❌ No                  | Unit Testing             |
| `@MockBean` | Spring Boot | ✅ Yes                 | Integration/Spring Tests |

---

# 1. @Mock

`@Mock` is a Mockito annotation.

Example:

```java
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;
}
```

What happens?

```text
Mockito creates fake UserRepository
        ↓
Injects into AuthService
        ↓
No Spring Context Started
```

Very fast.

---

## Example

Production code:

```java
@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow();
    }
}
```

Test:

```java
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    AuthService authService;

    @Test
    void testGetUser() {

        User user = new User();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        User result = authService.getUser(1L);

        assertNotNull(result);
    }
}
```

No Spring Boot startup.

Pure unit test.

---

# 2. @MockBean

`@MockBean` belongs to Spring Boot Test.

Example:

```java
@SpringBootTest
class AuthServiceTest {

    @MockBean
    UserRepository userRepository;

    @Autowired
    AuthService authService;
}
```

What happens?

```text
Spring Context Starts
       ↓
Real AuthService Bean Created
       ↓
Real UserRepository Bean Removed
       ↓
Mock UserRepository Injected
```

---

## Example

```java
@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testGetUser() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(new User()));

        User user = authService.getUser(1L);

        assertNotNull(user);
    }
}
```

Here Spring Boot starts the application context.

Slower but useful when testing Spring wiring.

---

# Visual Difference

## @Mock

```text
Mockito
   |
   +-- Mock UserRepository
   |
   +-- Create AuthService

No Spring
```

---

## @MockBean

```text
Spring Context
      |
      +-- AuthService Bean
      |
      +-- UserRepository Bean
               ↓
         replaced by MockBean
```

---

# Interview Question

### Which one is faster?

`@Mock`

Because:

```text
No Spring Context
No Bean Creation
No Auto Configuration
```

Runs in milliseconds.

---

### Which one should be preferred for unit testing?

`@Mock`

Because a unit test should test only one class.

Example:

```java
AuthService
```

Mock:

```java
UserRepository
PasswordEncoder
JwtService
```

and test only AuthService logic.

---

### When should I use @MockBean?

When Spring context is required.

Examples:

```java
@SpringBootTest
@WebMvcTest
@DataJpaTest
```

and you want to replace a real bean with a mock.

---

# Real Project Example (User Service)

### Unit Test

```java
@Mock
UserRepository repository;

@Mock
PasswordEncoder encoder;

@InjectMocks
AuthService authService;
```

Testing:

```java
register()
login()
```

logic.

---

### Controller Test

```java
@WebMvcTest(AuthController.class)
```

```java
@MockBean
AuthService authService;
```

Because:

```text
Spring MVC starts
Controller bean created
AuthService bean required
Replace it with mock
```

---

# Senior-Level Interview Answer

> `@Mock` is a Mockito annotation used in pure unit tests and does not require the Spring context. `@MockBean` is a Spring Boot testing annotation that creates a Mockito mock and registers it in the Spring application context, replacing any existing bean of the same type. I use `@Mock` for fast unit tests and `@MockBean` when testing Spring-managed components such as controllers or integration scenarios.

## Question 4: What is @Valid annotation?

`@Valid` is used to trigger **Bean Validation** on an object before your controller method executes.

Example:

```java
@PostMapping("/register")
public RegisterResponse register(
        @Valid @RequestBody RegisterRequest request) {

    return authService.register(request);
}
```

Your DTO:

```java
@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Invalid email")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
```

When a request comes in, Spring does:

1. Convert JSON → `RegisterRequest`
2. See `@Valid`
3. Check all validation annotations (`@NotBlank`, `@Email`, `@Size`)
4. If validation fails, throw `MethodArgumentNotValidException`
5. Your `@RestControllerAdvice` handles it

---

### Without `@Valid`

Request:

```json
{
  "username": "",
  "email": "abc",
  "password": "123"
}
```

Spring will accept it and call:

```java
authService.register(request);
```

with invalid data.

---

### With `@Valid`

The same request causes validation to fail before reaching your service.

Response:

```json
{
  "username": "Username is required",
  "email": "Invalid email",
  "password": "Password must be at least 8 characters"
}
```

---

## Common Validation Annotations

```java
@NotNull
```

Value cannot be `null`.

```java
@NotBlank
```

For strings: cannot be `null`, empty, or only spaces.

```java
@NotEmpty
```

Cannot be `null` or empty.

```java
@Email
```

Must be a valid email format.

```java
@Size(min = 8, max = 20)
```

String/Collection size constraints.

```java
@Pattern(regexp = "...")
```

Custom regex validation.

---

## Interview Question: `@Valid` vs `@Validated`

### `@Valid`

From Jakarta Validation.

Used for object validation.

```java
@PostMapping
public void create(@Valid @RequestBody UserRequest request)
```

---

### `@Validated`

Spring-specific.

Supports validation groups and method parameter validation.

Example:

```java
@RestController
@Validated
public class UserController {

    @GetMapping("/{id}")
    public User getUser(
            @PathVariable @Min(1) Long id) {
        return null;
    }
}
```

Without `@Validated`, the `@Min(1)` on method parameters won't be applied.

---

### Interview Answer

**Q: What does `@Valid` do?**

> `@Valid` triggers Bean Validation on an object. Spring validates fields annotated with constraints like `@NotBlank`, `@Email`, and `@Size`. If validation fails, Spring throws `MethodArgumentNotValidException` before the controller logic executes.

---

### One more advanced example

Nested validation:

```java
public class RegisterRequest {

    @Valid
    private AddressDto address;
}
```

```java
public class AddressDto {

    @NotBlank
    private String city;
}
```

The inner `AddressDto` will also be validated because of the nested `@Valid`.

This nested validation question occasionally comes up in Spring Boot interviews.

