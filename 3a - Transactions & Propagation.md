# Spring Transactions & Propagation – Complete Notes

## 1. What is a Transaction?

A **transaction** is a logical unit of work that must be completed entirely or not at all.

Example:

```java
withdraw(1000);
deposit(1000);
```

Both operations must succeed together.

If withdrawal succeeds but deposit fails:

```text
Account A = -1000
Account B = +0
```

Data becomes inconsistent.

A transaction ensures:

```text
Either:
✓ Withdraw
✓ Deposit

OR

✗ Withdraw
✗ Deposit
```

---

# 2. ACID Properties

### A – Atomicity

All operations succeed or all fail.

```text
Debit ₹1000
Credit ₹1000

Success -> Both commit
Failure -> Both rollback
```

---

### C – Consistency

Database remains valid before and after transaction.

```text
Total Money Before = ₹2000
Total Money After  = ₹2000
```

---

### I – Isolation

Multiple transactions should not interfere.

```text
User A updates account
User B reads account

Database prevents conflicts.
```

---

### D – Durability

Once committed:

```text
COMMIT
```

Data survives JVM crash or server restart.

---

# 3. Spring Transaction Management

Enable transaction:

```java
@Transactional
public void transferMoney() {
    debit();
    credit();
}
```

Spring automatically:

```text
BEGIN TRANSACTION

debit()
credit()

COMMIT
```

If exception occurs:

```text
BEGIN TRANSACTION

debit()

Exception

ROLLBACK
```

---

# 4. Transaction Lifecycle

```text
Start Transaction
       |
       v
Execute Queries
       |
       v
Exception?
   /       \
 Yes       No
  |         |
Rollback   Commit
```

---

# 5. @Transactional

```java
@Transactional
public void saveUser() {
}
```

Default values:

```java
@Transactional(
    propagation = Propagation.REQUIRED,
    isolation = Isolation.DEFAULT,
    readOnly = false
)
```

---

# 6. Commit

```java
@Transactional
public void save() {
    repository.save(user);
}
```

Success:

```text
Transaction Started
Save User
Commit
```

Data saved permanently.

---

# 7. Rollback

```java
@Transactional
public void save() {

    repository.save(user);

    throw new RuntimeException();
}
```

Result:

```text
Rollback
```

Nothing saved.

---

# 8. Checked vs Unchecked Exceptions

### Unchecked

```java
RuntimeException
NullPointerException
ArithmeticException
```

Rollback by default.

```java
throw new RuntimeException();
```

Spring rolls back.

---

### Checked

```java
IOException
SQLException
```

By default Spring does NOT rollback.

```java
throw new IOException();
```

Transaction commits unless configured.

---

### Force Rollback

```java
@Transactional(
    rollbackFor = Exception.class
)
```

Now checked exceptions also rollback.

---

# 9. Propagation

Propagation determines:

> What should happen if a transactional method calls another transactional method?

---

## REQUIRED (Default)

```java
@Transactional(
    propagation = Propagation.REQUIRED
)
```

Rule:

```text
Existing transaction?
      |
   Yes -> Join
   No  -> Create New
```

Example:

```java
@Transactional
public void placeOrder() {
    payment();
}
```

```java
@Transactional
public void payment() {
}
```

Flow:

```text
T1 Started

placeOrder()

payment()
joins T1

Commit T1
```

Most commonly used.

---

## REQUIRES_NEW

```java
@Transactional(
    propagation = Propagation.REQUIRES_NEW
)
```

Rule:

```text
Always create new transaction
```

Example:

```java
@Transactional
public void placeOrder() {

    saveOrder();

    saveAudit();
}
```

```java
@Transactional(
    propagation = Propagation.REQUIRES_NEW
)
public void saveAudit() {
}
```

Flow:

```text
T1 Started

saveOrder()

Suspend T1

T2 Started

saveAudit()

Commit T2

Resume T1
```

Use Cases:

* Audit Logs
* Notifications
* Tracking

---

## SUPPORTS

```java
@Transactional(
    propagation = Propagation.SUPPORTS
)
```

Rule:

```text
If transaction exists -> Join

Else -> Execute normally
```

Example:

```java
getProduct()
```

Can run:

```text
With Transaction
OR
Without Transaction
```

Use Case:

Read-only methods.

---

## NOT_SUPPORTED

```java
@Transactional(
    propagation = Propagation.NOT_SUPPORTED
)
```

Rule:

```text
Suspend current transaction
Execute without transaction
```

Example:

