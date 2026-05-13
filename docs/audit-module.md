# 📋 AuditLog Module — Critical Operation Traceability

## Overview

The **AuditLog** module provides traceability over the most critical endpoints in the application: `POST`, `PUT`, `PATCH`, and `DELETE`. Its goal is to answer the key audit questions:

- Who performed the action?
- Which endpoint was hit and with what HTTP method?
- What data was persisted and on which entity?
- When did it happen?
- What was the HTTP result of the operation?

The entity is **fully decoupled** from the rest of the application — no JPA relationships with other tables — making it an independent audit record, conceptually equivalent to what an AOP system would achieve automatically.

---

## Module Structure

```
auditlog/
├── domain/
│   └── AuditLog.java               # JPA entity
├── enumerate/
│   └── AuditAction.java            # CREATE | UPDATE | DISABLE
├── repository/
│   └── AuditLogRepository.java     # Persistence layer
├── service/
│   ├── IAuditLogService.java       # Interface
│   └── AuditLogServiceImpl.java    # saveAudit + saveFailedAudit
├── dto/
│   └── AuditLogResponseDTO.java    # Query response
└── mapper/
    └── AuditLogMapper.java         # Entity → DTO conversion
```

---

## Data Captured Per Event

| Field | Description |
|---|---|
| `performedBy` | Username extracted from the active JWT |
| `userId` | Logical FK to the user (no JPA coupling) |
| `entityName` | Name of the affected entity: `"Product"`, `"Order"`, `"Inventory"` — `"UNKNOWN"` on failure |
| `entityId` | ID of the specific record affected — `null` on failure |
| `action` | Enum: `CREATE`, `UPDATE`, `DISABLE` — `null` on failure |
| `httpMethod` | HTTP verb: `POST`, `PUT`, `PATCH`, `DELETE` |
| `endpoint` | Full request URI: e.g. `/api/admin/products` |
| `previousValue` | JSON snapshot before the change (for updates) |
| `newValue` | JSON snapshot after the change |
| `performedAt` | Exact timestamp of the operation |
| `httpStatus` | HTTP response code: `200`, `201`, `404`, `409`, etc. |
| `success` | `true` for successful operations, `false` for failures |
| `errorMessage` | Error description — `null` on success |

---

## Domain Model

```java
@Entity
@Table(name = "audit_log")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String performedBy;
    private Long userId;

    private String entityName;
    private Long entityId;

    @Enumerated(EnumType.STRING)
    private AuditAction action;

    private String httpMethod;
    private String endpoint;

    @Column(columnDefinition = "TEXT")
    private String previousValue;

    @Column(columnDefinition = "TEXT")
    private String newValue;

    private LocalDateTime performedAt;

    private Integer httpStatus;
    private Boolean success;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;
}
```

```java
public enum AuditAction {
    CREATE,
    UPDATE,
    DISABLE   // Used for soft deletes (enable = false)
}
```

---

## How It Works

### Two-Path Audit Strategy

The module handles two distinct scenarios through two dedicated methods:

| Scenario | Trigger point | Method called |
|---|---|---|
| Successful operation | Controller, after persistence | `saveAudit(...)` |
| Failed operation / exception | GlobalExceptionHandler | `saveFailedAudit(...)` |

This separation ensures every critical request is recorded regardless of outcome.

---

### Path 1 — Successful Operation (Controller)

After the service persists the data successfully, the controller calls `saveAudit()` with the full context: entity name, entity ID, action, new value, and HTTP status.

```
Client Request
      │
      ▼
Controller
      ├── productService.createProduct(dto)   ← persistence
      └── auditService.saveAudit(...)          ← audit with full context
```

**Controller example:**

```java
@PostMapping
public ResponseEntity<Map<String, Object>> createProduct(
        @Valid @RequestBody ProductCreateRequestDTO dto,
        HttpServletRequest request,
        Authentication auth) {

    ProductCreateResponseDTO created = productService.createProduct(dto);

    auditService.saveAudit(
        auth, request,
        AuditAction.CREATE, "Product", created.getId(),
        created, null, 201
    );

    return ResponseEntity.created(URI.create("/api/products/" + created.getId()))
            .body(Map.of("message", "Product created successfully", "product", created));
}
```

**Audit record produced:**

```json
{
  "performBy": "andres",
  "userId": 1,
  "entityName": "Product",
  "entityId": 19,
  "action": "CREATE",
  "httMethod": "POST",
  "endpoint": "/api/admin/products",
  "newValue": "{\"id\":19,\"name\":\"Doble Dragon Emerald\",...}",
  "previousValue": null,
  "performedAt": "2026-05-13T23:42:52.677652Z",
  "httpStatus": 201,
  "succes": true,
  "errorMessage": null
}
```

---

### Path 2 — Failed Operation (GlobalExceptionHandler)

When the service throws an exception, execution never reaches the `saveAudit()` call in the controller. Spring routes the exception directly to `GlobalExceptionHandler`, which captures the failure and calls `saveFailedAudit()`.

```
Client Request
      │
      ▼
Controller
      └── productService.createProduct(dto)   ← throws exception
                  │
                  └──► GlobalExceptionHandler
                              └── auditService.saveFailedAudit(...)  ← audit with partial context
```

