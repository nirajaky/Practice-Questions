`@Qualifier` is used when **multiple beans of the same type exist** and Spring doesn't know which one to inject.

---

## Problem Without `@Qualifier`

Suppose:

```java
public interface PaymentService {
    void pay();
}
```

Implementation 1:

```java
@Service
public class UpiPaymentService implements PaymentService {

    @Override
    public void pay() {
        System.out.println("UPI Payment");
    }
}
```

Implementation 2:

```java
@Service
public class CardPaymentService implements PaymentService {

    @Override
    public void pay() {
        System.out.println("Card Payment");
    }
}
```

Now:

```java
@Service
public class OrderService {

    @Autowired
    private PaymentService paymentService;
}
```

Spring finds:

```text
UpiPaymentService
CardPaymentService
```

Both implement `PaymentService`.

Spring throws:

```text
NoUniqueBeanDefinitionException
```

because it doesn't know which bean to inject.

---

## Solution Using `@Qualifier`

```java
@Service
public class OrderService {

    @Autowired
    @Qualifier("upiPaymentService")
    private PaymentService paymentService;
}
```

Spring injects:

```java
UpiPaymentService
```

---

## Constructor Injection (Preferred)

```java
@Service
public class OrderService {

    private final PaymentService paymentService;

    public OrderService(
            @Qualifier("upiPaymentService")
            PaymentService paymentService) {

        this.paymentService = paymentService;
    }
}
```

---

## Custom Bean Name

```java
@Service("upi")
public class UpiPaymentService
        implements PaymentService {
}
```

Inject:

```java
@Autowired
@Qualifier("upi")
private PaymentService paymentService;
```

---

## `@Primary` vs `@Qualifier`

### Using `@Primary`

```java
@Service
@Primary
public class UpiPaymentService
        implements PaymentService {
}
```

Now:

```java
@Autowired
private PaymentService paymentService;
```

Spring automatically injects `UpiPaymentService`.

---

### Using `@Qualifier`

```java
@Autowired
@Qualifier("cardPaymentService")
private PaymentService paymentService;
```

Injects the specific bean regardless of `@Primary`.

---

## Which Takes Priority?

```text
@Qualifier  >  @Primary
```

If both are present:

```java
@Autowired
@Qualifier("cardPaymentService")
private PaymentService paymentService;
```

Spring injects `CardPaymentService` even if another bean is marked `@Primary`.

---

## Common Interview Question

### Can `@Qualifier` be used without `@Autowired`?

Yes, with constructor injection:

```java
public OrderService(
        @Qualifier("upiPaymentService")
        PaymentService paymentService) {
}
```

No need for `@Autowired` if there's a single constructor (Spring 4.3+).

---

## Real-world Spring Boot Examples

* Multiple `DataSource` beans
* Multiple `RestTemplate` beans
* Multiple `KafkaTemplate` beans
* Multiple implementations of a service interface
* Multiple cache managers

---

### Interview Answer (30 seconds)

> `@Qualifier` is used to resolve ambiguity when multiple beans of the same type exist in the Spring container. By default Spring injects by type, but if more than one matching bean is found, it throws `NoUniqueBeanDefinitionException`. `@Qualifier` allows us to specify exactly which bean should be injected. It overrides `@Primary` if both are present.
