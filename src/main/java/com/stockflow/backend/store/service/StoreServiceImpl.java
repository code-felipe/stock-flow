package com.stockflow.backend.store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stockflow.backend.exception.ResourceNotFoundException;
import com.stockflow.backend.store.dto.StoreSummaryDTO;
import com.stockflow.backend.store.repository.IStoreRepository;
import com.stockflow.backend.utils.mapper.Mapper;

@Service
public class StoreServiceImpl implements IStoreService{
	
	@Autowired
	private IStoreRepository storeRepo;
	
	@Override
	public StoreSummaryDTO findById(Long id) {
		// TODO Auto-generated method stub
		if(id == null) throw new IllegalArgumentException("Store ID is required");
		
		return storeRepo.findById(id)
				.map(Mapper::toSummaryDTO)
				.orElseThrow(()-> new ResourceNotFoundException("Store not found with id: " + id));
	}

}
