This is a very important JPA interview topic because interviewers often ask:

> "Show me how the tables look in the database."

Let's understand from the SQL perspective.

---

# 1. OneToOne

Example:

```text
User
 |
Address
```

One User has one Address.

One Address belongs to one User.

---

## Entity

```java
@Entity
public class User {

    @Id
    private Long id;

    @OneToOne
    private Address address;
}
```

```java
@Entity
public class Address {

    @Id
    private Long id;
}
```

---

## Tables

### USER

| id | address_id |
| -- | ---------- |
| 1  | 101        |
| 2  | 102        |

### ADDRESS

| id  |
| --- |
| 101 |
| 102 |

Notice:

```sql
address_id
```

is a Foreign Key.

```sql
USER.address_id
        |
        v
ADDRESS.id
```

---

# 2. ManyToOne

Example:

```text
Many Orders
      |
      v
One User
```

A user can have many orders.

Each order belongs to one user.

---

## Entity

```java
@Entity
public class Order {

    @Id
    private Long id;

    @ManyToOne
    private User user;
}
```

---

## Tables

### USER

| id | name  |
| -- | ----- |
| 1  | Niraj |
| 2  | John  |

### ORDERS

| id  | user_id |
| --- | ------- |
| 101 | 1       |
| 102 | 1       |
| 103 | 1       |
| 104 | 2       |

Foreign Key:

```sql
ORDERS.user_id
         |
         v
USER.id
```

Notice:

```text
Many Orders
share same user_id
```

That's why:

```text
ManyToOne
```

---

# 3. OneToMany

Same relationship viewed from the opposite side.

---

## Entity

```java
@Entity
public class User {

    @Id
    private Long id;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;
}
```

```java
@Entity
public class Order {

    @ManyToOne
    private User user;
}
```

---

## SQL Tables

Exactly the same tables:

### USER

| id | name  |
| -- | ----- |
| 1  | Niraj |

### ORDERS

| id  | user_id |
| --- | ------- |
| 101 | 1       |
| 102 | 1       |
| 103 | 1       |

---

### Important Interview Point

Many developers think:

```java
@OneToMany
```

creates a new column.

It usually does NOT.

The actual foreign key sits on:

```java
@ManyToOne
```

side.

Because SQL naturally stores:

```text
Many records
pointing to One record
```

using a foreign key.

---

# 4. ManyToMany

Example:

```text
Users
  |
Roles

One user can have many roles.
One role can belong to many users.
```

Example:

```text
Niraj -> ADMIN
Niraj -> USER

John -> USER
```

---

## Entity

```java
@Entity
public class User {

    @ManyToMany
    private List<Role> roles;
}
```

```java
@Entity
public class Role {

    @ManyToMany(mappedBy = "roles")
    private List<User> users;
}
```

---

## Tables

### USER

| id | name  |
| -- | ----- |
| 1  | Niraj |
| 2  | John  |

### ROLE

| id | role  |
| -- | ----- |
| 1  | ADMIN |
| 2  | USER  |

---

### JOIN TABLE

### USER_ROLE

| user_id | role_id |
| ------- | ------- |
| 1       | 1       |
| 1       | 2       |
| 2       | 2       |

Meaning:

```text
Niraj -> ADMIN
Niraj -> USER
John -> USER
```

---

## Why Join Table?

If we put:

```sql
role_id
```

inside USER table:

```text
One user could have only one role
```

Problem.

If we put:

```sql
user_id
```

inside ROLE table:

```text
One role could have only one user
```

Problem.

Therefore:

```text
ManyToMany
=
Third Join Table
```

---

# Visual Summary

## OneToOne

```text
USER
 |
ADDRESS
```

```sql
USER
-----
id
address_id
```

---

## ManyToOne

```text
Many Orders
      |
      v
One User
```

```sql
ORDERS
------
id
user_id
```

---

## OneToMany

```text
One User
      |
Many Orders
```

Same SQL:

```sql
ORDERS
------
id
user_id
```

---

## ManyToMany

```text
Users
  |
Roles
```

```sql
USER

ROLE

USER_ROLE
---------
user_id
role_id
```

---

# Interview Gold Question

### Why do we usually write both sides?

```java
@OneToMany(mappedBy = "user")
private List<Order> orders;
```

and

```java
@ManyToOne
private User user;
```

Because:

```text
User -> Orders
```

and

```text
Order -> User
```

navigation becomes possible from both directions.

This is called:

```text
Bidirectional Mapping
```

---

# Real Microservice Example

In your User Service:

```java
@Entity
public class User {

    @OneToMany(mappedBy = "user")
    private List<Address> addresses;
}
```

SQL:

### USER

| id | username |
| -- | -------- |

### ADDRESS

| id | city | user_id |
| -- | ---- | ------- |

Notice:

```text
Foreign Key always lives on the Many side.
```

This is one of the most commonly asked JPA interview concepts.
