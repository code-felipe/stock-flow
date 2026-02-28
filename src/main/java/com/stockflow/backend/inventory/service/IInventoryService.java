package com.stockflow.backend.inventory.service;

import java.util.List;

import com.stockflow.backend.inventory.dto.InventorySummaryDTO;

public interface IInventoryService {
	public List<InventorySummaryDTO> list(Long storeId);
}
