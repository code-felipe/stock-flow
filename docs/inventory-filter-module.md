# Technical Design Document  
## Inventory Filtering Module (Admin Inventory Management)
---

# 1. Overview

The **Inventory Filtering Module** provides dynamic filtering, pagination, and sorting for Inventory retrieval in the StockFlow API.

This module is designed for **Admin Inventory Management (CRUD dashboard)** and is architecturally different from the Product Catalog module.

Unlike the Product Catalog, this module operates on `Inventory` as the aggregate root, joining `Product` and `Store` to compose a contextual stock view per store.

---

# 2. Business Requirement

The system must allow administrators to:

- Browse inventory entries with flexible filtering
- Search by product name (case-insensitive)
- Filter by SKU
- Filter by price range
- Filter by stock range (`onHand`, `reserved`)
- Filter by price range (`minPrice`, `maxPrice`)
- Filter by active status
- Filter by discontinued status
- Filter by category name
- Support pagination and sorting
- Maintain scalability for growing inventory catalogs

---

## Why Products Should Not Own Stock

A common design mistake is placing stock quantities directly on the `Product` entity. This creates tight coupling between the product catalog and its operational stock state, leading to several problems:

- A product may exist in multiple stores with **different stock levels**. A single `stock` field on `Product` cannot represent this without duplication or ambiguity.
- Stock is **operational data** (changes frequently), while product data is **descriptive** (name, price, SKU). Mixing both in one entity violates the Single Responsibility Principle.
- Placing stock on `Product` makes it impossible to **independently scale or audit** stock operations without touching the product catalog.
- It prevents clean **bounded context separation** between catalog management and warehouse/inventory management, which is a recognized best practice in domain-driven design.

By delegating stock ownership to `Inventory`, StockFlow decouples these concerns, enabling each store to manage its own stock levels independently without affecting the product definition.

---

## Inventory Filtering

StockFlow includes a dynamic filtering module designed for real inventory search scenarios.

Supported filters:

- Case-insensitive product name search
- Price range filtering
- Stock range filtering (`onHand`, `reserved`)
- Pagination and sorting

Example:  
**GET** /api/store/{storeId}/inventories?name=ring&minStock=5&maxStock=100&page=0&size=10

This allows administrators to implement flexible inventory search scoped to a specific store.

---

## Inventory CRUD

Supports full inventory lifecycle management:

- Create inventory entries
- Update inventory details
- Soft delete inventories (logical deletion - update instead)
- Retrieve active inventory items

Soft deletion helps preserve historical integrity and prevents accidental data loss.

---

# 3. Architectural Context

## Domain Scope

This module is:

> Entity-centric at the data access layer  
> DTO-driven at the API layer

### Meaning:

- The database query is centered around the `Inventory` entity.
- JPA repositories and Specifications operate directly on `Inventory`, joining `Product` and `Store`.
- The API does not expose the entity directly.
- Responses are mapped to DTOs (`ProductStockDTO`).

The join path is: **Store → Inventory → Product → Category**, making the specification more complex than in the product module, since it must traverse multiple associations to filter by product or category attributes.

---

# 4. Endpoint Specification

### Endpoint
GET /api/store/{storeId}/inventories

---

## Supported Query Parameters

| Parameter      | Description                                    |
|----------------|------------------------------------------------|
| name           | Partial product name search (case-insensitive) |
| sku            | Exact SKU match (case-insensitive)             |
| category       | Category unique name                           |
| minPrice       | Minimum price (>= value)                       |
| maxPrice       | Maximum price (<= value)                       |
| minStock       | Minimum onHand stock (>= value)                |
| maxStock       | Maximum onHand stock (<= value)                |
| active         | Boolean filter                                 |
| discontinuedAt | Discontinued date filter                       |
| page           | Page index (default: 0)                        |
| size           | Page size (default: 10)                        |

---

## Example Requests

> GET /api/store/1/inventories?name=Pearl&minPrice=10&maxPrice=50&page=0&size=10

> GET /api/store/1/inventories?category=Rings&minStock=5&maxStock=100&page=0&size=10

---

# 5. Technical Design

## 5.1 Data Access Model

- Repository: `JpaRepository<Inventory, InventoryId>`
- Specification-based dynamic filtering
- Criteria API predicates generated from `InventoryFilter`
- Joins traversed: `Inventory → Product → Category` and `Inventory → Store`
- Pagination via `Pageable`

Example repository usage:

```java
Page<Inventory> page = repo.findAll(spec, pageable);
```

---

## 5.2 Specification Design

The Specification is more complex than the product module because filters must traverse from `Inventory` into `Product` and `Category`. The store context is always required and is resolved via the composite `InventoryId` (`storeId` + `productId`).

