package com.stockflow.backend.inventory.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.stockflow.backend.category.repository.ICategoryRepository;
import com.stockflow.backend.exception.DuplicateResourceException;
import com.stockflow.backend.exception.ResourceNotFoundException;
import com.stockflow.backend.inventory.domain.Inventory;
import com.stockflow.backend.inventory.domain.InventoryId;
import com.stockflow.backend.inventory.dto.InventoryCreateRequestDTO;
import com.stockflow.backend.inventory.dto.InventoryCreateResponseDTO;
import com.stockflow.backend.inventory.dto.InventorySummaryDTO;
import com.stockflow.backend.inventory.repository.IInventoryRepository;
import com.stockflow.backend.product.domain.Product;
import com.stockflow.backend.product.dto.ProductFilter;
import com.stockflow.backend.product.dto.summary.ProductStockDTO;
import com.stockflow.backend.product.dto.summary.ProductStockView;
import com.stockflow.backend.product.dto.summary.ProductSummaryDTO;
import com.stockflow.backend.product.repository.IProductRepository;
import com.stockflow.backend.product.repository.specification.ProductStockSpecification;
import com.stockflow.backend.product.spec.ProductSpecifications;
import com.stockflow.backend.store.domain.Store;
import com.stockflow.backend.store.repository.IStoreRepository;
import com.stockflow.backend.utils.mapper.Mapper;

import jakarta.persistence.EntityNotFoundException;

@Service
public class InventoryServiceImpl implements IInventoryService{
	
	@Autowired
	private IInventoryRepository inventoryRepo;
	
	@Autowired
	private IProductRepository productRepo;
	
	@Autowired
	private ICategoryRepository categoryRepo;
	
	@Autowired
	private IStoreRepository storeRepo;


	private boolean hasText(String s) {
	    return s != null && !s.trim().isEmpty();
	}

	
	// This service uses the native sql - query
	/*
	@Override
	public Page<ProductStockView> findProducts(ProductFilter filter, Long storeId, Pageable pageable) {
		if (filter == null) {
	        return inventoryRepo.findProductStockByStore(storeId, pageable);
	    }

	    boolean hasExtraFilters =
	            filter.getId() != null ||                // (nota: id en stock view no lo estás filtrando en SQL aún)
	            hasText(filter.getSku()) ||
	            filter.getMinPrice() != null ||
	            filter.getMaxPrice() != null ||
	            filter.getMinStock() != null ||
	            filter.getMaxStock() != null ||
	            filter.getActive() != null ||
	            filter.getDiscontinuedAt() != null;

	    boolean hasNameSearch = hasText(filter.getName());
	    boolean hasCategory = hasText(filter.getCategory()); // si lo agregas al filter, recomendado

	    // Caso: no hay filtros extra (solo name/category o nada)
	    if (!hasExtraFilters) {
	        // si no hay ni name ni category -> query base simple
	        if (!hasNameSearch && !hasCategory) {
	            return inventoryRepo.findProductStockByStore(storeId, pageable);
	        }

	        // si solo hay name/category -> llama query filtrada, el resto null
	        return inventoryRepo.findStoreStockWithFilters(
	                storeId,
	                hasNameSearch ? filter.getName().trim() : null,
	                null, null, null, null, null, null, null,
	                hasCategory ? filter.getCategory().trim() : null,
	                pageable
	        );
	    }

	    // Caso: hay filtros extra (name + extras + category)
	    return inventoryRepo.findStoreStockWithFilters(
	            storeId,
	            hasNameSearch ? filter.getName().trim() : null,
	            hasText(filter.getSku()) ? filter.getSku().trim() : null,
	            filter.getMinPrice(),
	            filter.getMaxPrice(),
	            filter.getMinStock(),
	            filter.getMaxStock(),
	            filter.getActive(),
	            filter.getDiscontinuedAt(),
	            hasCategory ? filter.getCategory().trim() : null,
	            pageable
	    );
	}
*/
	
	// uses specification
	@Override
	public Page<ProductStockDTO> findStockByStore(Long storeId, ProductFilter filter, Pageable pageable) {
		// TODO Auto-generated method stub
		Specification<Product> spec =
		        Specification.where(ProductStockSpecification.forStore(storeId, filter.getMinStock(), filter.getMaxStock()))
		            .and(ProductStockSpecification.nameContains(filter.getName()))
		            .and(ProductStockSpecification.hasCategory(filter.getCategory()))
		            .and(ProductStockSpecification.minPrice(filter.getMinPrice()))
		            .and(ProductStockSpecification.maxPrice(filter.getMaxPrice()));

		Page<Product> page = productRepo.findAll(spec, pageable);
		
		return page.map(product -> {
	        // busca el inventory de este producto para este store específico
	        Inventory inv = product.getInventories().stream()
	            .filter(i -> i.getId().getStoreId().equals(storeId))
	            .findFirst()
	            .orElse(null);
	        
	        return Mapper.toSummaryDTO(product, inv);
	        });
	}


	@Override
	public InventoryCreateResponseDTO createInventory(Long storeId, Long productId,
			InventoryCreateRequestDTO inventoryDTO) {
		// TODO Auto-generated method stub
		
		InventoryId invId = new InventoryId(storeId, productId);
		
		// validates duplicates between store id and product id
		if(inventoryRepo.existsById(invId)) {
			throw new DuplicateResourceException(
					"Inventory already exists for store: " + storeId + 
					" and product: " + productId);
		}
		
		
		Store store = storeRepo.findById(storeId)
		        .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

		Product product = productRepo.findById(productId)
		        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
		
		Inventory inv = Inventory.builder()
				.id(invId)
				.onHand(inventoryDTO.getOnHand())
				.reserved(inventoryDTO.getReserved())
				.product(product)
				.store(store)
				.build();
		
		Inventory saved = inventoryRepo.save(inv);
		
		return Mapper.createInventoryResponse(saved);
				
	}


}
