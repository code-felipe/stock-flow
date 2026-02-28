package com.stockflow.backend.store.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockflow.backend.inventory.dto.InventorySummaryDTO;
import com.stockflow.backend.inventory.service.IInventoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/stores")
@Tag(name = "Stores", description = "Store endpoints")
public class StoreRestController {
	
	@Autowired
	private IInventoryService inventoryService;
	
	@GetMapping("/{storeId}/inventory-summary")
	@Operation(summary = "Inventory summary by store",
	               description = "Returns all products with onHand/reserved for a given store. Products without inventory return 0.")
	public ResponseEntity<List<InventorySummaryDTO>> listInventorySummary(
	            @Parameter(description = "Store id", example = "1")
	            @PathVariable Long storeId
	    ) {
	        return ResponseEntity.ok(inventoryService.list(storeId));
	    }
}
