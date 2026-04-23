package com.stockflow.backend.inventory.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.stockflow.backend.category.domain.Category;
import com.stockflow.backend.category.repository.ICategoryRepository;
import com.stockflow.backend.exception.DuplicateResourceException;
import com.stockflow.backend.exception.ResourceNotFoundException;
import com.stockflow.backend.inventory.domain.Inventory;
import com.stockflow.backend.inventory.domain.InventoryId;
import com.stockflow.backend.inventory.dto.create.InventoryCreateRequestDTO;
import com.stockflow.backend.inventory.dto.create.InventoryCreateResponseDTO;
import com.stockflow.backend.inventory.dto.delete.InventoryDeleteResponseDTO;
import com.stockflow.backend.inventory.dto.summary.InventorySummaryDTO;
import com.stockflow.backend.inventory.dto.update.InventoryUpdateRequestDTO;
import com.stockflow.backend.inventory.dto.update.InventoryUpdateResponseDTO;
import com.stockflow.backend.inventory.repository.IInventoryRepository;
import com.stockflow.backend.product.domain.Product;
import com.stockflow.backend.product.dto.ProductFilter;
import com.stockflow.backend.product.dto.detail.ProductDetailDTO;
import com.stockflow.backend.product.dto.summary.ProductStockDTO;
import com.stockflow.backend.product.dto.summary.ProductStockView;
import com.stockflow.backend.product.dto.summary.ProductSummaryDTO;
import com.stockflow.backend.product.repository.IProductRepository;
import com.stockflow.backend.product.repository.specification.ProductStockSpecification;
import com.stockflow.backend.product.service.IProductService;
import com.stockflow.backend.product.spec.ProductSpecifications;
import com.stockflow.backend.store.domain.Store;
import com.stockflow.backend.store.dto.StoreSummaryDTO;
import com.stockflow.backend.store.repository.IStoreRepository;
import com.stockflow.backend.store.service.IStoreService;
import com.stockflow.backend.utils.mapper.Mapper;

import jakarta.persistence.EntityNotFoundException;

@Service
public class InventoryServiceImpl implements IInventoryService{
	
	@Autowired
	private IInventoryRepository inventoryRepo;
	
	@Autowired
	private IProductRepository productRepo;
	
	@Autowired
	private IProductService productService;
	
	@Autowired
	private ICategoryRepository categoryRepo;
	
	@Autowired
	private IStoreService storeService;

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

		Page<Product> page = productRepo.findAll(spec, pageable);// needs to use productService
		
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
		
		
		StoreSummaryDTO storeDTO = storeService.findById(storeId);
		
		ProductDetailDTO productDTO = productService.findById(productId);
		Product product = Mapper.toDetail(productDTO);
		
		product.setCategories(resolveCategoriesFromIds(productDTO.getCategoryIds()));
		
		Inventory inv = Inventory.builder()
				.id(invId)
				.onHand(inventoryDTO.getOnHand())
				.reserved(inventoryDTO.getReserved())
				.product(product)
				.store(Mapper.summaryEntity(storeDTO))
				.build();
		
		Inventory saved = inventoryRepo.save(inv);
		
		return Mapper.createInventoryResponse(saved);
				
	}


	@Override
	public InventoryUpdateResponseDTO updateInventory(Long storeId, Long productId,
			InventoryUpdateRequestDTO dto) {
		// TODO Auto-generated method stub

		InventoryId invId = new InventoryId(storeId, productId);

	    Inventory inv = inventoryRepo.findById(invId)
	        .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));
	    
		inv.setOnHand(dto.getOnHand());
		inv.setReserved(dto.getReserved());
		
		Inventory saved = inventoryRepo.save(inv);
		
		return Mapper.updateInventoryResponse(saved);
	}

	@Override
	public InventoryDeleteResponseDTO deleteInventory(Long storeId, Long productId) {
		InventoryId invId = new InventoryId(storeId, productId);
		
		Inventory inv = inventoryRepo.findById(invId)
				.orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));
		
		 inventoryRepo.delete(inv);

		return Mapper.deleteInventoryResponse(inv);
	}
	
	private Set<Category> resolveCategoriesFromIds(Set<Long> categoryIds) {
	    if (categoryIds == null || categoryIds.isEmpty()) return null;
	    return new HashSet<>(categoryRepo.findAllById(categoryIds));
	}


	@Override
	public Inventory findById(Long storeId, Long producId) {
		// TODO Auto-generated method stub
		InventoryId invId = new InventoryId(storeId, producId);
		
		Inventory inv = inventoryRepo.findById(invId)
				.orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));
		
		return inv;
	}

}
