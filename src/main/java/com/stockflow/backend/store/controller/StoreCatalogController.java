package com.stockflow.backend.store.controller;


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

import com.stockflow.backend.inventory.service.IInventoryService;
import com.stockflow.backend.product.dto.ProductClientFilter;
import com.stockflow.backend.product.dto.ProductFilter;
import com.stockflow.backend.product.dto.summary.ProductStockDTO;

import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/catalog/stores")
@Tag(name = "Stores", description = "Store catalog endpoints")
public class StoreCatalogController {
	
	@Autowired
	private IInventoryService inventoryService;
	
	@GetMapping("/{storeId}/stock")
	public ResponseEntity<Page<ProductStockDTO>> listStock(
	        @PathVariable Long storeId,
	        @ModelAttribute ProductClientFilter filter,
	        @RequestParam(required = false) String search,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size
	) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (hasText(search) && !hasText(filter.getName())) {
            filter.setName(search);
        }
        
        
        Page<ProductStockDTO> result = inventoryService.findStockByStore(storeId, filter, pageable);
        
        return ResponseEntity.ok(result);
    }
	
	
	private boolean hasText(String s) {
	    return s != null && !s.trim().isEmpty();
	}
}
