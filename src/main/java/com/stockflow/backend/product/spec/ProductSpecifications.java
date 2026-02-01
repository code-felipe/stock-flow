package com.stockflow.backend.product.spec;


import com.stockflow.backend.product.domain.Product;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductSpecifications {

    public static Specification<Product> withFilters(
            String name,
            Long id,
            String sku,
            Double minPrice,
            Double maxPrice,
            BigDecimal minStock,
            BigDecimal maxStock,
            Boolean active,
            Date discontinuedAt
    ) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();

            // name contains ignore case
            if (hasText(name)) {
                String like = "%" + name.trim().toLowerCase() + "%";
                p.add(cb.like(cb.lower(root.get("name")), like));
            }

            if (id != null) {
                p.add(cb.equal(root.get("id"), id));
            }

            if (hasText(sku)) {
                p.add(cb.equal(cb.lower(root.get("sku")), sku.trim().toLowerCase()));
            }

            if (minPrice != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                p.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (minStock != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("stock"), minStock));
            }
            if (maxStock != null) {
                p.add(cb.lessThanOrEqualTo(root.get("stock"), maxStock));
            }
            if (active != null) {
                p.add(cb.equal(root.get("active"), active));
            } else {
                p.add(cb.isTrue(root.get("active"))); // default behavior
            }
            // optional: discontinuedAt filter
            if (discontinuedAt != null) {
                p.add(cb.lessThanOrEqualTo(root.get("discontinuedAt"), discontinuedAt));
            }

            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }
}

