package com.stockflow.backend.product.repository.specification;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.jpa.domain.Specification;

import com.stockflow.backend.product.domain.Product;

public class ProductSpecification {
	
	private ProductSpecification() {}

    public static Specification<Product> nameContains(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return null;
            return cb.like(
                    cb.lower(root.get("name")),
                    "%" + name.trim().toLowerCase() + "%"
            );
        };
    }

    public static Specification<Product> hasId(Long id) {
        return (root, query, cb) -> {
            if (id == null) return null;
            return cb.equal(root.get("id"), id);
        };
    }

    public static Specification<Product> hasSku(String sku) {
        return (root, query, cb) -> {
            if (sku == null || sku.isBlank()) return null;
            return cb.equal(
                    cb.lower(root.get("sku")),
                    sku.trim().toLowerCase()
            );
        };
    }

    public static Specification<Product> minPrice(Double minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null) return null;
            return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
        };
    }

    public static Specification<Product> maxPrice(Double maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null) return null;
            return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    public static Specification<Product> minStock(BigDecimal minStock) {
        return (root, query, cb) -> {
            if (minStock == null) return null;
            return cb.greaterThanOrEqualTo(root.get("stock"), minStock);
        };
    }

    public static Specification<Product> maxStock(BigDecimal maxStock) {
        return (root, query, cb) -> {
            if (maxStock == null) return null;
            return cb.lessThanOrEqualTo(root.get("stock"), maxStock);
        };
    }

    public static Specification<Product> discontinuedBefore(Instant discontinuedAt) {
        return (root, query, cb) -> {
            if (discontinuedAt == null) return null;
            return cb.lessThanOrEqualTo(root.get("discontinuedAt"), discontinuedAt);
        };
    }
    
    
    public static Specification<Product> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return cb.isTrue(root.get("active"));
            }
            return cb.equal(root.get("active"), active);
        };
    }

    
    public static Specification<Product> hasCategory(String category) {
        return (root, query, cb) -> {
            if (category == null || category.isBlank()) return null;
            query.distinct(true);
            return cb.equal(
                    cb.lower(root.join("categories").get("name")),
                    category.trim().toLowerCase()
            );
        };
    }
}
