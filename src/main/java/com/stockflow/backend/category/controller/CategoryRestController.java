package com.stockflow.backend.category.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.stockflow.backend.category.dto.CategoryDTO;
import com.stockflow.backend.category.service.ICategoryService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/categories")
@Tag(name = "Categories", description = "Endpoints for categories product inventory")
public class CategoryRestController {
	

    @Autowired
    private ICategoryService categoryService;
    
    
    
    @GetMapping
    @Operation(
            summary = "Get categories",
            description = "List all categories"
    )
    public ResponseEntity<List<CategoryDTO>> list() {
    
        return ResponseEntity.ok(categoryService.findCategories());
    }
    
    
    @PostMapping
    @Operation(summary = "Create category", description = "Create a new category")
    public ResponseEntity<Map<String, Object>> createCategory(@Valid @RequestBody CategoryDTO dto) {

        CategoryDTO category = categoryService.createCategory(dto);

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Category created successfully");
        body.put("category", category);

        return ResponseEntity
                .created(URI.create("/api/categories/" + category.getId()))
                .body(body);
    }
    
    @PutMapping("/{id}")
    @Operation(
            summary = "Edit category",
            description = "Edit a category from its id"
    )
    public ResponseEntity<Map<String, Object>> updateCategory(
    		@PathVariable Long id,
    		@Valid @RequestBody CategoryDTO dto) {
    	
    	CategoryDTO updated = categoryService.updateCategory(id, dto);
    	
    	 Map<String, Object> body = new HashMap<>();
         body.put("message", "Category have been updated successfully");
         body.put("category", updated);
         
         return ResponseEntity.ok().body(body);
    }
    
    @GetMapping("/{id}")
    @Operation(
            summary = "Get category by id",
            description = "Find a product by its id."
    )
    public ResponseEntity<Map<String, Object>> byId(
            @Parameter(description = "Category id", example = "1")
            @PathVariable Long id
    ) {
    	CategoryDTO category = categoryService.findById(id);
    	
    	  Map<String, Object> body = new HashMap<>();
          body.put("message", "Category founded successfully");
          body.put("category", category);
          
        return ResponseEntity.ok().body(body);
    }
    
}
