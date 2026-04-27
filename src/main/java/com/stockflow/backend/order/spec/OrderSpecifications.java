package com.stockflow.backend.order.spec;


import com.stockflow.backend.order.domain.Order;
import com.stockflow.backend.product.domain.Product;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderSpecifications {

    public static Specification<Order> withFilters(
            String orderStatus,
            Instant dateFrom,
            Instant dateTo,
            Double totalMin,
            Double totalMax
    ) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();

            // name contains ignore case
            if (hasText(orderStatus)) {
                String like = "%" + orderStatus.trim().toLowerCase() + "%";
                p.add(cb.like(cb.lower(root.get("orderStatus")), like));
            }

            if (dateFrom != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("orderDate"), dateFrom));
            }
            if (dateTo != null) {
                p.add(cb.lessThanOrEqualTo(root.get("orderDate"), dateTo));
            }
            
            if (totalMin != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("total"), totalMin));
            }
            if (totalMax != null) {
                p.add(cb.lessThanOrEqualTo(root.get("total"), totalMax));
            }
            
          

            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }
}

