# StockFlow API

StockFlow is a REST API designed for multi-store inventory and sales management.

It provides a structured backend that covers the full operational cycle of a 
retail business: product catalog management, stock tracking per store, order 
processing, and payment record-keeping — all secured with role-based authentication.

Key capabilities include:

- Product & category management with soft deletion and SKU control
- Multi-store inventory tracking, decoupled from the product catalog
- Order lifecycle management: from cart to fulfillment, cancellation, and stock restoration
- Payment record-keeping (internal tracking only, no payment gateway integration)
- Role-based access control for admin and user operations
- Dynamic filtering and pagination across products and inventories
- Sales statistics and performance metrics (upcoming)
- Order and sales report generation (upcoming)

---

## Core Features

- Product management (CRUD): [Product CRUD module details](docs/product-crud-module.md)
- Inventory management (CRUD): [Inventory CRUD module details](docs/inventory-crud-module.md)
- Order management (CRUD): [Order CRUD module details](docs/order-crud-module.md)
- Inventory tracking per product
- Soft deletion to preserve historical data
- Pagination and sorting for large datasets
- Dynamic product filtering (price, stock, name, SKU, category, ranges): [Product Filter module details](docs/product-filter-module.md)
- Dynamic store/inventory filtering (price, stock, name, SKU, category, onHand, reserved, ranges): [Inventory Filter module details](docs/inventory-filter-module.md)
- RESTful API design with clear resource modeling
- Input validation and centralized exception handling
- Architecture - Package by Feature - [Architecture details](docs/architecture.md)

---

## UML - Domain Model

The domain model is designed to reflect a realistic catalog/inventory structure and maintain separation between persistence and API layers.

![UML diagram](docs/diagrams/uml-diagram.svg)

---

## Database Schema - Entity Relationship Diagram (ERD)

The following diagram represents the current database schema generated 
from the live MySQL instance. It reflects the implemented entities and 
their relationships as of the current development phase.
> Note: The schema reflects the current implementation phase. 
> Some modules such as CartItem (session-based) are not represented 
> as they do not persist in the database.

![UML diagram](docs/diagrams/stock-flow-db-design.svg)


---

## Package Structure

```text
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

## Modules

Each module groups one or more related entities under a shared business concern. Below is a brief description of each, with a link to its detailed documentation.

| Module | Entities | Description | Docs |
|---|---|---|---|
| **Admin — Stock Management** | `Store` · `Product` · `Inventory` | Gives the admin full control over what products exist, which store holds them, and how much stock is available. Intentionally isolated from the customer-facing layer. | [View](docs/store-inventory-product-category-module.md) |
| **Catalog** | `Category` | Manages how products are classified and grouped for browsing.|

> Module docs follow the same format: overview, responsibilities, API endpoints, and design decisions.

---


## Tech Stack

- Java  
- Spring Boot  
- Spring Data JPA  
- MySQL / SQL Database  
- Maven
- Swagger  

---

## Purpose

StockFlow is a learning-driven but realistically designed backend service that explores:

- REST API design
- Data modeling with JPA
- Filtering and pagination strategies
- Validation and error handling patterns
- Separation of concerns in layered architecture

The goal is to simulate how a real catalog/inventory backend might be structured while keeping the project maintainable and extensible.

---
