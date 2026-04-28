# StockFlow — Backend Architecture

## Overview

This project uses a **Package by Feature** approach, each domain entity (category, product, inventory, store) owns its full MVC stack: controller, service, repository, domain model, and DTOs.

This was chosen over the traditional Package by Layer structure to avoid monolithic `controllers/`, `services/`, and `repositories/` folders that grow cluttered as the project scales. Keeping each feature self-contained makes the codebase easier to navigate and maintain.

A shared `utils.mapper` package handles `DTO ↔ entity` mapping across all domains.

---

## Package Structure

```
com.stockflow.backend/
│
├── category/
│   ├── controller/
│   ├── domain/
│   ├── dto/
│   ├── repository/
│   └── service/
│
├── product/
│   ├── controller/
│   ├── domain/
│   ├── dto/
│   ├── repository/
│   └── service/
│
├── inventory/
│   ├── controller/
│   ├── domain/
│   ├── dto/
│   ├── repository/
│   └── service/
│
├── store/
│   ├── controller/
│   ├── domain/
│   ├── dto/
│   ├── repository/
│   └── service/
│
├── order/
│   ├── controller/
│   ├── domain/
│   ├── dto/
│   ├── repository/
│   └── service/
│
├── orderItem/
│   ├── controller/
│   ├── domain/
│   ├── dto/
│   ├── repository/
│   └── service/│
│
└── utils/
    └── mapper/
```

---

## Layer Responsibilities

Each entity package contains the same five layers, each with a clear, bounded responsibility:

| Layer | Package | Responsibility |
|---|---|---|
| **Controller** | `{entity}/controller` | Exposes REST endpoints. Handles HTTP request/response lifecycle. Delegates all logic to the service layer. |
| **Service** | `{entity}/service` | Contains business logic. Orchestrates repository calls. Converts between domain models and DTOs via the mapper. |
| **Repository** | `{entity}/repository` | Spring Data JPA interface. Defines queries and data access operations against the database. |
| **Domain** | `{entity}/domain` | JPA entity class mapped to a database table. Represents the core data model. |
| **DTO** | `{entity}/dto` | Data Transfer Objects. Decouples the API contract from the internal domain model. May include request/response variants. |

---

## Shared Utilities

### `utils/mapper`

A single class with static methods that handles all `DTO ↔ entity` conversions across every domain. Currently all mappings live in one place for simplicity — the plan is to split them into per-entity mappers as the project grows.

```
utils/
└── mapper/
    └── Mapper       ← static methods for all entities
```

> Future refactor: split into CategoryMapper, ProductMapper, etc., one class per domain.

---

## Data Flow (per request)

```
HTTP Request
     │
     ▼
 Controller          ← validates input, calls service
     │
     ▼
  Service            ← applies business rules, uses mapper
     │         │
     ▼         ▼
Repository   Mapper  ← converts DTO ↔ Domain
     │
     ▼
  Database
     │
     ▼
 Domain Entity       ← mapped back to response DTO
     │
     ▼
HTTP Response
```

---

## Design Notes

### What works well about this approach

- **High cohesion per entity.** All code related to `Product`, for example, lives in one place. Finding, reading, and modifying a feature requires navigating only one package tree.
- **Independent scalability.** As the project grows, each entity module can evolve, be extracted, or be refactored without touching unrelated code.
- **Mirrors Domain-Driven Design (DDD) aggregate thinking.** Each entity package roughly corresponds to a bounded context or aggregate root.

### Things to keep in mind as the project grows

- **Cross-entity dependencies**: If `InventoryService` needs data from `Product`, call `ProductService` instead of going directly to `ProductRepository`. Each service is the only one responsible for its own data — other modules should ask it, not bypass it.
- **Shared DTOs**: If a DTO combines data from two entities (e.g., an inventory response that also includes product info), it doesn't clearly belong to either module. In those cases, a `shared/dto` package is a better home than forcing it into one entity arbitrarily.

**Example:**

```java
				
StoreSummaryDTO storeDTO = storeService.findById(storeId);
		
ProductDetailDTO productDTO = productService.findById(productId);
		....
```

---

## Entity Summary

| Entity | Description |
|---|---|
| `Category` | Product classification / grouping |
| `Product` | Core sellable item with attributes and category reference |
| `Inventory` | Stock levels and availability per product/store |
| `Store` | Physical or logical location holding inventory |
| `Order` | Sales transaction linking a user and store to a set of purchased items |
| `OrderItem` | Individual line item within an order, capturing a price and quantity snapshot at the time of purchase |

---
