## JWT, Spring Security and OAuth

A lot of developers confuse **JWT** and **OAuth 2.0**, but they solve different problems.

# Short Answer

### JWT

JWT is a **token format**.

Example:

```text
eyJhbGciOiJIUzI1NiJ9
.
eyJzdWIiOiJuaXJhakBnbWFpbC5jb20ifQ
.
abcxyzsignature
```

JWT answers:

> "How do I represent user information inside a token?"

---

### OAuth 2.0

OAuth is an **authorization framework/protocol**.

OAuth answers:

> "How can one application securely access another application's resources on behalf of a user?"

---

# Example 1: Your Current User Service

Suppose user logs in:

```http
POST /login
```

You verify:

```text
Email
Password
```

Then generate JWT:

```text
JWT Token
```

Client sends:

```http
Authorization: Bearer JWT_TOKEN
```

This is:

```text
Authentication + JWT
```

No OAuth involved.

---

# Example 2: Login with Google

When user clicks:

```text
Continue with Google
```

Flow:

```text
User
  |
  v
Google Login Page
  |
  v
Google verifies credentials
  |
  v
Google sends Access Token
  |
  v
Your Application
```

This is:

```text
OAuth 2.0
```

Google is the Authorization Server.

Your application is the Client.

---

# JWT Structure

A JWT consists of:

```text
Header.Payload.Signature
```

Example:

```text
xxxxx.yyyyy.zzzzz
```

The payload contains claims:

```json
{
  "sub": "niraj@gmail.com",
  "role": "USER",
  "exp": 1712345678
}
```

---

# OAuth Components

OAuth has four major actors:

```text
Resource Owner
    |
    v
User

Client
    |
    v
Your Application

Authorization Server
    |
    v
Google

Resource Server
    |
    v
Google APIs
```

---

# Real-World Comparison

## JWT Authentication

Your company application:

```text
User
 |
 | Login
 |
User Service
 |
 | Generate JWT
 |
Client
```

You control everything.

---

## OAuth Authentication

```text
User
 |
 | Login with Google
 |
Google
 |
 | Access Token
 |
Your Application
```

Google handles authentication.

---

# Can OAuth Use JWT?

Yes.

Very common interview trap:

> Is JWT an alternative to OAuth?

No.

OAuth and JWT can work together.

Example:

```text
OAuth 2.0
     |
     v
Access Token
     |
     v
JWT Format
```

Google, Microsoft, and many identity providers issue OAuth access tokens that are JWTs.

Think:

```text
OAuth = Protocol

JWT = Token Format
```

Like:

```text
HTTP = Protocol

JSON = Data Format
```

Different concepts.

---

# In Your Project

Currently we will implement:

```text
Spring Security
+
JWT
```

Flow:

```text
Login
 |
Verify Password
 |
Generate JWT
 |
Return Token
 |
Client sends JWT
 |
JwtAuthenticationFilter
 |
Authorized
```

No OAuth required.

---

# Interview Answer (5–6 Years Level)

If asked:

**"Difference between JWT and OAuth?"**

You can answer:

> JWT is a token format used to carry claims and user information in a signed token. OAuth 2.0 is an authorization framework that allows a client application to access resources on behalf of a user. OAuth may use JWT as the format of its access token, but they solve different problems. JWT focuses on token representation, while OAuth focuses on delegated authorization.

That's the kind of answer interviewers expect from a mid-to-senior Spring Boot developer.

---

Excellent question. This is where many developers get confused because **Spring Security**, **JWT**, and **OAuth 2.0** often appear together.

## They are different layers

### Spring Security

A security framework inside your application.

It answers:

> "How do I secure my endpoints and decide who can access what?"

Examples:

```java
@PreAuthorize("hasRole('ADMIN')")
```

```java
http.authorizeHttpRequests(...)
```

```java
SecurityFilterChain
```

Spring Security handles:

* Authentication
* Authorization
* Security filters
* Session management
* Password encoding

---

### JWT

A token format.

It answers:

> "How do I carry the user's identity between requests?"

Example:

```text
Authorization: Bearer eyJhbGciOi...
```

Spring Security can use JWT to authenticate users.

---

### OAuth 2.0

An authorization protocol.

It answers:

> "How can another application authenticate a user or access resources on their behalf?"

Examples:

* Login with Google
* Login with GitHub
* Login with Microsoft

---

# Our Project

Current flow:

```text
User
 |
 | email/password
 v
User Service
 |
 | validate credentials
 v
Generate JWT
 |
 v
Client
 |
 | JWT
 v
Spring Security Filter
 |
 v
Controller
```

You own:

* User database
* Login API
* Password verification
* JWT generation

This is often called:

```text
Custom Authentication
using Spring Security + JWT
```

No OAuth involved.

---

# OAuth Flow

Suppose you don't want to store passwords.

User clicks:

```text
Login with Google
```

Flow:

```text
User
 |
 v
Google Login Page
 |
 v
Google verifies user
 |
 v
Google returns token
 |
 v
Your Application
```

Now Google authenticates the user.

Your application trusts Google.

This is OAuth/OIDC.

---

# Real Interview Explanation

### In our project

Spring Security authenticates users by checking:

```text
Email
Password
```

against our database.

Then Spring Security uses JWT to identify the user on future requests.

---

### With OAuth

Spring Security doesn't verify passwords itself.

Instead:

```text
Google
Microsoft
GitHub
Keycloak
Okta
```

verifies the user.

Spring Security simply accepts the token from that provider.

---

# Another Way to Think About It

### Current Project

```text
Spring Security
        +
User DB
        +
JWT
```

You are the Identity Provider.

---

### OAuth Project

```text
Spring Security
        +
Google/Keycloak/Okta
        +
OAuth 2.0
```

Someone else is the Identity Provider.

---

# Can Spring Security Work With OAuth?

Absolutely.

Spring Security supports:

1. Form Login

```text
Username + Password
```

2. JWT Authentication

```text
Bearer Token
```

3. OAuth 2.0 Login

```text
Login with Google
```

4. OpenID Connect (OIDC)

```text
Enterprise SSO
```

5. SAML

```text
Corporate Login
```

Spring Security is the framework that implements all these authentication mechanisms.

---

## Interview Answer

If asked:

> "You are already using Spring Security. Why would you need OAuth?"

A strong answer is:

> Spring Security is the framework that secures my application. In our project, Spring Security authenticates users using credentials stored in our database and issues JWTs. OAuth 2.0 is needed when authentication or authorization is delegated to an external identity provider such as Google, Okta, Keycloak, or Microsoft. Spring Security can implement both approaches, but OAuth solves delegated authorization whereas Spring Security provides the security infrastructure.
