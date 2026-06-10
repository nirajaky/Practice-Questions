SQL JOINs are another must-know topic for 5–6 years Java/Spring Boot interviews.

# Why Do We Need JOINs?

Suppose we have:

### USER

| id | name  |
| -- | ----- |
| 1  | Niraj |
| 2  | John  |

### ORDERS

| id  | product | user_id |
| --- | ------- | ------- |
| 101 | Laptop  | 1       |
| 102 | Mobile  | 1       |
| 103 | Watch   | 2       |

Question:

> Show order details along with user name.

Data is in two tables.

That's where JOIN comes in.

---

# 1. INNER JOIN

Returns only matching records from both tables.

```sql
SELECT
    u.name,
    o.product
FROM users u
INNER JOIN orders o
ON u.id = o.user_id;
```

Result:

| name  | product |
| ----- | ------- |
| Niraj | Laptop  |
| Niraj | Mobile  |
| John  | Watch   |

---

## Visual

```text
USER                ORDERS

1 Niraj      <-->   user_id=1
2 John       <-->   user_id=2
```

Only matched records returned.

---

# Example with Unmatched Data

### USER

| id | name  |
| -- | ----- |
| 1  | Niraj |
| 2  | John  |
| 3  | Amit  |

### ORDERS

| id  | product | user_id |
| --- | ------- | ------- |
| 101 | Laptop  | 1       |
| 102 | Mobile  | 1       |

Now:

```sql
SELECT *
FROM users u
INNER JOIN orders o
ON u.id = o.user_id;
```

Result:

| name  | product |
| ----- | ------- |
| Niraj | Laptop  |
| Niraj | Mobile  |

John and Amit disappear because they have no orders.

---

# 2. LEFT JOIN

Returns:

```text
All rows from Left Table
+
Matching rows from Right Table
```

Query:

```sql
SELECT
    u.name,
    o.product
FROM users u
LEFT JOIN orders o
ON u.id = o.user_id;
```

Result:

| name  | product |
| ----- | ------- |
| Niraj | Laptop  |
| Niraj | Mobile  |
| John  | NULL    |
| Amit  | NULL    |

---

## Visual

```text
LEFT TABLE (USER)

Keep Everything

USER  --------> ORDERS
```

Even if no match exists, user is returned.

---

# 3. RIGHT JOIN

Returns:

```text
All rows from Right Table
+
Matching rows from Left Table
```

Query:

```sql
SELECT
    u.name,
    o.product
FROM users u
RIGHT JOIN orders o
ON u.id = o.user_id;
```

Keeps all rows from ORDERS.

Less commonly used.

---

# 4. FULL OUTER JOIN

Returns:

```text
All rows from Left Table
+
All rows from Right Table
```

Matched where possible.

```sql
SELECT *
FROM users u
FULL OUTER JOIN orders o
ON u.id = o.user_id;
```

Result contains everything from both tables.

(MySQL doesn't support FULL OUTER JOIN directly.)

---

# Visual Summary

## INNER JOIN

```text
USER        ORDERS

   (Overlap)

Only common records
```

---

## LEFT JOIN

```text
USER + Overlap
```

Keep all users.

---

## RIGHT JOIN

```text
Overlap + ORDERS
```

Keep all orders.

---

## FULL JOIN

```text
USER + OVERLAP + ORDERS
```

Keep everything.

---

# Self Join

A table joins itself.

Example:

### EMPLOYEE

| id | name  | manager_id |
| -- | ----- | ---------- |
| 1  | CEO   | NULL       |
| 2  | Niraj | 1          |
| 3  | John  | 1          |

Query:

```sql
SELECT
    e.name,
    m.name AS manager
FROM employee e
LEFT JOIN employee m
ON e.manager_id = m.id;
```

Result:

| Employee | Manager |
| -------- | ------- |
| CEO      | NULL    |
| Niraj    | CEO     |
| John     | CEO     |

---

# Cross Join

Every row combines with every row.

### USER

| id |
| -- |
| 1  |
| 2  |

### ROLE

| id |
| -- |
| 10 |
| 20 |

```sql
SELECT *
FROM users
CROSS JOIN roles;
```

Result:

| user | role |
| ---- | ---- |
| 1    | 10   |
| 1    | 20   |
| 2    | 10   |
| 2    | 20   |

Total rows:

```text
users × roles
```

2 × 2 = 4

Rarely used.

---

# How Hibernate Uses JOINs

Entity:

```java
@Entity
public class Order {

    @ManyToOne
    private User user;
}
```

JPQL:

```java
@Query("""
SELECT o
FROM Order o
JOIN FETCH o.user
""")
```

Hibernate generates:

```sql
SELECT *
FROM orders o
INNER JOIN users u
ON o.user_id = u.id
```

This is how we solve Lazy Loading and N+1 problems.

---

# JOIN vs JOIN FETCH (Interview Question)

Normal JPQL:

```java
SELECT o
FROM Order o
JOIN o.user
```

Used for filtering.

Does not necessarily load the relation.

---

Fetch Join:

```java
SELECT o
FROM Order o
JOIN FETCH o.user
```

Loads:

```text
Order
+
User
```

in a single query.

Very common optimization in Spring Boot projects.

---

# Most Common Interview Queries

### Users with Orders

```sql
SELECT *
FROM users u
INNER JOIN orders o
ON u.id = o.user_id;
```

---

### Users Without Orders

```sql
SELECT *
FROM users u
LEFT JOIN orders o
ON u.id = o.user_id
WHERE o.id IS NULL;
```

---

### Count Orders Per User

```sql
SELECT
    u.name,
    COUNT(o.id)
FROM users u
LEFT JOIN orders o
ON u.id = o.user_id
GROUP BY u.name;
```

---

# Interview Answer

### INNER JOIN

Returns only matching records from both tables.

### LEFT JOIN

Returns all records from the left table and matching records from the right table.

### RIGHT JOIN

Returns all records from the right table and matching records from the left table.

### FULL OUTER JOIN

Returns all records from both tables.

### JOIN FETCH

A Hibernate/JPA optimization that loads related entities in a single query and helps avoid the N+1 problem.
