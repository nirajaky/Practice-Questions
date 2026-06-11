Bulkhead is another very important **Resilience4j pattern** and interviewers often ask:

> "What problem does Bulkhead solve that Circuit Breaker doesn't?"

---

# What is Bulkhead?

Bulkhead isolates resources so that failure in one part of the system does not affect other parts.

The name comes from ships.

A ship is divided into compartments (bulkheads):

```text
+-------+-------+-------+
| Room1 | Room2 | Room3 |
+-------+-------+-------+
```

If water enters Room1:

```text
+~~~~~~~+-------+-------+
| Water | Room2 | Room3 |
+~~~~~~~+-------+-------+
```

Only Room1 is affected, not the whole ship.

Same concept in software.

---

# Problem Without Bulkhead

Consider:

```text
User Service
      |
      +----> Payment Service
      |
      +----> Notification Service
      |
      +----> Inventory Service
```

Suppose User Service has:

```text
50 Threads
```

---

## Payment Service Becomes Slow

Each payment request takes:

```text
30 seconds
```

Now:

```text
50 Threads
    ↓
All occupied waiting for Payment Service
```

---

### What happens next?

A user calls Notification API:

```text
GET /send-notification
```

No thread available.

Another user calls:

```text
GET /inventory
```

No thread available.

Everything becomes slow even though only Payment Service has a problem.

This is called **resource starvation**.

---

# Solution: Bulkhead

Reserve separate resources.

```text
Payment Threads      = 10

Notification Threads = 10

Inventory Threads    = 10

Others               = 20
```

Now if Payment Service hangs:

```text
Payment Pool
10/10 busy
```

Only Payment requests suffer.

Notification and Inventory continue working.

---

# Visual Example

Without Bulkhead:

```text
50 Threads

Payment Requests
████████████████████████████████████████

Notification Requests
Cannot Execute

Inventory Requests
Cannot Execute
```

---

With Bulkhead:

```text
Payment Pool (10)
██████████

Notification Pool (10)
██

Inventory Pool (10)
███

Remaining Pool (20)
Available
```

System remains healthy.

---

# Resilience4j Bulkhead Types

## 1. Semaphore Bulkhead

Limits concurrent executions.

Example:

```text
Allow only 5 requests at a time
```

If sixth request arrives:

```text
BulkheadFullException
```

---

### Code

```java
@Bulkhead(
    name = "paymentBulkhead",
    fallbackMethod = "fallback"
)
public String makePayment() {

    return paymentClient.call();
}
```

Configuration:

```yaml
resilience4j:
  bulkhead:
    instances:
      paymentBulkhead:
        max-concurrent-calls: 5
```

---

### Scenario

Requests:

```text
Request 1 -> Allowed
Request 2 -> Allowed
Request 3 -> Allowed
Request 4 -> Allowed
Request 5 -> Allowed
Request 6 -> Rejected
```

---

# 2. Thread Pool Bulkhead

Creates a dedicated thread pool.

Most common in microservices.

```java
@Bulkhead(
    name = "paymentBulkhead",
    type = Bulkhead.Type.THREADPOOL
)
```

Configuration:

```yaml
resilience4j:
  thread-pool-bulkhead:
    instances:
      paymentBulkhead:
        core-thread-pool-size: 5
        max-thread-pool-size: 10
        queue-capacity: 20
```

---

# Real Microservice Example

Suppose:

```text
Order Service
```

calls:

```text
Payment Service
Shipping Service
Notification Service
```

---

Without Bulkhead:

```text
100 Tomcat Threads

Payment Service Down

100 Threads Waiting
```

Now:

```text
Shipping API
Notification API
```

also stop responding.

---

With Bulkhead:

```text
Payment Pool      = 20
Shipping Pool     = 20
Notification Pool = 20
Others            = 40
```

Payment service can fail, but Shipping and Notification remain responsive.

---

# Circuit Breaker vs Bulkhead

Many candidates confuse these.

| Circuit Breaker                | Bulkhead                                 |
| ------------------------------ | ---------------------------------------- |
| Stops calls to failing service | Isolates resources                       |
| Prevents repeated failures     | Prevents resource exhaustion             |
| Works based on failure rate    | Works based on concurrency/thread limits |
| OPEN/CLOSED/HALF_OPEN states   | No states                                |
| Protects downstream service    | Protects your application                |

---

# Example Together

```java
@Retry(name = "paymentRetry")
@CircuitBreaker(
    name = "paymentCB",
    fallbackMethod = "fallback"
)
@Bulkhead(name = "paymentBulkhead")
public String makePayment() {
    return paymentClient.call();
}
```

### Flow

```text
Request
   ↓
Bulkhead
   ↓
Retry
   ↓
Circuit Breaker
   ↓
Payment Service
```

* Bulkhead → limits concurrent requests
* Retry → retries temporary failures
* Circuit Breaker → stops calling a persistently failing service

---

# Interview Answer (1 minute)

> Bulkhead is a Resilience4j pattern that isolates resources such as threads or concurrent calls so that failure in one downstream dependency does not impact the entire application. For example, if Payment Service becomes slow and consumes all available threads, Notification and Inventory APIs may also stop responding. By configuring separate thread pools or concurrency limits using Bulkhead, only the Payment-related requests are affected while other functionalities continue working normally. Unlike Circuit Breaker, which stops calls based on failure rate, Bulkhead protects the application from resource exhaustion.
