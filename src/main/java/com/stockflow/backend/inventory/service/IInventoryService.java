package com.stockflow.backend.inventory.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.stockflow.backend.inventory.dto.create.InventoryCreateRequestDTO;
import com.stockflow.backend.inventory.dto.create.InventoryCreateResponseDTO;
import com.stockflow.backend.inventory.dto.delete.InventoryDeleteResponseDTO;
import com.stockflow.backend.inventory.dto.summary.InventorySummaryDTO;
import com.stockflow.backend.inventory.dto.update.InventoryUpdateRequestDTO;
import com.stockflow.backend.inventory.dto.update.InventoryUpdateResponseDTO;
import com.stockflow.backend.product.dto.ProductFilter;
import com.stockflow.backend.product.dto.summary.ProductStockDTO;



public interface IInventoryService {

	public Page<ProductStockDTO> findStockByStore(Long storeId, ProductFilter filter, Pageable pageable);
	public InventoryCreateResponseDTO createInventory(Long storeId, Long productId, InventoryCreateRequestDTO inventory);
	public InventoryUpdateResponseDTO updateInventory(Long storeId, Long productId, InventoryUpdateRequestDTO inventory);
	public InventoryDeleteResponseDTO deleteInventory(Long storeId, Long productId);
}
