# Technical Design Document  
## Product Filtering Module (Admin Product Management)
---

# 1. Overview

The **Product Filtering Module** provides dynamic filtering, pagination, and sorting for Product retrieval in the StockFlow API.

This module is designed for **Admin Product Management (CRUD dashboard)** and is architecturally different from the Store Catalog module.

Unlike the Store Catalog, this module operates directly on the `Product` and `Category` entities as the aggregate root.

---
# 2. Business Requirement

The system must allow administrators to:

- Browse products with flexible filtering
- Search products by name (case-insensitive)
- Filter by SKU
- Filter by price range
- Filter by stock range (when applicable)
- Filter by active status
- Filter by discontinued status
- Filter by category name
- Support pagination and sorting
- Maintain scalability for growing product catalogs

---


## Product Filtering

StockFlow includes a dynamic filtering module designed for real catalog search scenarios.

Supported filters:

- Case-insensitive name search
- Price range filtering
- Stock range filtering
- Pagination and sorting

Example:
**GET** /api/products?name=ring&minPrice=10&maxPrice=50&page=0&size=10
- [Product filtering module details](docs/product-filter-module.md)

This allows clients to implement flexible product search similar to e-commerce filtering experiences.

---

## Product CRUD

Supports full product lifecycle management:

- Create products
- Update product details
- Soft delete products (logical deletion)
- Retrieve active catalog items

Soft deletion helps preserve historical integrity and prevents accidental data loss.

- [Product CRUD module details](docs/product-crud-module.md)

# 3. Architectural Context

## Domain Scope

This module is:

> Entity-centric at the data access layer  
> DTO-driven at the API layer

### Meaning:

- The database query is centered around the `Product` entity.
- JPA repositories and Specifications operate directly on `Product`.
- The API does not expose the entity directly.
- Responses are mapped to DTOs (`ProductSummaryDTO`).
---

# 4. Endpoint Specification

### Endpoint
GET /api/products

---

## Supported Query Parameters

| Parameter | Description |
|-----------|------------|
| name | Partial product name search (case-insensitive) |
| sku | Exact SKU match (case-insensitive) |
| category | category unique name |
| minPrice | Minimum price (>= value) |
| maxPrice | Maximum price (<= value) |
| minStock | Minimum stock (>= value) |
| maxStock | Maximum stock (<= value) |
| active | Boolean filter |
| discontinuedAt | Discontinued date filter |
| page | Page index (default: 0) |
| size | Page size (default: 10) |
---
## Example Request


> GET /api/products?name=Pearl&minPrice=10&maxPrice=50&page=0&size=10

> GET /api/products?category=Rings&minPrice=10&maxPrice=50&page=0&size=10

---

# 5. Technical Design

## 5.1 Data Access Model

- Repository: `JpaRepository<Product, Long>`
- Specification-based dynamic filtering
- Criteria API predicates generated from `ProductFilter`
- Pagination via `Pageable`

Example repository usage:

```java
	Page<Product> page = repo.findAll(spec, pageable);
```

---
## 5.2 Service Layer Responsibility

**The service layer:**

- Determines which filters are active

- Builds Specification<Product>

- Executes repository query

- Maps results to DTOs

Example:

```java
	return page.map(Mapper::toSummaryDTO);
```
---
## 5.3 Architectural Characteristics
**Entity-Centric Query Model**

- Queries operate on Product

- Filtering maps naturally to entity fields

- Compatible with JpaSpecificationExecutor<Product>

**DTO-Driven API**

- Controllers return ProductSummaryDTO

- Entities are not exposed externally

- Clean separation between persistence and API contract
---

# 6. Architectural Separation (Comparison with Store Catalog)
| Product Filtering (Admin) | Store Catalog (User)               |
| --------------------------| ---------------------------------- |
| Entity-centric queries    | Projection-centric queries         |
| Operates on `Product` and `Category`	| Combines Product + Inventory       |
| Uses Specifications | Uses composed query with LEFT JOIN |
| Returns DTO mapped from entity | Returns DTO mapped from projection |
| No Store context | Store-specific context required    |


# 7. Design Rationale

- This module does not require a projection interface because:

- The result matches a real entity (Product).

- No computed fields are required.

- No contextual joins (like Store-specific Inventory) are needed.

- Filtering can be expressed cleanly using Specifications.

**This keeps:**

- Queries maintainable

- Architecture simple

- Performance predictable

- Code extensible

# 8. Conclusion

**The Product Filtering Module:**

- Is entity-centric at the persistence layer

- Is DTO-driven at the API layer

- Uses dynamic Specifications for flexibility

- Supports scalable pagination

- Maintains clean architectural boundaries

This approach is appropriate because the result set represents a real domain entity (Product) rather than a composed contextual view.
