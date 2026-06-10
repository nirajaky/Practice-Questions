This is a very common Java interview question.

# Checked Exception

Exceptions that are checked by the compiler.

If a method can throw a checked exception, you **must** either:

1. Handle it using `try-catch`
2. Declare it using `throws`

Example:

```java
import java.io.IOException;

public void readFile() throws IOException {
    throw new IOException("File not found");
}
```

or

```java
try {
    readFile();
} catch (IOException e) {
    e.printStackTrace();
}
```

If you don't do either, the code won't compile.

---

## Examples of Checked Exceptions

* `IOException`
* `SQLException`
* `FileNotFoundException`
* `ClassNotFoundException`
* `ParseException`

Inheritance:

```text
Throwable
   |
Exception
   |
Checked Exceptions
```

---

# Runtime Exception (Unchecked Exception)

Exceptions that are **not checked by the compiler**.

You are not forced to catch or declare them.

Example:

```java
public void divide() {
    int result = 10 / 0;
}
```

Compiles fine.

At runtime:

```text
ArithmeticException
```

is thrown.

---

## Examples of Runtime Exceptions

* `NullPointerException`
* `ArithmeticException`
* `ArrayIndexOutOfBoundsException`
* `IllegalArgumentException`
* `IllegalStateException`
* `NumberFormatException`

Inheritance:

```text
Throwable
   |
RuntimeException
   |
Unchecked Exceptions
```

---

# Hierarchy

```text
Throwable
│
├── Error
│     ├── OutOfMemoryError
│     └── StackOverflowError
│
└── Exception
      │
      ├── RuntimeException
      │     ├── NullPointerException
      │     ├── IllegalArgumentException
      │     └── ArithmeticException
      │
      └── Checked Exceptions
            ├── IOException
            ├── SQLException
            └── ParseException
```

---

# Example Comparison

## Checked

```java
public void processFile() throws IOException {
    Files.readString(Path.of("test.txt"));
}
```

Compiler says:

```text
Unhandled exception IOException
```

if not handled.

---

## Runtime

```java
public void process() {
    String name = null;
    name.length();
}
```

Compiles successfully.

Fails only at runtime.

---

# Why Do Runtime Exceptions Exist?

Because many programming errors cannot reasonably be recovered from.

Example:

```java
String name = null;
name.length();
```

What should Java force you to do?

```java
try {
    ...
} catch (NullPointerException e) {
}
```

That would make code messy.

Hence runtime exceptions.

---

# Spring Boot Perspective

In Spring applications, we usually create custom exceptions as:

```java
public class UserNotFoundException
        extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
```

not:

```java
public class UserNotFoundException
        extends Exception {
}
```

---

## Why?

Example:

```java
public User getUser(Long id) {

    return userRepository.findById(id)
            .orElseThrow(() ->
                    new UserNotFoundException("User not found"));
}
```

No need to write:

```java
throws UserNotFoundException
```

everywhere.

Cleaner code.

---

# Transactions Interview Question

Spring rolls back transactions automatically for:

```java
RuntimeException
```

Example:

```java
@Transactional
public void createUser() {

    userRepository.save(user);

    throw new RuntimeException();
}
```

Result:

```text
Rollback
```

---

But for checked exceptions:

```java
@Transactional
public void createUser() throws IOException {

    userRepository.save(user);

    throw new IOException();
}
```

Default behavior:

```text
No Rollback
```

unless configured.

To rollback checked exceptions:

```java
@Transactional(
    rollbackFor = Exception.class
)
```

---

# Interview Answer

| Feature                     | Checked Exception      | Runtime Exception                    |
| --------------------------- | ---------------------- | ------------------------------------ |
| Checked by Compiler         | Yes                    | No                                   |
| Must Handle/Catch           | Yes                    | No                                   |
| Compile-time Validation     | Yes                    | No                                   |
| Extends                     | `Exception`            | `RuntimeException`                   |
| Used For                    | Recoverable situations | Programming errors/business failures |
| Spring Transaction Rollback | No (default)           | Yes (default)                        |

---

## Common Spring Boot Practice

For business/application exceptions:

```java
UserNotFoundException
OrderNotFoundException
DuplicateEmailException
InvalidOrderStateException
```

use:

```java
extends RuntimeException
```

This is the approach you'll see in most production Spring Boot microservices.
