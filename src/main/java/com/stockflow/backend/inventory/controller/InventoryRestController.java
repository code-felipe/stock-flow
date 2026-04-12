package com.stockflow.backend.inventory.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockflow.backend.inventory.dto.summary.InventorySummaryDTO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/store/inventory")
@Tag(name = "Inventories", description = "Endpoints for inventories in store")
public class InventoryRestController {
	

	
}
