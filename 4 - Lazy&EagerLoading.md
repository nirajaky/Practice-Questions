This is one of the most frequently asked JPA/Hibernate interview topics.

# What is Loading?

Suppose we have:

```java
@Entity
public class User {

    @Id
    private Long id;

    private String name;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;
}
```

One user can have many orders.

When we fetch a user:

```java
User user = userRepository.findById(1L).get();
```

Question:

> Should Hibernate also fetch all orders immediately?

Two strategies exist:

1. Lazy Loading
2. Eager Loading

---

# Lazy Loading

```java
@OneToMany(
    mappedBy = "user",
    fetch = FetchType.LAZY
)
private List<Order> orders;
```

Meaning:

> Load User now.
>
> Load Orders only when someone actually accesses them.

---

## Example

```java
User user =
    userRepository.findById(1L).get();
```

Hibernate executes:

```sql
SELECT *
FROM users
WHERE id = 1;
```

Only user is loaded.

---

Later:

```java
user.getOrders();
```

Now Hibernate executes:

```sql
SELECT *
FROM orders
WHERE user_id = 1;
```

Second query is fired.

---

## Visual

```text
findById()

User
 |
Orders (NOT Loaded)
```

Later:

```text
user.getOrders()
      |
      v
Load Orders
```

---

## Benefits

### Faster Initial Query

```sql
SELECT *
FROM users
```

instead of:

```sql
SELECT *
FROM users
LEFT JOIN orders
```

---

### Less Memory

If a user has:

```text
10,000 Orders
```

they won't be loaded unnecessarily.

---

# Eager Loading

```java
@OneToMany(
    mappedBy = "user",
    fetch = FetchType.EAGER
)
private List<Order> orders;
```

Meaning:

> Whenever User is loaded,
> Orders are loaded immediately.

---

## Example

```java
User user =
    userRepository.findById(1L).get();
```

Hibernate may execute:

```sql
SELECT *
FROM users
LEFT JOIN orders
ON users.id = orders.user_id
WHERE users.id = 1;
```

or multiple queries internally.

---

Orders are already available:

```java
user.getOrders();
```

No additional query.

---

## Visual

```text
findById()

User
 |
Orders Loaded
```

Everything fetched immediately.

---

# Real Example

Suppose:

```text
User
 |
10000 Orders
```

---

## Lazy

```java
User user =
    userRepository.findById(1L).get();
```

Query:

```sql
SELECT * FROM users;
```

Only user data fetched.

---

## Eager

```java
User user =
    userRepository.findById(1L).get();
```

Query:

```sql
SELECT *
FROM users
JOIN orders
```

All 10,000 orders fetched immediately.

Huge memory and performance impact.

---

# Hibernate Defaults

Interview favorite.

## @ManyToOne

Default:

```java
@ManyToOne
private User user;
```

is:

```java
FetchType.EAGER
```

---

## @OneToOne

Default:

```java
@OneToOne
```

is:

```java
FetchType.EAGER
```

---

## @OneToMany

Default:

```java
@OneToMany
```

is:

```java
FetchType.LAZY
```

---

## @ManyToMany

Default:

```java
@ManyToMany
```

is:

```java
FetchType.LAZY
```

---

# LazyInitializationException

Very common interview question.

Example:

```java
User user =
    userRepository.findById(1L).get();
```

Transaction ends.

Later:

```java
user.getOrders();
```

Hibernate tries:

```sql
SELECT * FROM orders
```

But Session is already closed.

Result:

```text
LazyInitializationException
```

---

## Example

```java
@Transactional
public User getUser() {

    return userRepository
            .findById(1L)
            .get();
}
```

Controller:

```java
User user = service.getUser();

user.getOrders();
```

Transaction already finished.

Exception occurs.

---

# N+1 Problem

Very important.

Suppose:

```java
List<User> users =
    userRepository.findAll();
```

Query 1:

```sql
SELECT *
FROM users;
```

Returns:

```text
100 Users
```

Now:

```java
for(User u : users) {
    u.getOrders();
}
```

Hibernate fires:

```sql
SELECT * FROM orders WHERE user_id=1;
SELECT * FROM orders WHERE user_id=2;
SELECT * FROM orders WHERE user_id=3;
...
```

Total:

```text
1 + 100 Queries
```

This is called:

```text
N+1 Problem
```

A major performance issue.

---

# Best Practice

Many beginners do:

```java
@OneToMany(
    fetch = FetchType.EAGER
)
```

to avoid lazy loading issues.

Bad idea.

Can cause:

* Huge joins
* Large memory usage
* Slow APIs

---

Most production applications prefer:

```java
@OneToMany(
    fetch = FetchType.LAZY
)
```

and fetch explicitly when needed.

Example:

```java
@Query("""
SELECT u
FROM User u
JOIN FETCH u.orders
WHERE u.id = :id
""")
```

This loads both User and Orders in a controlled way.

---

# Spring Boot Interview Answer

### Lazy Loading

Data is loaded only when accessed for the first time.

```java
fetch = FetchType.LAZY
```

Benefits:

* Better performance
* Less memory consumption

Drawback:

* Can cause `LazyInitializationException`
* Can cause N+1 queries if not handled properly

---

### Eager Loading

Data is loaded immediately with the parent entity.

```java
fetch = FetchType.EAGER
```

Benefits:

* Data available immediately
* No lazy loading exception

Drawbacks:

* Unnecessary joins
* Higher memory usage
* Slower queries

---

### Which Should We Prefer?

For most production systems:

```java
FetchType.LAZY
```

is preferred.

Load related entities explicitly using:

* `JOIN FETCH`
* Entity Graphs
* DTO projections

rather than making every relationship eager.
