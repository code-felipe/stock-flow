package com.stockflow.backend.product.service;

import org.springframework.data.domain.Pageable;


import org.springframework.data.domain.Page;


import com.stockflow.backend.product.dto.ProductDTO;
import com.stockflow.backend.product.dto.ProductFilter;

public interface IProductService {
	
	public Page<ProductDTO> findProducts(ProductFilter filter, Pageable pageable);
	public ProductDTO findById(Long id);
	public ProductDTO createProduct(ProductDTO product);
	public ProductDTO discontinueProduct(Long id); 
	public ProductDTO restore(Long id);
	public ProductDTO updateProduct(Long id, ProductDTO product);

}