```java
generateReport()
```

Flow:

```text
T1 Started

Suspend T1

generateReport()

No Transaction

Resume T1
```

Use Cases:

* Reports
* File Generation
* Long Running Tasks

---

## MANDATORY

```java
@Transactional(
    propagation = Propagation.MANDATORY
)
```

Rule:

```text
Transaction must already exist
```

Example:

```java
@Transactional
public void transferMoney() {

    debitAccount();
}
```

```java
@Transactional(
    propagation = Propagation.MANDATORY
)
public void debitAccount() {
}
```

Called without transaction:

```text
Exception
```

Use Cases:

* Debit
* Credit
* Inventory Update

Critical operations.

---

## NEVER

```java
@Transactional(
    propagation = Propagation.NEVER
)
```

Rule:

```text
Must NOT run in transaction
```

Example:

```java
@Transactional
public void placeOrder() {

    healthCheck();
}
```

```java
@Transactional(
    propagation = Propagation.NEVER
)
public void healthCheck() {
}
```

Result:

```text
Exception
```

because transaction exists.

---

## NESTED

```java
@Transactional(
    propagation = Propagation.NESTED
)
```

Rule:

```text
Create Savepoint
```

Example:

```java
@Transactional
public void placeOrder() {

    saveOrder();

    reserveInventory();

    sendEmail();
}
```

Flow:

```text
T1 Started

saveOrder()

Savepoint S1

reserveInventory()
fails

Rollback to S1

sendEmail()

Commit T1
```

Result:

```text
Order Saved       ✓

Inventory         ✗

Email Sent        ✓
```

---

# 10. Propagation Comparison

| Propagation   | Existing Tx | No Existing Tx |
| ------------- | ----------- | -------------- |
| REQUIRED      | Join        | Create New     |
| REQUIRES_NEW  | New Tx      | New Tx         |
| SUPPORTS      | Join        | No Tx          |
| NOT_SUPPORTED | Suspend     | No Tx          |
| MANDATORY     | Join        | Exception      |
| NEVER         | Exception   | No Tx          |
| NESTED        | Savepoint   | New Tx         |

---

# 11. Isolation Levels

Controls concurrent transactions.

---

## READ_UNCOMMITTED

Can read uncommitted data.

```text
Dirty Reads Possible
```

Rarely used.

---

## READ_COMMITTED

Can read only committed data.

Most databases default.

```java
@Transactional(
 isolation = Isolation.READ_COMMITTED
)
```

---

## REPEATABLE_READ

Same query returns same data.

Prevents:

```text
Dirty Read
Non-repeatable Read
```

---

## SERIALIZABLE

Highest isolation.

```text
Most Safe
Most Slow
```

Transactions execute almost sequentially.

---

# 12. Read Only Transaction

```java
@Transactional(readOnly = true)
public User getUser() {
}
```

Benefits:

* Better performance
* Prevents accidental updates

Used for:

```text
SELECT operations
```

---

# 13. Self Invocation Problem (Interview Favorite)

```java
@Service
public class OrderService {

    @Transactional
    public void placeOrder() {

        saveAudit();
    }

    @Transactional(
      propagation = Propagation.REQUIRES_NEW
    )
    public void saveAudit() {
    }
}
```

Problem:

```text
saveAudit()
called from same class
```

Spring proxy is bypassed.

Result:

```text
REQUIRES_NEW ignored
```

Solution:

```java
@Autowired
AuditService auditService;
```

Move method to another Spring bean.

---

# 14. Frequently Asked Interview Questions

### What is default propagation?

```java
Propagation.REQUIRED
```

---

### What is default isolation?

```java
Isolation.DEFAULT
```

---

### Does Spring rollback checked exceptions?

```text
No
```

Only unchecked exceptions by default.

---

### Difference between REQUIRED and REQUIRES_NEW?

**REQUIRED**

```text
Join existing transaction
```

**REQUIRES_NEW**

```text
Always create new transaction
```

---

### Difference between NOT_SUPPORTED and NEVER?

**NOT_SUPPORTED**

```text
Suspend transaction
Run without transaction
```

**NEVER**

```text
Throw exception if transaction exists
```

---

### What is T1?

```text
T1 = Transaction 1
T2 = Transaction 2
```

Not Thread 1.

---

### What is the most commonly used propagation?

```java
Propagation.REQUIRED
```

In real projects, about 90%+ of transactional methods use `REQUIRED`, with `REQUIRES_NEW` occasionally used for audit logs, notifications, and independent persistence operations.
