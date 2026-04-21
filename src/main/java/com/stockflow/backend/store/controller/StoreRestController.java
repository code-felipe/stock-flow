package com.stockflow.backend.store.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockflow.backend.inventory.dto.create.InventoryCreateRequestDTO;
import com.stockflow.backend.inventory.dto.create.InventoryCreateResponseDTO;
import com.stockflow.backend.inventory.dto.delete.InventoryDeleteResponseDTO;
import com.stockflow.backend.inventory.dto.summary.InventorySummaryDTO;
import com.stockflow.backend.inventory.dto.update.InventoryUpdateRequestDTO;
import com.stockflow.backend.inventory.dto.update.InventoryUpdateResponseDTO;
import com.stockflow.backend.inventory.service.IInventoryService;
import com.stockflow.backend.product.dto.ProductFilter;
import com.stockflow.backend.product.dto.create.ProductCreateRequestDTO;
import com.stockflow.backend.product.dto.summary.ProductStockDTO;
import com.stockflow.backend.product.dto.summary.ProductStockView;
import com.stockflow.backend.product.dto.update.ProductUpdateResponseDTO;
import com.stockflow.backend.utils.mapper.Mapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/stores")
@Tag(name = "Stores", description = "Store endpoints")
public class StoreRestController {
	
	@Autowired
	private IInventoryService inventoryService;

	
	@GetMapping("/{storeId}/stock")
	public ResponseEntity<Page<ProductStockDTO>> listStock(
	        @PathVariable Long storeId,
	        @ModelAttribute ProductFilter filter,
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
	
	@PostMapping("/{storeId}/product/{productId}/inventory")
	@Operation(summary = "Create inventory for product", description = "Create a new inventory")
	public ResponseEntity<Map<String, Object>> createInventory(
			@PathVariable Long storeId,
			@PathVariable Long productId,
			@RequestBody @Valid InventoryCreateRequestDTO dto
			){
		
		InventoryCreateResponseDTO created = inventoryService.createInventory(storeId, productId, dto);
		
		Map<String, Object> body = new HashMap<>();
		body.put("message", "Inventory created successfully");
		body.put("inventory", created);
		
		return ResponseEntity
				.created(URI.create("/api/stores/" + storeId + "/product/" + productId + "/inventory"))
				.body(body);
	
	}
	
	
	@PutMapping("/{storeId}/product/{productId}/inventory")
    @Operation(
            summary = "Edit inventory",
            description = "Edit a inventory from its id"
    )
    public ResponseEntity<Map<String, Object>> updateInventory(
    		@PathVariable Long storeId,
    		@PathVariable Long productId,
    		@Valid @RequestBody InventoryUpdateRequestDTO  dto) {
    	
		InventoryUpdateResponseDTO updated = inventoryService.updateInventory(storeId, productId, dto);
    	
    	 Map<String, Object> body = new HashMap<>();
         body.put("message", "Inventory have been updated successfully");
         body.put("inventory", updated);
         
         return ResponseEntity.ok().body(body);
    }
	@DeleteMapping("/{storeId}/product/{productId}/inventory")
    @Operation(
            summary = "Delete inventory",
            description = "Delete a inventory from combined 'store-product' id's"
    )
    public ResponseEntity<Map<String, Object>> deleteInventory(
    		@PathVariable Long storeId,
    		@PathVariable Long productId) {
    	
		InventoryDeleteResponseDTO delete = inventoryService.deleteInventory(storeId, productId);
    	
    	 Map<String, Object> body = new HashMap<>();
         body.put("message", "Inventory have been deleted successfully");
         body.put("inventory", delete);
         
         return ResponseEntity.ok().body(body);
    }
		
	private boolean hasText(String s) {
	    return s != null && !s.trim().isEmpty();
	}
}
