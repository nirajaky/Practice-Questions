Let's take a realistic microservice example.

```text
User Service
     |
     |----> Payment Service
     |
     |----> Notification Service
```

Suppose a user places an order.

`UserService` calls `PaymentService` to process payment.

---

# Without Circuit Breaker

```java
public String placeOrder() {
    return paymentClient.makePayment();
}
```

### Scenario

Payment Service goes down at 10:00 AM.

Request 1:

```text
User Service ---> Payment Service
                     ↓
                  Timeout after 30 sec
```

Request 2:

```text
User Service ---> Payment Service
                     ↓
                  Timeout after 30 sec
```

Request 3:

```text
User Service ---> Payment Service
                     ↓
                  Timeout after 30 sec
```

Hundreds of users come.

Every request:

* waits 30 seconds
* consumes a thread
* increases CPU usage
* eventually User Service itself becomes slow

This is called a **cascading failure**.

---

# With Circuit Breaker

Circuit Breaker monitors success/failure rates.

Initially:

```text
State = CLOSED
```

Meaning:

```text
User Service ---> Payment Service
```

Calls are allowed.

---

## Step 1: Payment Service Starts Failing

Requests:

```text
Call 1 -> Fail
Call 2 -> Fail
Call 3 -> Fail
Call 4 -> Fail
Call 5 -> Fail
```

Suppose threshold is:

```yaml
failure-rate-threshold: 50
minimum-number-of-calls: 10
```

After enough failures:

```text
Circuit State = OPEN
```

---

## Step 2: Circuit Open

Now:

```text
User Service -X-> Payment Service
```

No actual HTTP call happens.

Instead:

```java
fallbackMethod()
```

runs immediately.

User gets response in milliseconds instead of waiting 30 seconds.

---

## Step 3: Half Open

After waiting:

```yaml
wait-duration-in-open-state: 10s
```

Resilience4j says:

> Let me test if Payment Service recovered.

State becomes:

```text
HALF_OPEN
```

Allows a few requests.

```text
Test Call 1
Test Call 2
Test Call 3
```

---

### If Successful

```text
HALF_OPEN
      ↓
CLOSED
```

Normal traffic resumes.

---

### If Still Failing

```text
HALF_OPEN
      ↓
OPEN
```

Circuit opens again.

---

# Actual Spring Boot Code

## Dependency

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
</dependency>
```

---

## Payment Client

Imagine:

```java
@Service
public class PaymentClient {

    @CircuitBreaker(
        name = "paymentService",
        fallbackMethod = "paymentFallback"
    )
    public String makePayment() {

        RestTemplate restTemplate =
                new RestTemplate();

        return restTemplate.getForObject(
                "http://payment-service/pay",
                String.class);
    }

    public String paymentFallback(Exception ex) {

        return "Payment Service is currently unavailable";
    }
}
```

---

## User Service

```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final PaymentClient paymentClient;

    public String placeOrder() {

        return paymentClient.makePayment();
    }
}
```

---

# Configuration

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:

        sliding-window-size: 10

        minimum-number-of-calls: 5

        failure-rate-threshold: 50

        wait-duration-in-open-state: 10s

        permitted-number-of-calls-in-half-open-state: 3
```

---

# Meaning of Configuration

### sliding-window-size

```yaml
sliding-window-size: 10
```

Look at last 10 requests.

Example:

```text
Success
Success
Fail
Fail
Fail
Success
Fail
Fail
Success
Fail
```

Total:

```text
10 calls
```

---

### failure-rate-threshold

```yaml
failure-rate-threshold: 50
```

If more than 50% fail:

```text
Circuit OPEN
```

Example:

```text
10 calls
6 failed
```

Failure rate:

```text
60%
```

Circuit opens.

---

### wait-duration-in-open-state

```yaml
wait-duration-in-open-state: 10s
```

After opening:

```text
Wait 10 seconds
```

Then try again.

---

### permitted-number-of-calls-in-half-open-state

```yaml
3
```

During HALF_OPEN:

```text
Allow only 3 test calls
```

If all succeed:

```text
HALF_OPEN -> CLOSED
```

Otherwise:

```text
HALF_OPEN -> OPEN
```

---

# What User Sees

Without Circuit Breaker:

```text
Place Order
↓
Wait 30 sec
↓
500 Error
```

With Circuit Breaker:

```text
Place Order
↓
Fallback Response
↓
"Payment service temporarily unavailable"
```

Response comes almost instantly.

---

# Most Common Interview Question

### Does Circuit Breaker stop the first failed call?

No.

Initially:

```text
Circuit = CLOSED
```

Failures must occur first.

Only after the configured threshold is crossed does it move to OPEN state.

---

### Why not use only Retry?

Suppose Payment Service is completely down.

Retry:

```text
Call
 ↓
Retry
 ↓
Retry
 ↓
Retry
```

Still fails.

Thousands of users doing retries will overload the system.

Circuit Breaker solves this by saying:

```text
Service is down.
Stop calling it for some time.
```

That's why in real projects we often use:

```java
@Retry(name = "paymentRetry")
@CircuitBreaker(
    name = "paymentService",
    fallbackMethod = "paymentFallback"
)
```

Retry handles temporary glitches, while Circuit Breaker protects the system from persistent failures.
