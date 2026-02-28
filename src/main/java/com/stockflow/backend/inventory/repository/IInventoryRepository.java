package com.stockflow.backend.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stockflow.backend.inventory.domain.Inventory;
import com.stockflow.backend.inventory.domain.InventoryId;
import com.stockflow.backend.product.dto.summary.ProductStockView;

public interface IInventoryRepository extends JpaRepository<Inventory, InventoryId>, JpaSpecificationExecutor<Inventory>{
	
	@Query(value = """
		    SELECT
		      p.id   AS productId,
		      p.name AS productName,
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
		List<ProductStockView> findProductStockByStore(@Param("storeId") Long storeId);
}
