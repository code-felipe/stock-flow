package com.stockflow.backend.store.service;

import com.stockflow.backend.store.dto.StoreSummaryDTO;

public interface IStoreService {
	
	public StoreSummaryDTO findById(Long id);
}
