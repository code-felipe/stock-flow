package com.stockflow.backend.product.repository;




import org.springframework.data.domain.Pageable;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stockflow.backend.product.domain.Product;

public interface IProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
	
	
	public Page<Product> findByNameContainingIgnoreCase(String value, Pageable pageable);
	  @Query("""
			    SELECT DISTINCT p
			    FROM Product p
			    JOIN p.categories c
			    WHERE LOWER(c.name) = LOWER(:name)
			  """)
			  Page<Product> findProductsByCategoryNameIgnoreCase(@Param("name") String name, Pageable pageable);
			  
	  /*
	  
	  @EntityGraph(attributePaths = "categories")
	  @Query("""
	    SELECT DISTINCT p
	    FROM Product p
	    JOIN p.categories c
	    WHERE LOWER(c.name) = LOWER(:name)
	  """)
	  Page<Product> findProductsByCategoryNameIgnoreCase(@Param("name") String name, Pageable pageable);
	  */
	  //Update query to avoid conflicts
	  //¿Existe un producto con este SKU, pero NO el producto con este ID?
	  public Boolean existsBySkuAndIdNot(String sku, Long id);
}
