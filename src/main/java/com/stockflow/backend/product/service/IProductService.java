package com.stockflow.backend.product.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;

import com.stockflow.backend.product.dto.ProductFilter;
import com.stockflow.backend.product.dto.create.ProductCreateRequestDTO;
import com.stockflow.backend.product.dto.create.ProductCreateResponseDTO;
import com.stockflow.backend.product.dto.detail.ProductDetailDTO;
import com.stockflow.backend.product.dto.summary.ProductSummaryDTO;
import com.stockflow.backend.product.dto.update.ProductUpdateRequestDTO;
import com.stockflow.backend.product.dto.update.ProductUpdateResponseDTO;


public interface IProductService {
	
	//Summary DTO
	public Page<ProductSummaryDTO> findProducts(ProductFilter filter, Pageable pageable);
	//Summary DTO with Specification	//Detail DTO + cardinalities(category)
	public ProductDetailDTO findById(Long id);
	public ProductCreateResponseDTO createProduct(ProductCreateRequestDTO product);
	public ProductUpdateResponseDTO discontinueProduct(Long id); 
	public ProductUpdateResponseDTO restore(Long id);
	public ProductUpdateResponseDTO updateProduct(Long id, ProductUpdateRequestDTO product);

}
