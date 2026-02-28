package com.stockflow.backend.inventory.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stockflow.backend.inventory.dto.InventorySummaryDTO;
import com.stockflow.backend.inventory.repository.IInventoryRepository;
import com.stockflow.backend.utils.mapper.Mapper;

@Service
public class InventoryServiceImpl implements IInventoryService{
	
	@Autowired
	private IInventoryRepository inventoryRepo;
	
	@Override
	public List<InventorySummaryDTO> list(Long storeId) {
		// TODO Auto-generated method stub
		return inventoryRepo.findProductStockByStore(storeId).stream()
			    .map(Mapper::toSummaryDTO)
			    .toList();
	}

}
