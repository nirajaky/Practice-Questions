These are extremely important topics for Spring Boot, JPA, and database interviews.

# What is a Transaction?

A transaction is a group of database operations that must be treated as **one unit of work**.

Example:

```java
@Transactional
public void transferMoney() {

    debit(fromAccount, 1000);

    credit(toAccount, 1000);
}
```

Either:

```text
Debit Success
Credit Success
```

OR

```text
Nothing Happens
```

There should never be:

```text
Debit Success
Credit Failed
```

because money would disappear.

This is where ACID comes in.

---

# ACID Properties

## A = Atomicity

Atomic means:

```text
All or Nothing
```

Example:

```java
@Transactional
public void placeOrder() {

    saveOrder();

    savePayment();

    saveInvoice();
}
```

If:

```text
saveOrder()      Success
savePayment()    Success
saveInvoice()    Failed
```

Then:

```text
saveOrder()      Rollback
savePayment()    Rollback
```

Database returns to original state.

---

## C = Consistency

Database must always remain valid.

Example:

Before transaction:

```text
Account A = 5000
Account B = 3000
Total = 8000
```

After transfer:

```text
Account A = 4000
Account B = 4000
Total = 8000
```

Consistency preserved.

Invalid state:

```text
Account A = 4000
Account B = 3000
Total = 7000
```

Consistency violated.

---

## I = Isolation

Multiple transactions should not interfere with each other.

Example:

User A:

```sql
UPDATE account
SET balance = balance - 1000
```

User B:

```sql
UPDATE account
SET balance = balance - 500
```

Both running simultaneously.

Database isolation ensures data doesn't become corrupted.

Isolation levels:

```text
READ_UNCOMMITTED
READ_COMMITTED
REPEATABLE_READ
SERIALIZABLE
```

Interviewers often ask these separately.

---

## D = Durability

Once transaction commits:

```sql
COMMIT;
```

data must survive:

```text
Application restart
Server restart
Power failure
```

Database writes committed data to persistent storage.

---

# What is Rollback?

Rollback means:

```text
Undo all changes
made in current transaction
```

Example:

```java
@Transactional
public void createUser() {

    userRepository.save(user);

    throw new RuntimeException();
}
```

Flow:

```text
Insert User
      |
RuntimeException
      |
Rollback
```

Result:

```text
No User Saved
```

---

# What is Commit?

Commit means:

```text
Persist all changes permanently
```

Example:

```java
@Transactional
public void createUser() {

    userRepository.save(user);

    // No Exception
}
```

Flow:

```text
Insert User
      |
Commit
      |
Data Saved
```

---

# Visual Example

Initial DB:

```text
Users Table

ID  Name
1   John
```

Transaction starts:

```java
@Transactional
public void addUser() {

    save(Niraj);

    throw new RuntimeException();
}
```

Temporary state:

```text
Users Table

1 John
2 Niraj
```

Exception occurs:

```text
Rollback
```

Final state:

```text
Users Table

1 John
```

Niraj removed.

---

# How Spring Handles Transactions

When Spring sees:

```java
@Transactional
public void createUser() {
}
```

it creates an AOP proxy.

Flow:

```text
Controller
      |
Transaction Proxy
      |
BEGIN TRANSACTION
      |
Service Method
      |
COMMIT / ROLLBACK
```

---

# Internal Flow

```java
@Transactional
public void createUser() {

    userRepository.save(user);

    userRepository.save(profile);
}
```

Spring internally does:

```text
BEGIN

INSERT USER

INSERT PROFILE

COMMIT
```

If exception:

```text
BEGIN

INSERT USER

INSERT PROFILE

EXCEPTION

ROLLBACK
```

---

# Runtime vs Checked Exception

Default Spring behavior:

## Runtime Exception

```java
throw new RuntimeException();
```

Result:

```text
Rollback
```

---

## Checked Exception

```java
throw new IOException();
```

Result:

```text
Commit
```

by default.

Many developers get this wrong in interviews.

---

# Rollback Checked Exception

```java
@Transactional(
    rollbackFor = Exception.class
)
public void createUser() throws IOException {

    saveUser();

    throw new IOException();
}
```

Now:

```text
Rollback
```

---

# Common Interview Question

## Why Runtime Exceptions Rollback Automatically?

Spring assumes:

```text
Runtime Exception
=
Programming error
=
Transaction should fail
```

Examples:

```java
NullPointerException
IllegalArgumentException
IllegalStateException
```

These indicate the operation should not continue.

---

# Real Project Example

User Registration:

```java
@Transactional
public void registerUser() {

    saveUser();

    saveAddress();

    saveRoles();

    sendEvent();
}
```

Suppose:

```text
saveUser()      Success
saveAddress()   Success
saveRoles()     Success
sendEvent()     Fail
```

Spring performs:

```text
Rollback
```

Result:

```text
User Not Saved
Address Not Saved
Roles Not Saved
```

Database remains consistent.

---

# Interview Answer

### What is ACID?

ACID properties guarantee reliable database transactions:

* **Atomicity** → All or nothing.
* **Consistency** → Database remains valid before and after transaction.
* **Isolation** → Concurrent transactions do not interfere.
* **Durability** → Committed data survives failures.

### What is Rollback?

Rollback undoes all database changes made within the current transaction when the transaction fails.

### When does Spring Rollback?

By default:

```text
RuntimeException → Rollback
Checked Exception → No Rollback
```

unless configured using:

```java
@Transactional(rollbackFor = Exception.class)
```