```java
public class ProductStockSpecification {

    private ProductStockSpecification() {}

    public static Specification<Inventory> forStore(Long storeId, BigDecimal minStock, BigDecimal maxStock) {
        return (root, query, cb) -> {
            Join<Inventory, Product> product = root.join("product", JoinType.LEFT);
            root.on(cb.equal(root.get("id").get("storeId"), storeId));
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            if (minStock != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("onHand"), minStock.intValue()));
            }
            if (maxStock != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("onHand"), maxStock.intValue()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Inventory> nameContains(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return null;
            return cb.like(
                cb.lower(root.join("product").get("name")),
                "%" + name.trim().toLowerCase() + "%"
            );
        };
    }

    public static Specification<Inventory> hasCategory(String category) {
        return (root, query, cb) -> {
            if (category == null || category.isBlank()) return null;
            query.distinct(true);
            return cb.equal(
                cb.lower(root.join("product").join("categories").get("name")),
                category.trim().toLowerCase()
            );
        };
    }

    public static Specification<Inventory> minPrice(Double minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null) return null;
            return cb.greaterThanOrEqualTo(root.join("product").get("price"), minPrice);
        };
    }

    public static Specification<Inventory> maxPrice(Double maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null) return null;
            return cb.lessThanOrEqualTo(root.join("product").get("price"), maxPrice);
        };
    }
}
```

---

## 5.3 DTO Mapping with Implicit Coalesce

The mapper applies an **implicit Coalesce** pattern: if no `Inventory` record exists for a given product in the store, `onHand` and `reserved` default to `0` instead of `null`. This prevents null propagation in the API response and keeps the DTO contract consistent.

```java
public static ProductStockDTO toSummaryDTO(Product product, Inventory inv) {
    return ProductStockDTO.builder()
            .productId(product.getId())
            .productName(product.getName())
            .productDescription(product.getDescription())
            .productPrice(product.getPrice())
            .productSku(product.getSku())
            .productImageUrl(product.getImageUrl())
            .productIsActive(product.getActive())
            .productDiscontinuedAt(product.getDiscontinuedAt())
            .onHand(inv != null ? inv.getOnHand() : 0)// Here - Coalesce
            .reserved(inv != null ? inv.getReserved() : 0)// Here - Coalesce
            .productCreatedAt(product.getCreatedAt())
            .updatedAt(inv != null ? inv.getUpdatedAt() : null)
            .createdAt(inv != null ? inv.getCreatedAt() : null)
            .build();
}
```

---

## 5.4 Service Layer Responsibility

**The service layer:**

- Determines which filters are active
- Builds `Specification<Inventory>`
- Executes repository query scoped to `storeId`
- Maps results to DTOs

Example:

```java
return page.map(inv -> Mapper.toSummaryDTO(inv.getProduct(), inv));
```

---

## 5.5 Architectural Characteristics

**Entity-Centric Query Model**

- Queries operate on `Inventory` as the root
- Filtering traverses joins: `Inventory → Product → Category`
- Compatible with `JpaSpecificationExecutor<Inventory>`

**DTO-Driven API**

- Controllers return `ProductStockDTO`
- Entities are not exposed externally
- Clean separation between persistence and API contract

---

# 6. Architectural Separation (Comparison with Product Filtering)

| Inventory Filtering (Admin)                     | Product Filtering (Admin)                   |
|-------------------------------------------------|---------------------------------------------|
| Entity-centric queries on `Inventory`           | Entity-centric queries on `Product`         |
| Joins: Store → Inventory → Product → Category  | Operates on `Product` and `Category` only   |
| Uses Specifications with multi-level joins      | Uses Specifications with direct predicates  |
| Returns DTO mapped from `Inventory` + `Product` | Returns DTO mapped from entity              |
| Store context always required (`storeId`)       | No Store context                            |
| Stock decoupled from product definition         | No stock awareness                          |

---

# 7. Design Rationale

This module does not collapse stock into the `Product` entity because:

- The same product can have **different stock levels per store**.
- Stock is **operational data** that changes independently of product metadata.
- Keeping `Inventory` as the aggregate root for stock queries allows **independent auditing, scaling, and access control** per store.
- Filtering can be expressed cleanly using Specifications that traverse the `Inventory → Product` join.

**This keeps:**

- Queries maintainable
- Architecture decoupled
- Performance predictable
- Stock management independently extensible

---

# 8. Conclusion

**The Inventory Filtering Module:**

- Is entity-centric at the persistence layer (`Inventory` as root)
- Is DTO-driven at the API layer (`ProductStockDTO`)
- Uses dynamic Specifications for multi-level join filtering
- Is always scoped to a `storeId` context
- Applies implicit Coalesce in DTO mapping to avoid null stock values
- Maintains clean architectural boundaries between catalog and stock management

This approach is appropriate because the result set represents a **contextual stock view per store**, not a raw product entity, requiring a composed join that only `Inventory` as the aggregate root can naturally express.
