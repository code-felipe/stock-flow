package com.stockflow.backend.inventory.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.stockflow.backend.inventory.dto.InventoryCreateRequestDTO;
import com.stockflow.backend.inventory.dto.InventoryCreateResponseDTO;
import com.stockflow.backend.inventory.dto.InventorySummaryDTO;
import com.stockflow.backend.product.dto.ProductFilter;
import com.stockflow.backend.product.dto.summary.ProductStockDTO;



public interface IInventoryService {
//	public List<InventorySummaryDTO> list(Long storeId);
	
//	public Page<ProductStockView> findProducts(
//			Long storeId, String name, String category, Pageable pageable
//			);
	
	// uses native query
//	public Page<ProductStockView> findProducts(ProductFilter filter, Long storeId, Pageable pageable);
	public Page<ProductStockDTO> findStockByStore(Long storeId, ProductFilter filter, Pageable pageable);
//	public InventorySummaryDTO findById(Long id);
	public InventoryCreateResponseDTO createInventory(Long storeId, Long productId, InventoryCreateRequestDTO inventory);
	
	
}
