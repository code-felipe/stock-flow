package com.stockflow.backend.inventory.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stockflow.backend.inventory.domain.Inventory;
import com.stockflow.backend.inventory.domain.InventoryId;
import com.stockflow.backend.product.dto.summary.ProductStockView;

public interface IInventoryRepository extends JpaRepository<Inventory, InventoryId>, JpaSpecificationExecutor<Inventory>{
	
	@Query(
			  value = """
			    SELECT
			      p.id            AS productId,
			      p.name          AS productName,
			      p.description   AS productDescription,
			      p.price         AS productPrice,
			      p.image_url     AS productImageUrl,
			      p.sku           AS productSku,
			      COALESCE(i.on_hand, 0)    AS onHand,
			      COALESCE(i.reserved, 0)   AS reserved,
			      i.created_at    AS createdAt,
			      i.updated_at    AS updatedAt
			    FROM products p
			    LEFT JOIN inventories i
			      ON i.product_id = p.id
			     AND i.store_id   = :storeId
			    WHERE
			      (:name IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(:name), '%'))
			      AND (:sku IS NULL OR LOWER(p.sku) LIKE CONCAT('%', LOWER(:sku), '%'))
			      AND (:minPrice IS NULL OR p.price >= :minPrice)
			      AND (:maxPrice IS NULL OR p.price <= :maxPrice)
			      AND (:minStock IS NULL OR COALESCE(i.on_hand, 0) >= :minStock)
			      AND (:maxStock IS NULL OR COALESCE(i.on_hand, 0) <= :maxStock)
			      AND (:active IS NULL OR p.active = :active)
			      AND (:discontinuedAt IS NULL OR p.discontinued_at IS NOT NULL)
			      AND (
			        :category IS NULL OR EXISTS (
			          SELECT 1
			          FROM product_categories pc
			          JOIN categories c ON c.id = pc.category_id
			          WHERE pc.product_id = p.id
			            AND LOWER(c.name) = LOWER(:category)
			        )
			      )
			    """,
			  countQuery = """
			    SELECT COUNT(*)
			    FROM products p
			    LEFT JOIN inventories i
			      ON i.product_id = p.id
			     AND i.store_id   = :storeId
			    WHERE
			      (:name IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(:name), '%'))
			      AND (:sku IS NULL OR LOWER(p.sku) LIKE CONCAT('%', LOWER(:sku), '%'))
			      AND (:minPrice IS NULL OR p.price >= :minPrice)
			      AND (:maxPrice IS NULL OR p.price <= :maxPrice)
			      AND (:minStock IS NULL OR COALESCE(i.on_hand, 0) >= :minStock)
			      AND (:maxStock IS NULL OR COALESCE(i.on_hand, 0) <= :maxStock)
			      AND (:active IS NULL OR p.active = :active)
			      AND (:discontinuedAt IS NULL OR p.discontinued_at IS NOT NULL)
			      AND (
			        :category IS NULL OR EXISTS (
			          SELECT 1
			          FROM product_categories pc
			          JOIN categories c ON c.id = pc.category_id
			          WHERE pc.product_id = p.id
			            AND LOWER(c.name) = LOWER(:category)
			        )
			      )
			    """,
			  nativeQuery = true
			)
			Page<ProductStockView> findStoreStockWithFilters(
			  @Param("storeId") Long storeId,
			  @Param("name") String name,
			  @Param("sku") String sku,
			  @Param("minPrice") Double minPrice,
			  @Param("maxPrice") Double maxPrice,
			  @Param("minStock") BigDecimal minStock,
			  @Param("maxStock") BigDecimal maxStock,
			  @Param("active") Boolean active,
			  @Param("discontinuedAt") Instant discontinuedAt,
			  @Param("category") String category,
			  Pageable pageable
			);
	
	
 	@Query(value = """
		    SELECT
		      p.id   AS productId,
		      p.name AS productName,
		      p.price AS productPrice,
		      p.image_url AS productImageUrl,
		      p.description AS productDescription,
		      p.sku  AS productSku,
		      COALESCE(i.on_hand, 0)  AS onHand,
		      COALESCE(i.reserved, 0) AS reserved,
		      i.created_at AS createdAt,
		      i.updated_at AS updatedAt
		    FROM products p
		    LEFT JOIN inventories i
		      ON i.product_id = p.id
		     AND i.store_id = :storeId
		    """, nativeQuery = true)
		Page<ProductStockView> findProductStockByStore(@Param("storeId") Long storeId, Pageable pageable);
 	
 	// it's replaced by the filter method.
	@Query(
			  value = """
			    SELECT
			      p.id          AS productId,
			      p.name        AS productName,
			      p.description AS productDescription,
			      p.price       AS productPrice,
			      p.imageUrl    AS productImageUrl,
			      p.sku         AS productSku,
			      COALESCE(i.onHand, 0)   AS onHand,
			      COALESCE(i.reserved, 0) AS reserved,
			      i.createdAt   AS createdAt,
			      i.updatedAt   AS updatedAt
			    FROM Product p
			    LEFT JOIN Inventory i
			      ON i.product = p AND i.store.id = :storeId
			    WHERE
			      (:name IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(:name), '%'))
			      AND (
			        :category IS NULL OR EXISTS (
			          SELECT 1
			          FROM p.categories c
			          WHERE LOWER(c.name) = LOWER(:category)
			        )
			      )
			    """,
			  countQuery = """
			    SELECT COUNT(p)
			    FROM Product p
			    WHERE
			      (:name IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(:name), '%'))
			      AND (
			        :category IS NULL OR EXISTS (
			          SELECT 1
			          FROM p.categories c
			          WHERE LOWER(c.name) = LOWER(:category)
			        )
			      )
			    """
			)
			Page<ProductStockView> findStockByStoreFilterByCategoryNameAndProductName(
			  @Param("storeId") Long storeId,
			  @Param("name") String name,
			  @Param("category") String category,
			  Pageable pageable
			);
	
}
