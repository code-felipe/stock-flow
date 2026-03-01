package com.stockflow.backend.store.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockflow.backend.inventory.dto.InventorySummaryDTO;
import com.stockflow.backend.inventory.service.IInventoryService;
import com.stockflow.backend.product.dto.ProductFilter;
import com.stockflow.backend.product.dto.summary.ProductStockView;
import com.stockflow.backend.utils.mapper.Mapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/stores")
@Tag(name = "Stores", description = "Store endpoints")
public class StoreRestController {
	
	@Autowired
	private IInventoryService inventoryService;

	
	@GetMapping("/{storeId}/stock")
	public ResponseEntity<Page<InventorySummaryDTO>> listStock(
	        @PathVariable Long storeId,
	        @ModelAttribute ProductFilter filter,
	        @RequestParam(required = false) String search,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size
	) {
	    Pageable pageable = PageRequest.of(page, size);

	    if (hasText(search) && !hasText(filter.getName())) {
	        filter.setName(search);
	    }

	    Page<ProductStockView> pg = inventoryService.findProducts(filter, storeId, pageable);
	    return ResponseEntity.ok(pg.map(Mapper::toSummaryDTO));
	}
	
	private boolean hasText(String s) {
	    return s != null && !s.trim().isEmpty();
	}
}
