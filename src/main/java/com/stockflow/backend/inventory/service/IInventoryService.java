package com.stockflow.backend.inventory.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.stockflow.backend.inventory.dto.InventorySummaryDTO;
import com.stockflow.backend.product.dto.ProductFilter;
import com.stockflow.backend.product.dto.summary.ProductStockDTO;
import com.stockflow.backend.product.dto.summary.ProductStockView;
import com.stockflow.backend.product.dto.summary.ProductSummaryDTO;

public interface IInventoryService {
//	public List<InventorySummaryDTO> list(Long storeId);
	
//	public Page<ProductStockView> findProducts(
//			Long storeId, String name, String category, Pageable pageable
//			);
	
	public Page<ProductStockView> findProducts(ProductFilter filter, Long storeId, Pageable pageable);
	public Page<ProductStockDTO> findStockByStore(Long storeId, ProductFilter filter, Pageable pageable);
}
