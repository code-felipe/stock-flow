package com.stockflow.backend.inventory.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/store/inventory")
@Tag(name = "Inventories", description = "Endpoints for inventories in store")
public class InventoryRestController {
	

}
