package com.stockflow.backend.product.service;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	
//	@Override
//	public Page<ProductDTO> findProducts(String search, Pageable pageable) {
		// TODO Auto-generated method stub
//		Page<Product> page;
//		
//		if(search == null || search.isBlank()) {
//			page = repo.findAll(pageable);
//		}else {
//			page = repo.findByNameContainingIgnoreCase(search, pageable);
//		}
//		
//		return page.map(Mapper::toDTO);
//	}
	
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
	            filter.getMaxStock() != null;

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
	                filter.getMaxStock()
	        );
	        page = repo.findAll(spec, pageable);
	    }

	    return page.map(Mapper::toDTO);
	}


	private boolean hasText(String s) {
	    return s != null && !s.trim().isEmpty();
	}


	@Override
	public ProductDTO byId(Long id) {
		// TODO Auto-generated method stub
		if(id == null) throw new IllegalArgumentException("Product ID is required");
		
		return repo.findById(id)
				.map(Mapper::toDTO)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
	}

}
