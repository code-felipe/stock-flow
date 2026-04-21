package com.stockflow.backend.product.controller;

import com.stockflow.backend.product.dto.ProductFilter;
import com.stockflow.backend.product.dto.create.ProductCreateRequestDTO;
import com.stockflow.backend.product.dto.create.ProductCreateResponseDTO;
import com.stockflow.backend.product.dto.detail.ProductDetailDTO;
import com.stockflow.backend.product.dto.summary.ProductSummaryDTO;
import com.stockflow.backend.product.dto.update.ProductUpdateRequestDTO;
import com.stockflow.backend.product.dto.update.ProductUpdateResponseDTO;
import com.stockflow.backend.product.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Endpoints for product catalog and inventory")
public class ProductRestController {

    @Autowired
    private IProductService productService;

    @GetMapping
    @Operation(
            summary = "Get products",
            description = "Paginated product list with optional name search; sorted by createdAt (desc)."
    )
    public ResponseEntity<Page<ProductSummaryDTO>> list(
    		@ModelAttribute ProductFilter filter,
            @Parameter(description = "Global search box (free-text). By default, it searches product name (case-insensitive, partial match). If 'name' is provided in ProductFilter, it takes precedence.", example = "ring")
            @RequestParam(required = false) String search,
            
            @Parameter(description = "Optional search by product category name", example = "Rings")
            @RequestParam(required = false) String category,

            @Parameter(description = "Zero-based page index", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
    	
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        if (search != null && !search.isBlank() && (filter.getName() == null || filter.getName().isBlank())) {
            filter.setName(search);
        }
        if(category != null && !category.isBlank() && (filter.getCategory() == null || filter.getCategory().isBlank())) {
        	filter.setCategory(category);
        }
//        Page<ProductSummaryDTO> result;
//        if (category != null && !category.isBlank()) {
//            result = categoryService.findProductsByCategoryNameIgnoreCase(category, pageable);
//        } else {
//            result = productService.findProducts(filter, pageable);
//        }
        // Specification is now in use.
        Page<ProductSummaryDTO> result = productService.findProducts(filter, pageable);

        return ResponseEntity.ok()
            .header("X-Fields-Excluded", "categoryIds")
            .header("X-Note", "Products have categories, but categoryIds are not returned by this endpoint. Use /api/products/{id} for details.")
            .body(result);
    }
    
    
    @GetMapping("/{id}")
    @Operation(
            summary = "Get product by id",
            description = "Find a product by its id."
    )
    public ResponseEntity<Map<String, Object>> byId(
            @Parameter(description = "Product id", example = "1")
            @PathVariable Long id
    ) {
    	ProductDetailDTO product = productService.findById(id);
    	
    	  Map<String, Object> body = new HashMap<>();
          body.put("message", "Product founded successfully");
          body.put("product", product);
          
        return ResponseEntity.ok().body(body);
    }
    
    @PostMapping
    @Operation(summary = "Create product", description = "Create a new product")
    public ResponseEntity<Map<String, Object>> createProduct(@Valid @RequestBody ProductCreateRequestDTO dto) {

    	ProductCreateResponseDTO created = productService.createProduct(dto);

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Product created successfully");
        body.put("product", created);

        return ResponseEntity
                .created(URI.create("/api/products/" + created.getId()))
                .body(body);
    }

    
    // Soft-deletion
    @PatchMapping("/{id}/discontinue")
    @Operation(
            summary = "Discontinue product",
            description = "Discontinue a product from its id"
    )
    public ResponseEntity<Map<String, Object>> discontinue(@PathVariable Long id) {
    	
    	ProductUpdateResponseDTO updated = productService.discontinueProduct(id);
        
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Product have been discontinued successfully");
        body.put("product", updated);
        
        
        return ResponseEntity.ok().body(body);
    }
    
    // Restauration
    @PatchMapping("/{id}/restore")
    @Operation(
            summary = "Restore product",
            description = "Restore a product from its id"
    )
    public ResponseEntity<Map<String, Object>> restore(@PathVariable Long id) {
    	ProductUpdateResponseDTO updated = productService.restore(id);
        
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Product have been restored successfully");
        body.put("product", updated);
        
        return ResponseEntity.ok().body(body);
    }
    
    @PutMapping("/{id}")
    @Operation(
            summary = "Edit product",
            description = "Edit a product from its id"
    )
    public ResponseEntity<Map<String, Object>> updateProduct(
    		@PathVariable Long id,
    		@Valid @RequestBody ProductUpdateRequestDTO dto) {
    	
    	ProductUpdateResponseDTO updated = productService.updateProduct(id, dto);
    	
    	 Map<String, Object> body = new HashMap<>();
         body.put("message", "Product have been updated successfully");
         body.put("product", updated);
         
         return ResponseEntity.ok().body(body);
    }

}


