package com.stockflow.backend.product.repository;




import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.stockflow.backend.product.domain.Product;

public interface IProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
	
	public Page<Product> findByNameContainingIgnoreCase(String value, Pageable pageable);

}
