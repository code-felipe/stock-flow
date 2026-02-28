package com.stockflow.backend.category.service;

import org.springframework.data.domain.Pageable;

import java.util.List;

import org.springframework.data.domain.Page;


import com.stockflow.backend.category.dto.CategoryDTO;
import com.stockflow.backend.product.dto.summary.ProductSummaryDTO;


public interface ICategoryService {
	
	public List<CategoryDTO> findCategories();
	public CategoryDTO findById(Long id);
	public CategoryDTO createCategory(CategoryDTO category);
	public CategoryDTO discontinueCategory(Long id); 
	public CategoryDTO restore(Long id);
	public CategoryDTO updateCategory(Long id, CategoryDTO category);
	public Page<ProductSummaryDTO> findProductsByCategoryNameIgnoreCase(String name, Pageable pageable);
}
