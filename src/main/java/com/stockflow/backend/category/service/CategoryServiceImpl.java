package com.stockflow.backend.category.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.stockflow.backend.category.domain.Category;
import com.stockflow.backend.category.dto.CategoryDTO;
import com.stockflow.backend.category.repository.ICategoryRepository;
import com.stockflow.backend.exception.DuplicateResourceException;
import com.stockflow.backend.exception.ResourceNotFoundException;
import com.stockflow.backend.product.domain.Product;
import com.stockflow.backend.product.dto.summary.ProductSummaryDTO;
import com.stockflow.backend.product.repository.IProductRepository;
import com.stockflow.backend.utils.mapper.Mapper;

@Service
public class CategoryServiceImpl implements ICategoryService {
	
	@Autowired
	private IProductRepository productService;
	
	@Autowired
	private ICategoryRepository catRepo;
	
	
	// No need pagination due to low data
	@Override
	public List<CategoryDTO> findCategories() {
		return catRepo.findAll()
				.stream().map( Mapper::toDTO)
				.collect(Collectors.toList());
	}

	@Override
	public CategoryDTO findById(Long id) {
		// TODO Auto-generated method stub
		if(id == null) throw new IllegalArgumentException("Product ID is required");
		
		return catRepo.findById(id)
				.map(Mapper::toDTO)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
	}

	@Override
	public CategoryDTO createCategory(CategoryDTO category) {
		// TODO Auto-generated method stub
		String name = category.getName();
		if(catRepo.existsByNameIgnoreCase(name)) {
			throw new DuplicateResourceException("Category name already exists");
		}
		
		Category cat = Category.builder()
				.name(category.getName())
				.description(category.getDescription())
				.image(category.getImage())
				.build();
		
		return Mapper.toDTO(catRepo.save(cat));
	}

	@Override
	public CategoryDTO discontinueCategory(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CategoryDTO restore(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CategoryDTO updateCategory(Long id, CategoryDTO category) {
		Category cat = catRepo.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Category not found with id: " + id));
		
		this.applyUpdates(cat, category);
		
		return Mapper.toDTO(catRepo.save(cat));
	}

	@Override
	public Page<ProductSummaryDTO> findProductsByCategoryNameIgnoreCase(String name, Pageable pageable) {
		// TODO Auto-generated method stub
		if(name == null || name.isEmpty()) {
			return productService.findAll(pageable).map(Mapper::toSummaryDTO);
		}
		
		Page<Product> page = productService.findProductsByCategoryNameIgnoreCase(name, pageable);
		
		return page.map(Mapper::toSummaryDTO);
	}
	
	
	private void applyUpdates(Category category, CategoryDTO dto) {

	    if (hasText(dto.getName())) {
	    	String newName = dto.getName().trim();
	    	if(!newName.equals(category.getName())) {
	    		if(catRepo.existsByNameIgnoreCaseAndIdNot(newName,category.getId())) {
	        		 throw new IllegalArgumentException("SKU already in use: " + newName);
	    		}
	    		category.setName(newName);
	    	}
	    }
	    if (hasText(dto.getDescription())) {
	    	category.setDescription(dto.getDescription().trim());
	    }
	    if (hasText(dto.getImage())) {
	    	category.setImage(dto.getImage());
	    }

	}
	
	private boolean hasText(String s) {
	    return s != null && !s.trim().isEmpty();
	}


}
