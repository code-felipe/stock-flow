package com.stockflow.backend.product.repository.specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.stockflow.backend.inventory.domain.Inventory;
import com.stockflow.backend.product.domain.Product;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class ProductStockSpecification {
	
	private ProductStockSpecification() {}

	public static Specification<Product> forStore(Long storeId, BigDecimal minStock, BigDecimal maxStock) {
	    return (root, query, cb) -> {
	        Join<Product, Inventory> inv = root.join("inventories", JoinType.LEFT);
	        inv.on(cb.equal(inv.get("id").get("storeId"), storeId));
	        query.distinct(true);

	        List<Predicate> predicates = new ArrayList<>();

	        if (minStock != null) {
	            predicates.add(cb.greaterThanOrEqualTo(inv.get("onHand"), minStock.intValue()));
	        }
	        if (maxStock != null) {
	            predicates.add(cb.lessThanOrEqualTo(inv.get("onHand"), maxStock.intValue()));
	        }

	        return cb.and(predicates.toArray(new Predicate[0]));
	    };
	}
	
    public static Specification<Product> nameContains(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return null;
            return cb.like(
                cb.lower(root.get("name")),
                "%" + name.trim().toLowerCase() + "%"
            );
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
    
}
