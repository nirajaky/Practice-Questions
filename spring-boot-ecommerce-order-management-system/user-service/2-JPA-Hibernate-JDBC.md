## What is JPA, Hibernates and JDBC and what are we using in our user-service and when JDBC is used?


This is one of the most frequently asked interview topics.

Many developers use Spring Data JPA daily but cannot explain what's happening underneath.

# The Relationship

```text id="7t64pi"
Your Code
    |
Spring Data JPA
    |
Hibernate
    |
JDBC
    |
MySQL Driver
    |
MySQL Database
```

So in our User Service, **all of these are being used**.

---

# 1. JDBC (Java Database Connectivity)

JDBC is the lowest-level Java API for talking to a database.

Without JPA/Hibernate:

```java
Connection con =
        DriverManager.getConnection(url, user, password);

PreparedStatement ps =
        con.prepareStatement(
                "select * from users where email=?");

ps.setString(1, email);

ResultSet rs = ps.executeQuery();
```

You manually:

* Open connection
* Create SQL
* Execute query
* Read ResultSet
* Close connection

Lots of boilerplate code.

---

# 2. Hibernate

Hibernate is an ORM (Object Relational Mapping) framework.

Instead of writing SQL manually:

```java
User user = new User();
user.setEmail("niraj@gmail.com");

entityManager.persist(user);
```

Hibernate generates SQL:

```sql
insert into users(email)
values('niraj@gmail.com');
```

for you.

Hibernate sits on top of JDBC.

Hibernate itself uses JDBC internally.

---

# 3. JPA

JPA is not a framework.

JPA = Java Persistence API

It is only a specification (set of interfaces and rules).

Think of it like:

```text id="zxdl3r"
JPA = Contract

Hibernate = Implementation
```

Example:

JPA provides:

```java
EntityManager
```

Hibernate implements it.

Similarly:

```java
@Entity
@OneToMany
@ManyToOne
```

are JPA annotations.

---

# In Our User Service

You added:

```xml
spring-boot-starter-data-jpa
```

This brings:

```text id="9f3sd9"
Spring Data JPA
      +
Hibernate
```

Then you created:

```java
@Entity
public class User {
}
```

and

```java
public interface UserRepository
        extends JpaRepository<User, Long> {
}
```

When you call:

```java
userRepository.save(user);
```

The flow is:

```text id="x4hj2w"
save(user)
     |
Spring Data JPA
     |
Hibernate
     |
JDBC
     |
MySQL Driver
     |
MySQL
```

---

# Example

You write:

```java
userRepository.findByEmail(email);
```

Hibernate generates:

```sql
select *
from users
where email=?
```

JDBC executes it.

MySQL returns data.

Hibernate converts rows into:

```java
User
```

object.

---

# Then Why Learn JDBC?

Because interviewers often ask:

### What happens when save() is called?

Expected answer:

```text id="g0b74o"
Spring Data JPA
→ Hibernate
→ JDBC
→ Database
```

---

### Why is JDBC still important?

Because every ORM eventually uses JDBC.

Understanding JDBC helps in:

* Connection pools
* Performance tuning
* Query optimization
* Transaction management

---

# When Do Companies Use JDBC Directly?

For very high-performance operations.

Example:

```java
JdbcTemplate
```

or

```java
NamedParameterJdbcTemplate
```

instead of JPA.

Reasons:

* Faster
* Full SQL control
* Complex joins
* Bulk updates

Example:

```java
jdbcTemplate.query(
        "select * from users where email=?",
        rowMapper,
        email);
```

---

# When to Use JPA?

Use JPA for:

✅ CRUD APIs

✅ User Service

✅ Product Service

✅ Most business applications

Example in our project:

```java
User
Product
Order
```

entities.

---

# When to Use JDBC?

Use JDBC (usually via `JdbcTemplate`) for:

✅ Large reports

✅ Batch processing

✅ Bulk inserts

✅ Complex SQL queries

✅ Performance-critical queries

Example:

```sql
select
u.name,
count(o.id)
from users u
join orders o
group by u.name
```

Such queries are often easier and faster with JDBC.

---

# Interview Answer (5–6 Years)

**Q: Are we using JDBC in our User Service?**

Yes.

We are using it indirectly.

```text id="1ag8x8"
Spring Data JPA
     ↓
Hibernate
     ↓
JDBC
     ↓
MySQL Driver
     ↓
Database
```

We write repository methods using JPA, Hibernate generates SQL, and JDBC actually communicates with the database.

**Q: Difference between JPA, Hibernate, and JDBC?**

| Technology | What it is                             |
| ---------- | -------------------------------------- |
| JDBC       | Low-level Java API to access databases |
| JPA        | Specification/API for ORM              |
| Hibernate  | Most popular implementation of JPA     |

A concise interview summary is:

> JPA defines *what* ORM operations should look like, Hibernate implements those operations, and Hibernate uses JDBC underneath to execute SQL against the database.
