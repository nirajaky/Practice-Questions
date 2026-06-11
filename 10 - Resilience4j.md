# What is Resilience4j?

**Resilience4j** is a fault-tolerance library that helps your application handle failures gracefully when calling external systems.

Example:

```text
User Service
    |
    ---> Payment Service
    |
    ---> Notification Service
```

If Payment Service becomes slow or unavailable, User Service should not crash or keep waiting forever.

Resilience4j provides patterns to handle such situations.

---

# Why Do We Need It?

Without Resilience4j:

```java
paymentService.makePayment();
```

If Payment Service is down:

* Request fails
* Threads get blocked
* Application becomes slow
* Can lead to cascading failures

---

# Main Features of Resilience4j

## 1. Circuit Breaker (Most Important)

### Problem

Payment service is down.

Without Circuit Breaker:

```text
Request 1 -> Fail
Request 2 -> Fail
Request 3 -> Fail
...
```

Application keeps hitting a failed service.

---

### Solution

Circuit Breaker stops calls temporarily.

States:

```text
CLOSED
   ↓
Failures exceed threshold
   ↓
OPEN
   ↓
Wait duration
   ↓
HALF_OPEN
```

### CLOSED

Everything works normally.

```text
User Service
   |
   ---> Payment Service
```

---

### OPEN

Too many failures.

```text
User Service
   X
Payment Service
```

Requests fail immediately without calling Payment Service.

---

### HALF_OPEN

After waiting:

```text
Try few requests
```

If successful:

```text
HALF_OPEN -> CLOSED
```

Otherwise:

```text
HALF_OPEN -> OPEN
```

---

### Example

```java
@CircuitBreaker(
    name = "paymentService",
    fallbackMethod = "paymentFallback"
)
public String makePayment() {
    return restTemplate.getForObject(url, String.class);
}
```

Fallback:

```java
public String paymentFallback(Exception ex) {
    return "Payment Service Unavailable";
}
```

---

# 2. Retry

### Problem

Temporary network glitch.

```text
Call 1 -> Fail
Call 2 -> Success
```

Without retry:

```text
Request fails unnecessarily
```

---

### Example

```java
@Retry(name = "paymentRetry")
public String makePayment() {
    return paymentClient.call();
}
```

Configuration:

```yaml
resilience4j:
  retry:
    instances:
      paymentRetry:
        max-attempts: 3
        wait-duration: 2s
```

Spring automatically retries.

---

# 3. Rate Limiter

Limit number of requests.

Example:

Allow only:

```text
100 requests/minute
```

Excess requests are rejected.

---

### Example

```java
@RateLimiter(name = "paymentRateLimiter")
public String makePayment() {
    return paymentClient.call();
}
```

Useful for:

* Public APIs
* Third-party APIs
* Prevent abuse

---

# 4. Bulkhead

### Problem

Payment Service becomes slow.

All threads get occupied.

Then:

```text
User API
Order API
Notification API
```

all start failing.

---

### Solution

Separate thread pools.

```text
Payment Threads = 10

Notification Threads = 10

Order Threads = 10
```

Failure in one area doesn't consume all resources.

---

### Example

```java
@Bulkhead(name = "paymentBulkhead")
public String makePayment() {
    return paymentClient.call();
}
```

Think of a ship's bulkhead compartments:
one flooded compartment doesn't sink the entire ship.

---

# 5. Time Limiter

### Problem

External service takes 30 seconds.

Users shouldn't wait that long.

---

### Example

```java
@TimeLimiter(name = "paymentTimeout")
public CompletableFuture<String> makePayment() {
}
```

Configuration:

```yaml
resilience4j:
  timelimiter:
    instances:
      paymentTimeout:
        timeout-duration: 3s
```

After 3 seconds:

```text
Timeout Exception
```

---

# Common Interview Scenario

Suppose:

```text
Order Service
      |
      ---> Payment Service
```

Payment Service is unstable.

What would you apply?

Answer:

```text
Circuit Breaker
Retry
Time Limiter
Fallback Method
```

Combination:

```java
@Retry
@CircuitBreaker
@TimeLimiter
```

---

# Maven Dependency

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>
        resilience4j-spring-boot3
    </artifactId>
</dependency>
```

---

# Sample Configuration

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        failure-rate-threshold: 50
        minimum-number-of-calls: 10
        wait-duration-in-open-state: 10s
        sliding-window-size: 20
```

Meaning:

* Monitor last 20 calls
* If more than 50% fail
* Open circuit
* Wait 10 seconds
* Try again

---

# Frequently Asked Interview Questions

### Difference between Retry and Circuit Breaker?

**Retry**

```text
Fail
↓
Try Again
↓
Try Again
```

Used for temporary failures.

---

**Circuit Breaker**

```text
Fail many times
↓
Stop calling service
```

Used for persistent failures.

---

### Can Retry and Circuit Breaker be used together?

Yes.

Typical flow:

```text
Call
 ↓
Retry 3 times
 ↓
Still failing?
 ↓
Circuit Breaker records failure
 ↓
Eventually opens circuit
```

---

### What is Fallback Method?

A backup method executed when the main call fails.

```java
public String paymentFallback(Exception ex) {
    return "Please try later";
}
```

Instead of throwing an exception to users.

---

### Interview Answer (1 minute)

> Resilience4j is a lightweight fault-tolerance library used in Spring Boot microservices. It helps make applications resilient to failures using patterns such as Circuit Breaker, Retry, Rate Limiter, Bulkhead, and Time Limiter. Circuit Breaker prevents repeated calls to failing services, Retry handles temporary failures, Time Limiter prevents long waits, Rate Limiter controls traffic, and Bulkhead isolates resources. It is commonly used with RestTemplate, WebClient, Feign Clients, and external API integrations.
