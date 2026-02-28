package com.stockflow.backend.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.stockflow.backend.category.domain.Category;


public interface ICategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
	
	public Boolean existsByNameIgnoreCase(String name);
	
	//Update query to avoid conflicts
	//¿Existe una categoria con este SKU, pero NO la categoria con este ID?
	public Boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

}
