# Product Filtering Module

The **Product Filtering Module** provides dynamic filtering, pagination, and sorting for product retrieval in the StockFlow API.

It is designed to support scalable catalog browsing similar to modern e-commerce platforms.

---

## Features

- Dynamic filters (optional parameters)
- Pagination support
- Case-insensitive search by name
- Price range filtering
- Stock range filtering
- Default sorting by newest products

---

## Endpoint
GET /api/products

---

## Supported Query Parameters

| Parameter | Description |
|----------|-------------|
| name | Partial product name search (case-insensitive) |
| sku | Exact SKU match (case-insensitive) |
| minPrice | Minimum price (>= value) |
| maxPrice | Maximum price (<= value) |
| minStock | Minimum stock (>= value) |
| maxStock | Maximum stock (<= value) |
| page | Page index (default: 0) |
| size | Page size (default: 10) |

---

## Example Request
GET /api/products?name=ring&minPrice=10&maxPrice=50&page=0&size=10


---

## Technical Implementation

- Spring Boot
- Spring Data JPA Specifications
- JPA Criteria API for dynamic queries
- Pageable for pagination

---

## Purpose

This module enables scalable product browsing and flexible filtering similar to modern e-commerce platforms like Amazon or Shopify.

It ensures:

- Efficient database querying
- Clean architecture
- Flexible filtering logic
- REST-compliant design

---

## Future Improvements

Possible extensions:

- Category filtering
- Rating filters
- Full-text search (Elasticsearch)
- Dynamic sorting via query parameters
- Multi-select filters

---

## Summary

The Product Filtering Module provides:

- Dynamic filtering  
- Pagination  
- Scalable design  
- Clean architecture  
- REST best practices
- Real-world applicability  

---
