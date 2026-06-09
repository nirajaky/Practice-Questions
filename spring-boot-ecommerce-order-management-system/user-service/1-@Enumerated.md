## What this does?

```java
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private Role role;
```
Ans: 
Consider your enum:

```java
public enum Role {
    USER,
    ADMIN
}
```

And in your entity:

```java
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private Role role;
```

## What `@Enumerated` does

JPA does not automatically know how you want to store an enum in the database.

`@Enumerated` tells Hibernate how to map the enum to a database column.

---

## Using `EnumType.STRING`

```java
@Enumerated(EnumType.STRING)
private Role role;
```

If you save:

```java
user.setRole(Role.ADMIN);
```

Database value becomes:

```text
ADMIN
```

Example table:

| id | username | role  |
| -- | -------- | ----- |
| 1  | niraj    | ADMIN |
| 2  | john     | USER  |

This is the recommended approach.

---

## What if you use `EnumType.ORDINAL`?

```java
@Enumerated(EnumType.ORDINAL)
private Role role;
```

Then Hibernate stores the enum position:

```java
USER  -> 0
ADMIN -> 1
```

Database:

| id | username | role |
| -- | -------- | ---- |
| 1  | niraj    | 1    |
| 2  | john     | 0    |

---

## Why ORDINAL is dangerous

Suppose after 1 year a developer changes:

```java
public enum Role {
    SUPER_ADMIN,
    USER,
    ADMIN
}
```

Now positions become:

```java
SUPER_ADMIN -> 0
USER        -> 1
ADMIN       -> 2
```

But old database records still contain:

```text
0
1
```

So:

```text
0 -> SUPER_ADMIN
1 -> USER
```

Suddenly users get wrong roles.

This can create production security issues.

---

## Why STRING is safer

Database stores:

```text
USER
ADMIN
```

Even if you add:

```java
public enum Role {
    SUPER_ADMIN,
    USER,
    ADMIN
}
```

Old data remains:

```text
USER
ADMIN
```

No corruption occurs.

---

## What does `@Column(nullable = false)` do?

```java
@Column(nullable = false)
private Role role;
```

Generates:

```sql
role varchar(255) not null
```

This prevents:

```java
user.setRole(null);
```

from being persisted.

Every user must have a role.

---

## Interview Answer (5-6 Years)

If asked:

> Why do you use `@Enumerated(EnumType.STRING)` instead of `ORDINAL`?

A strong answer is:

> `EnumType.STRING` stores the enum name in the database, making the data readable and resilient to enum reordering. `ORDINAL` stores numeric positions, which can corrupt existing data if new enum values are inserted or the order changes. Therefore STRING is generally preferred in production applications.
