# JWT Security Module — StockFlow

**Spring Boot:** 4.0.2 | **Spring Security:** 7.0.2 | **JJWT:** 0.13.0
**Date:** April 29, 2026

---

## Table of Contents

1. [Why JWT Instead of Traditional Sessions?](#1-why-jwt-instead-of-traditional-sessions)
2. [Authentication Flow](#2-authentication-flow)
3. [Authorization Flow](#3-authorization-flow)
4. [Token Structure](#4-token-structure)
5. [Technical Decisions](#5-technical-decisions)
6. [Problems Found and How They Were Solved](#6-problems-found-and-how-they-were-solved)

---

## 1. Why JWT Instead of Traditional Sessions?

Spring Security is **stateful** by default — it stores the user session in server memory and sends a `JSESSIONID` cookie to the client. On every request, the server must look up that session in memory to identify the user.

JWT changes this model entirely — it is **stateless**:

| Feature | Traditional Session | JWT |
|---|---|---|
| State | Stored on the server | Stored inside the token |
| Scalability | Requires shared sessions across servers | Any server can validate the token |
| Coupling | Requires cookies, browser-dependent | HTTP header, works with any client |
| Database | Queried on every request | Only queried at login |

StockFlow uses JWT because the frontend is decoupled from the backend. The token travels in the `Authorization` header, allowing any client (React, Angular, mobile, Postman) to communicate with the API without relying on cookies.

The stateless policy is configured in `SpringSecurityConfig`:

```java
.sessionManagement(management -> management
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```

---

## 2. Authentication Flow

Login does not require a controller — `JWTAuthenticationFilter` intercepts the request **before** it reaches the `DispatcherServlet`, operating at the HTTP filter level.

```
POST /api/login  { username, password }
        │
        ▼
JWTAuthenticationFilter.attemptAuthentication()
        │
        ├── Tries to read as form-data (obtainUsername / obtainPassword)
        │
        └── If null → reads body as JSON (LoginRequest DTO)
                │
                ▼
        authenticationManager.authenticate(token)
                │
                ▼
        JpaUserDetailsService.loadUserByUsername()
                │
                ├── Finds user in DB by username
                ├── Loads their roles
                └── Returns UserDetails
                        │
                        ▼
                BCryptPasswordEncoder.matches()
                        │
                        ├── true  → successfulAuthentication()
                        │               └── Generates JWT token
                        │               └── Returns 200 + token in body and header
                        │
                        └── false → unsuccessfulAuthentication()
                                        └── Returns 401 + error message
```

The `authenticationManager.authenticate()` call internally invokes `loadUserByUsername()` because the service was registered in `SpringSecurityConfig`:

```java
@Autowired
public void userDetailsService(AuthenticationManagerBuilder build) throws Exception {
    build.userDetailsService(userDetailsService)
         .passwordEncoder(passwordEncoder);
}
```

This is Spring's **Inversion of Control (IoC)** in action — the framework calls your implementation automatically without you invoking it explicitly.

---

## 3. Authorization Flow

Once the client has a token, every subsequent request to a protected endpoint goes through `JWTAuthorizationFilter`:

```
Request to protected endpoint + Authorization: Bearer <token>
        │
        ▼
JWTAuthorizationFilter.doFilterInternal()
        │
        ├── Reads Authorization header
        ├── Validates header starts with "Bearer "
        │
        ▼
JWTService.validate(token)
        │
        ├── Verifies signature with SECRET key
        ├── Checks expiration date
        │
        ▼
JWTService.getRoles(token)
        │
        └── Extracts roles from token payload
                │
                ▼
        SecurityContextHolder.setAuthentication()
                │
                ▼
        @PreAuthorize("hasRole('ADMIN')") → 200 OK or 403 Forbidden
```

The server **never queries the database** during authorization — it only verifies the token signature mathematically. If the signature is valid, it trusts the payload.

Role-based access is applied at the controller level:

```java
@PreAuthorize("hasRole('ADMIN')")
@PostMapping
public ResponseEntity<?> createProduct(...) { ... }

@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@GetMapping
public ResponseEntity<?> getProducts(...) { ... }
```

---

## 4. Token Structure

A JWT token consists of three parts separated by dots:

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIs....qHPmljQHs3onBaz...
│──────────────────│  │─────────────────────│  │──────────────────│
      Header              Payload                  Signature
```

### Header
Contains the signing algorithm:
```json
{
  "alg": "HS256"
}
```

### Payload
Contains the claims — user data embedded in the token:
```json
{
  "sub": "admin",
  "authorities": "[{\"authority\":\"ROLE_ADMIN\"},{\"authority\":\"ROLE_USER\"}]",
  "iat": 1777471038,
  "exp": 1777485038
}
```

### Signature
A hash of Header + Payload signed with the SECRET key:
```
HMACSHA256(base64(header) + "." + base64(payload), SECRET)
```

If anyone modifies the payload, the signature no longer matches and the token is rejected.

---

## 5. Technical Decisions

### 5.1 PathPatternRequestMatcher over AntPathRequestMatcher

Spring Security 7 deprecated `AntPathRequestMatcher` in favor of `PathPatternRequestMatcher`. Using the old class caused compatibility issues with Spring Boot 4.x:

```java
//Old (deprecated in Spring Security 7)
new AntPathRequestMatcher("/api/login", "POST")

//Current
PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/api/login")
```

### 5.2 Filtering FACTOR_PASSWORD from Token

Spring Security 7 introduced a new multi-factor authentication system that automatically adds a `FactorGrantedAuthority` (with an `issuedAt` field of type `java.time.Instant`) to every authenticated user's authorities. This caused Jackson serialization to fail when building the JWT.

The fix was to filter only `SimpleGrantedAuthority` instances before writing the token:

```java
Collection<? extends GrantedAuthority> roles = auth.getAuthorities()
    .stream()
    .filter(a -> a instanceof SimpleGrantedAuthority)
    .collect(Collectors.toList());
```

### 5.3 Response Status and ContentType Before Writing Body

HTTP requires a strict order: status → headers → body. Once `response.getWriter().write()` is called, Tomcat flushes the response and headers can no longer be modified.

```java
//Wrong — status set after write (worked on old Tomcat, fails on Tomcat 11)
response.getWriter().write(new ObjectMapper().writeValueAsString(body));
response.setStatus(200);
response.setContentType("application/json");

//Correct — status set before write
response.setStatus(200);
response.setContentType("application/json");
response.getWriter().write(new ObjectMapper().writeValueAsString(body));
```

### 5.4 SECRET Key Minimum Length

JJWT 0.13.0 strictly enforces RFC 7518 — HMAC-SHA256 keys must be at least 256 bits (32 characters). Shorter keys throw a `WeakKeyException`:

```java
//27 characters = 216 bits — throws WeakKeyException
"Alguna.Clave.Secreta.123456"

//32 characters = 256 bits
"Alguna.Clave.Secreta.1234567890AB"
```

### 5.5 BCryptPasswordEncoder as a Separate Bean

Defining `BCryptPasswordEncoder` as a `@Bean` inside `SpringSecurityConfig` caused a circular dependency. The solution was to move it to a separate configuration class:

```java
// AppConfig.java
@Configuration
public class AppConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 5.6 Deserializing Roles from Token

Using `SimpleGrantedAuthority[].class` or `SimpleGrantedAuthorityMixin` to deserialize roles from the JWT payload failed in Jackson 2.20 due to missing `@class` type information. The fix was to deserialize as a generic `List<Map>` and build the authorities manually:

```java
@Override
public Collection<? extends GrantedAuthority> getRoles(String token) throws IOException {
    Object roles = getClaims(token).get("authorities");

    List<Map<String, Object>> list = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .readValue(roles.toString(), new TypeReference<List<Map<String, Object>>>() {});

    return list.stream()
        .filter(map -> map.containsKey("authority"))
        .map(map -> new SimpleGrantedAuthority((String) map.get("authority")))
        .collect(Collectors.toList());
}
```

---

## 6. Problems Found and How They Were Solved

### Problem 1 — Jackson could not deserialize `org.springframework.security.core.userdetails.User`

**Error:**
```
InvalidDefinitionException: Cannot construct instance of
`org.springframework.security.core.userdetails.User`
(no Creators, like default constructor, exist)
```

**Cause:** `JWTAuthenticationFilter` was trying to deserialize the login request body directly into Spring Security's `User` class, which has no default constructor and is not Jackson-deserializable.

**Solution:** Created a simple `LoginRequest` DTO:

```java
public class LoginRequest {
    private String username;
    private String password;
    // getters and setters
}
```

And used it in the filter:
```java
LoginRequest credentials = new ObjectMapper()
    .readValue(request.getInputStream(), LoginRequest.class);
```

---

### Problem 2 — NullPointerException on `username.trim()`

**Error:**
```
NullPointerException: Cannot invoke "String.trim()" because "username" is null
```

**Cause:** Cascading from Problem 1. The deserialization failed silently inside the catch block, leaving `username` as null. The code then called `username.trim()` without a null check.

**Solution:** Added a null validation before using the credentials:

```java
if (username == null || password == null) {
    throw new AuthenticationServiceException("Invalid or missing credentials");
}
username = username.trim();
```

---

### Problem 3 — ClassCastException when generating the JWT

**Error:**
```
ClassCastException: org.springframework.security.core.userdetails.User
cannot be cast to com.stockflow.backend.user.domain.User
```

**Cause:** `JWTServiceImpl.create()` was casting `auth.getPrincipal()` to the domain `User` entity, but `loadUserByUsername()` returns Spring Security's `User`, not the domain entity.

**Solution:** Cast to `UserDetails` interface instead:

```java
//Wrong
String username = ((com.stockflow.backend.user.domain.User) auth.getPrincipal()).getUsername();

//Correct
String username = ((UserDetails) auth.getPrincipal()).getUsername();
```

---

### Problem 4 — Jackson cannot serialize `java.time.Instant`

**Error:**
```
InvalidDefinitionException: Java 8 date/time type `java.time.Instant`
not supported by default
```

**Cause:** Spring Security 7's `FactorGrantedAuthority` contains an `issuedAt` field of type `java.time.Instant`, which Jackson cannot serialize without the JSR310 module.

**Solution:** Registered `JavaTimeModule` on the `ObjectMapper` and filtered out non-`SimpleGrantedAuthority` instances:

```java
// Filter only simple roles
Collection<? extends GrantedAuthority> roles = auth.getAuthorities()
    .stream()
    .filter(a -> a instanceof SimpleGrantedAuthority)
    .collect(Collectors.toList());

// Register Java time module
new ObjectMapper()
    .registerModule(new JavaTimeModule())
    .writeValueAsString(roles)
```

---

### Problem 5 — WeakKeyException: SECRET key too short

**Error:**
```
WeakKeyException: The specified key byte array is 216 bits which is not
secure enough for any JWT HMAC-SHA algorithm. Keys MUST have a size >= 256 bits.
```

**Cause:** JJWT 0.13.0 strictly enforces RFC 7518. The original SECRET `"Alguna.Clave.Secreta.123456"` is only 27 characters (216 bits), below the 256-bit minimum required for HS256.

**Solution:** Extended the SECRET to at least 32 characters:

```java
public static final String SECRET = Base64.getEncoder()
    .encodeToString("Alguna.Clave.Secreta.1234567890AB".getBytes());
```

---

### Problem 6 — BCrypt hash in DB did not match password

**Error:**
```
Bad credentials
```

**Cause:** The BCrypt hash stored in `import.sql` was generated with a different password, not `123456`. The `BCryptPasswordEncoder.matches()` call consistently returned `false`.

**Diagnosis:** Added a temporary log in `JpaUserDetailsService`:
```java
logger.info("Password match result: " + encoder.matches("123456", user.getPassword()));
// Output: Password match result: false
```

**Solution:** Generated a new valid hash using the app's own encoder and updated `import.sql`:
```java
logger.info("New hash: " + new BCryptPasswordEncoder().encode("123456"));
```

```sql
INSERT INTO users (username, password, enabled)
VALUES ('andres', '$2a$10$TWxv618Gpc/6pCiGM/8D2.PZpuFS4Xacu7g75UcLxCfxemFo68Upu', 1);
```

---

### Problem 7 — 403 Forbidden when accessing protected endpoints with valid token

**Error:** `403 Forbidden` even with a valid `ROLE_ADMIN` token.

**Cause:** `JWTServiceImpl.getRoles()` was failing to deserialize the roles from the token payload. Jackson threw an `InvalidTypeIdException` because it expected a `@class` type discriminator that was not present in the JSON. The exception was being swallowed, resulting in an empty authorities list — causing Spring Security to deny access.

**Solution:** Replaced the Mixin-based deserialization with a generic Map approach:

```java
List<Map<String, Object>> list = new ObjectMapper()
    .registerModule(new JavaTimeModule())
    .readValue(roles.toString(), new TypeReference<List<Map<String, Object>>>() {});

return list.stream()
    .filter(map -> map.containsKey("authority"))
    .map(map -> new SimpleGrantedAuthority((String) map.get("authority")))
    .collect(Collectors.toList());
```

---

### Problem 8 — Response returned 401 instead of 200 on successful login

**Error:** Login succeeded (token was generated) but the response status was `401`.

**Cause:** `response.setStatus(200)` was called **after** `response.getWriter().write(...)`. Tomcat 11 flushes the response immediately after the write, so the status could no longer be modified.

**Solution:** Moved `setStatus` and `setContentType` before the write:

```java
response.setStatus(200);
response.setContentType("application/json");
response.getWriter().write(new ObjectMapper().writeValueAsString(body));
```

---

### Problem 9 — Maven settings.xml required for dependency resolution

**Cause:** The tutorial being followed used Spring Security 5.x APIs (e.g., `AntPathRequestMatcher`, `WebSecurityConfigurerAdapter`) that were removed in Spring Security 6+. Running Spring Boot 4.0.2 with Spring Security 7.0.2 required a custom `settings.xml` in `C:\Users\{user}\.m2\` to force compatible dependency resolution.

**Lesson learned:** Always verify that tutorial code matches the Spring Boot and Spring Security versions in use. Major version upgrades (5.x → 6.x → 7.x) introduce breaking changes, especially in the Security module.

---

## Module File Structure

```
auth/
├── filter/
│   ├── JWTAuthenticationFilter.java   ← Handles login, generates token
│   └── JWTAuthorizationFilter.java    ← Validates token on every request
├── service/
│   ├── JWTService.java                ← Interface
│   └── JWTServiceImpl.java            ← create, validate, getRoles, getUsername, resolve
└── handler/
    └── LoginRequest.java              ← Login DTO (username + password)

user/
└── service/
    └── JpaUserDetailsService.java     ← Loads user from DB, used by AuthenticationManager

AppConfig.java                         ← BCryptPasswordEncoder bean
SpringSecurityConfig.java              ← Security filter chain, roles, exception handlers
```

---

## HTTP Status Codes Used

| Code | Meaning in StockFlow |
|---|---|
| `200 OK` | Successful login or authorized request |
| `401 Unauthorized` | Missing token, invalid token, or wrong credentials |
| `403 Forbidden` | Valid token but insufficient role/permissions |

> **Note:** The name `401 Unauthorized` is a historical misnomer in the HTTP spec. It actually means *unauthenticated* (we don't know who you are). `403 Forbidden` means *unauthorized* (we know who you are, but you can't access this).
