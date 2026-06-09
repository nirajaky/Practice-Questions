Interview Question Alert

A very common Spring Security interview question is:

"Why does Spring Boot automatically show a login page even when I haven't written any security code?"

Answer:

Because Spring Security auto-configuration detects the security dependency and creates a default SecurityFilterChain that secures all endpoints and enables form-based login.