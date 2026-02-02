package com.stockflow.backend.product.service;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stockflow.backend.exception.ResourceNotFoundException;
import com.stockflow.backend.product.domain.Product;
import com.stockflow.backend.product.dto.ProductDTO;
import com.stockflow.backend.product.dto.ProductFilter;
import com.stockflow.backend.product.mapper.Mapper;
import com.stockflow.backend.product.repository.IProductRepository;
import com.stockflow.backend.product.spec.ProductSpecifications;

@Service
public class ProductServiceImpl implements IProductService{
	
	@Autowired
	private IProductRepository repo;
	
	@Override
	public Page<ProductDTO> findProducts(ProductFilter filter, Pageable pageable) {

	    if (filter == null) {
	        return repo.findAll(pageable).map(Mapper::toDTO);
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
	        // name search + filtros
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

	    return page.map(Mapper::toDTO);
	}

	@Override
	public ProductDTO findById(Long id) {
		// TODO Auto-generated method stub
		if(id == null) throw new IllegalArgumentException("Product ID is required");
		
		return repo.findById(id)
				.map(Mapper::toDTO)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
	}


	@Override
	public ProductDTO createProduct(ProductDTO product) {
		Product pro = Product.builder()
				.name(product.getName())
				.description(product.getDescription())
				.price(product.getPrice())
				.sku(product.getSku())
				.imageUrl(product.getImageUrl())
				.stock(product.getStock())
				.active(true)
				.build();
		return Mapper.toDTO(repo.save(pro));
	}
	
	@Transactional
	@Override
	public ProductDTO discontinueProduct(Long id) {
		// TODO Auto-generated method stub
		Product product = repo.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Product not found with id:" + id));
		
		product.setActive(false);
		product.setDiscontinuedAt(new Date());
		
		return Mapper.toDTO(product);
	}
	
	@Transactional
	@Override
	public ProductDTO restore(Long id) {
		// TODO Auto-generated method stub
		Product product = repo.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Product not found with id: " + id));
		
		product.setActive(true);
		
		return Mapper.toDTO(product);
	}
	
	@Override
	public ProductDTO updateProduct(Long id, ProductDTO p) {
		// TODO Auto-generated method stub
		Product product = repo.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Product not found with id: " + id));
		
		this.applyUpdates(product, p);
		
		return Mapper.toDTO(repo.save(product));
	}
	
	private boolean hasText(String s) {
	    return s != null && !s.trim().isEmpty();
	}
	
	private void applyUpdates(Product product, ProductDTO dto) {

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
	        product.setSku(dto.getSku().trim());
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
	}


}
