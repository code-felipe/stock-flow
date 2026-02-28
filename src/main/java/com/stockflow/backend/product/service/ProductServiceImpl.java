package com.stockflow.backend.product.service;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stockflow.backend.category.domain.Category;
import com.stockflow.backend.category.repository.ICategoryRepository;
import com.stockflow.backend.exception.ResourceNotFoundException;
import com.stockflow.backend.product.domain.Product;
import com.stockflow.backend.product.dto.ProductDTO;
import com.stockflow.backend.product.dto.ProductFilter;
import com.stockflow.backend.product.dto.create.ProductCreateResponseDTO;
import com.stockflow.backend.product.dto.detail.ProductDetailDTO;
import com.stockflow.backend.product.dto.summary.ProductSummaryDTO;
import com.stockflow.backend.product.dto.update.ProductUpdateResponseDTO;
import com.stockflow.backend.product.repository.IProductRepository;
import com.stockflow.backend.product.spec.ProductSpecifications;
import com.stockflow.backend.utils.mapper.Mapper;

@Service
public class ProductServiceImpl implements IProductService{
	
	@Autowired
	private IProductRepository repo;
	
	@Autowired
	private ICategoryRepository catRepo;
	
	@Override
	public Page<ProductSummaryDTO> findProducts(ProductFilter filter, Pageable pageable) {

	    if (filter == null) {
	        return repo.findAll(pageable).map(Mapper::toSummaryDTO);
	    }

	    Page<Product> page;

	    boolean hasExtraFilters =
	            filter.getId() != null ||
	            hasText(filter.getSku()) ||
	            filter.getMinPrice() != null ||
	            filter.getMaxPrice() != null ||
	            filter.getMinStock() != null ||
	            filter.getMaxStock() != null ||
	            filter.getActive() != null ||
	            filter.getDiscontinuedAt() != null;

	    boolean hasNameSearch = hasText(filter.getName());

	    if (!hasExtraFilters) {
	        // solo name search o nada
	        if (!hasNameSearch) {
	            page = repo.findAll(pageable);
	        } else {
	            page = repo.findByNameContainingIgnoreCase(filter.getName().trim(), pageable);
	        }
	    } else {
	        // name search + filters
	        Specification<Product> spec = ProductSpecifications.withFilters(
	                filter.getName(),
	                filter.getId(),
	                filter.getSku(),
	                filter.getMinPrice(),
	                filter.getMaxPrice(),
	                filter.getMinStock(),
	                filter.getMaxStock(),
	                filter.getActive(),
	                filter.getDiscontinuedAt()
	        );
	        page = repo.findAll(spec, pageable);
	    }

	    return page.map(Mapper::toSummaryDTO);
	}

	@Override
	public ProductDetailDTO findById(Long id) {
		// TODO Auto-generated method stub
		if(id == null) throw new IllegalArgumentException("Product ID is required");
		
		return repo.findById(id)
				.map(Mapper::toDetailDTO)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
	}


	@Override
	public ProductCreateResponseDTO createProduct(ProductCreateResponseDTO product) {
		List<Category> found = catRepo.findAllById(product.getCategoryIds());
		
		  if (found.size() != product.getCategoryIds().size()) {
		        throw new ResourceNotFoundException("One or more categoryIds do not exist.");
		    }
		  
		Product pro = Product.builder()
				.name(product.getName())
				.description(product.getDescription())
				.price(product.getPrice())
				.sku(product.getSku())
				.imageUrl(product.getImageUrl())
				.stock(product.getStock())
				.active(true)
				.categories(new HashSet<>(found))
				.build();
		return Mapper.toCreateDTO(repo.save(pro));
	}
	
	@Transactional
	@Override
	public ProductUpdateResponseDTO discontinueProduct(Long id) {
		// TODO Auto-generated method stub
		Product product = repo.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Product not found with id:" + id));
		
		product.setActive(false);
		product.setDiscontinuedAt(new Date());
		
		return Mapper.toUpdateDTO(product);
	}
	
	@Transactional
	@Override
	public ProductUpdateResponseDTO restore(Long id) {
		// TODO Auto-generated method stub
		Product product = repo.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Product not found with id: " + id));
		
		product.setActive(true);
		
		return Mapper.toUpdateDTO(product);
	}
	
	@Override
	public ProductUpdateResponseDTO updateProduct(Long id, ProductUpdateResponseDTO p) {
		// TODO Auto-generated method stub
		Product product = repo.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Product not found with id: " + id));
		
		this.applyUpdates(product, p);
		
		return Mapper.toUpdateDTO(repo.save(product));
	}
	
	
	private void applyUpdates(Product product, ProductUpdateResponseDTO dto) {

		
	    if (hasText(dto.getName())) {
	        product.setName(dto.getName().trim());
	    }
	    if (hasText(dto.getDescription())) {
	        product.setDescription(dto.getDescription().trim());
	    }
	    if (dto.getPrice() != null) {
	        product.setPrice(dto.getPrice());
	    }
	    if (hasText(dto.getSku())) {
	        String newSku = dto.getSku().trim();
	        if(!newSku.equals(product.getSku())) {
	        	if(repo.existsBySkuAndIdNot(newSku, product.getId())) {
	        		 throw new IllegalArgumentException("SKU already in use: " + newSku);
	        	}
	        	product.setSku(newSku);
	        }
	        
	    }
	    
	    if (hasText(dto.getImageUrl())) {
	        product.setImageUrl(dto.getImageUrl().trim());
	    }
	    if (dto.getStock() != null) {
	        product.setStock(dto.getStock());
	    }
	    if (dto.getActive() != null) {
	        product.setActive(dto.getActive());
	    }
	    if (dto.getDiscontinuedAt() != null) {
	        product.setDiscontinuedAt(dto.getDiscontinuedAt());
	    }
	    
	    // Bring all cat ids - no duplicates.
	    Set<Long> ids = dto.getCategoryIds();
	    
	    // Get all categories 
	    List<Category> cats = catRepo.findAllById(ids);
	    
	    if (cats.size() != ids.size()) {
	        Set<Long> foundIds = cats.stream()
	                .map(Category::getId)
	                .collect(Collectors.toSet());

	        Set<Long> missing = ids.stream()
	                .filter(id -> !foundIds.contains(id))
	                .collect(Collectors.toSet());

	        throw new ResourceNotFoundException("Category not found: " + missing);
	    }
	    
	    product.getCategories().clear();
	    product.getCategories().addAll(cats);
	    
	    
	}
	
	private boolean hasText(String s) {
	    return s != null && !s.trim().isEmpty();
	}

}
