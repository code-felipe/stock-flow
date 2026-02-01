# StockFlow API

StockFlow is a REST API for managing product inventory and catalog data.

It allows clients to create, retrieve, update, and manage products in a scalable and structured way.

---

## Core Features

- Product management (CRUD)
- Inventory tracking
- Pagination and sorting
- Dynamic product filtering (price, stock, name, SKU)
- RESTful design using Spring Boot

---

## Product Filtering

StockFlow includes a dynamic filtering module that supports:

- Name search (case-insensitive)
- Price range filters
- Stock range filters
- Pagination for large catalogs

Example:
**GET /api/products?name=ring&minPrice=10&maxPrice=50&page=0&size=10**

---

## Tech Stack

- Java  
- Spring Boot  
- Spring Data JPA  
- MySQL / SQL Database  
- Maven  

---

## Purpose

StockFlow is designed as a scalable backend service for inventory and catalog management, similar to what modern e-commerce platforms use.

It focuses on clean architecture, performance, and real-world API design.

---