**GlobalExceptionHandler:**

```java
@Autowired
private IAuditLogService auditService;

@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<Map<String, Object>> handleNotFound(
        ResourceNotFoundException ex,
        HttpServletRequest request,
        Authentication auth) {

    auditService.saveFailedAudit(auth, request, 404, ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
            "timestamp", Instant.now().toString(),
            "status", 404,
            "error", "Not Found",
            "message", ex.getMessage()
    ));
}
```

**Audit record produced:**

```json
{
  "performBy": "andres",
  "userId": 1,
  "entityName": "UNKNOWN",
  "entityId": null,
  "action": null,
  "httMethod": "POST",
  "endpoint": "/api/admin/products",
  "previousValue": null,
  "newValue": null,
  "performedAt": "2026-05-13T23:44:41.182025Z",
  "httpStatus": 404,
  "succes": false,
  "errorMessage": "One or more categoryIds do not exist."
}
```

`entityName`, `entityId`, and `action` are `UNKNOWN`/`null` on failures because that context lives in the controller and never reaches the exception handler. The endpoint, HTTP method, user, and error message are always captured.

---

## Service Implementation

### `saveAudit` — Success path

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
@Override
public AuditLogResponseDTO saveAudit(Authentication auth, HttpServletRequest request,
        AuditAction action, String entityName, Long entityId,
        Object respNewValue, Object respPrevValue, Integer httpStatus) {

    String username = auth.getName();
    User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    try {
        return Mapper.from(auditRepo.save(AuditLog.builder()
                .performBy(username)
                .userId(user.getId())
                .entityName(entityName)
                .entityId(entityId)
                .action(action)
                .httMethod(request.getMethod())
                .endpoint(request.getRequestURI())
                .newValue(objectMapper.writeValueAsString(respNewValue))
                .previousValue(objectMapper.writeValueAsString(respPrevValue))
                .performedAt(Instant.now())
                .httpStatus(httpStatus)
                .succes(true)
                .build()));
    } catch (Exception e) {
        System.out.println("AUDIT ERROR: " + e.getMessage());
        return null;
    }
}
```

### `saveFailedAudit` — Failure path

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
@Override
public void saveFailedAudit(Authentication auth, HttpServletRequest request,
        int httpStatus, String errorMessage) {

    String username = auth != null ? auth.getName() : "anonymous";
    Long userId = userRepo.findByUsername(username)
            .map(User::getId)
            .orElse(null);

    try {
        auditRepo.save(AuditLog.builder()
                .performBy(username)
                .userId(userId)
                .entityName("UNKNOWN")
                .entityId(null)
                .action(null)
                .httMethod(request.getMethod())
                .endpoint(request.getRequestURI())
                .performedAt(Instant.now())
                .httpStatus(httpStatus)
                .succes(false)
                .errorMessage(errorMessage)
                .build());
    } catch (Exception e) {
        System.out.println("FAILED AUDIT ERROR: " + e.getMessage());
    }
}
```

### Why `REQUIRES_NEW`?

Both methods use `@Transactional(propagation = Propagation.REQUIRES_NEW)`. This opens a **separate, independent transaction** for the audit write. Without it, if the original transaction is being rolled back (due to the exception), the audit record would roll back with it and be lost.

---

## Design Decisions

### Decoupled by Design

`AuditLog` has no `@ManyToOne` or `@OneToMany` relationships with any other entity. User identity is stored as plain fields (`performedBy`, `userId`) rather than a JPA join. This means:

- The audit table is never affected by cascades or schema changes in other modules
- Records are preserved even if the referenced user or entity is later deleted
- The module can be extracted or replicated independently

### Manual Embedding vs AOP

| | Manual (current) | AOP |
|---|---|---|
| Complexity | Low | High |
| Transparency | Explicit per endpoint | Hidden via proxies |
| Flexibility | Full control per call | Requires pointcut tuning |
| Failure capture | Via GlobalExceptionHandler | Native via `@Around` |
| Maintainability | Straightforward | Harder to debug |

For the current scale of the project, manual embedding is the right tradeoff. AOP would simplify failure capture but adds significant architectural overhead.

---

## Known Limitations

### Partial Context on Failures

When an exception is caught by `GlobalExceptionHandler`, `entityName`, `entityId`, and `action` cannot be recovered — that context exists only in the controller scope. The audit record is still written with `"UNKNOWN"` for those fields.

This is acceptable because the endpoint URI (`/api/admin/products`), HTTP method, user, and error message together provide sufficient context to identify what happened and who triggered it.

### Validation Errors (`@Valid`)

For `MethodArgumentNotValidException` and `HandlerMethodValidationException`, the exception fires before the service layer is reached — no entity is involved yet. These are audited with partial context only (endpoint + error message).

---

## Future Improvements

- Introduce pagination and filtering for audit log retrieval (`GET /api/audit-logs?entity=Product&userId=3&from=2026-01-01`)
- Consider async persistence (`@Async`) to avoid blocking the main request thread on high-traffic endpoints
- Evaluate AOP adoption if the number of audited endpoints grows significantly — it would allow full context capture on failures natively
