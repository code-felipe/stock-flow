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
import com.stockflow.backend.product.dto.ProductFilter;
import com.stockflow.backend.product.dto.create.ProductCreateRequestDTO;
import com.stockflow.backend.product.dto.create.ProductCreateResponseDTO;
import com.stockflow.backend.product.dto.detail.ProductDetailDTO;
import com.stockflow.backend.product.dto.summary.ProductSummaryDTO;
import com.stockflow.backend.product.dto.update.ProductUpdateRequestDTO;
import com.stockflow.backend.product.dto.update.ProductUpdateResponseDTO;
import com.stockflow.backend.product.repository.IProductRepository;
import com.stockflow.backend.product.repository.specification.ProductSpecification;
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

		Specification<Product> spec =
                Specification.where(ProductSpecification.nameContains(filter.getName()))
                        .and(ProductSpecification.hasId(filter.getId()))
                        .and(ProductSpecification.hasSku(filter.getSku()))
                        .and(ProductSpecification.minPrice(filter.getMinPrice()))
                        .and(ProductSpecification.maxPrice(filter.getMaxPrice()))
                        .and(ProductSpecification.minStock(filter.getMinStock()))
                        .and(ProductSpecification.maxStock(filter.getMaxStock()))
                        .and(ProductSpecification.isActive(filter.getActive()))
                        .and(ProductSpecification.discontinuedBefore(filter.getDiscontinuedAt()))
						.and(ProductSpecification.hasCategory(filter.getCategory()));
		Page<Product> page = repo.findAll(spec, pageable);
		
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
	public ProductCreateResponseDTO createProduct(ProductCreateRequestDTO product) {
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
				.active(true)
				.categories(new HashSet<>(found))
				.build();
		
		Product saved = repo.save(pro);
		
		return Mapper.createProductResponse(saved);
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
	public ProductUpdateResponseDTO updateProduct(Long id, ProductUpdateRequestDTO p) {
		// TODO Auto-generated method stub
		Product product = repo.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Product not found with id: " + id));
		
		this.applyUpdates(product, p);
		
		return Mapper.toUpdateDTO(repo.save(product));
	}
	
	
	private void applyUpdates(Product product, ProductUpdateRequestDTO dto) {

		
	    if (hasText(dto.getName())) {
	        product.setName(dto.getName().trim());
	    }
	    if (hasText(dto.getDescription())) {
	        product.setDescription(dto.getDescription().trim());
	    }
	    if (dto.getPrice() != null) {
	        product.setPrice(dto.getPrice());
	    }
	    
	    if (hasText(dto.getImageUrl())) {
	        product.setImageUrl(dto.getImageUrl().trim());
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
