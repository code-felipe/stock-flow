package com.stockflow.backend.product.controller;

import com.stockflow.backend.product.dto.ProductDTO;
import com.stockflow.backend.product.dto.ProductFilter;
import com.stockflow.backend.product.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;

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
    private IProductService service;

    @GetMapping
    @Operation(
            summary = "Get products",
            description = "Paginated product list with optional name search; sorted by createdAt (desc)."
    )
    public ResponseEntity<Page<ProductDTO>> list(
    		@ModelAttribute ProductFilter filter,
            @Parameter(description = "Optional search by product name", example = "ring")
            @RequestParam(required = false) String search,

            @Parameter(description = "Zero-based page index", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
    	
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ProductDTO> result = service.findProducts(filter, pageable);
        return ResponseEntity.ok(result);
    }
    
    
    @GetMapping("/{id}")
    @Operation(
            summary = "Get product by id",
            description = "Find a product by its id."
    )
    public ResponseEntity<ProductDTO> byId(
            @Parameter(description = "Product id", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }
    
    @PostMapping
    @Operation(
            summary = "Create product",
            description = "Create a new product"
    )
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO dto){
    	ProductDTO product = service.createProduct(dto);
    	
    	return ResponseEntity.created(URI.create("/api/products"+product.getId())).body(product);
    }
    
    // Soft-deletion
    @PatchMapping("/{id}/discontinue")
    @Operation(
            summary = "Discontinue product",
            description = "Discontinue a product from its id"
    )
    public ResponseEntity<ProductDTO> discontinue(@PathVariable Long id) {
        ProductDTO updated = service.discontinueProduct(id);
        return ResponseEntity.ok(updated);
    }
    
    // Restauration
    @PatchMapping("/{id}/restore")
    @Operation(
            summary = "Restore product",
            description = "Restore a product from its id"
    )
    public ResponseEntity<ProductDTO> restore(@PathVariable Long id) {
        ProductDTO updated = service.restore(id);
        return ResponseEntity.ok(updated);
    }
    
    @PutMapping("/{id}")
    @Operation(
            summary = "Edit product",
            description = "Edit a product from its id"
    )
    public ResponseEntity<ProductDTO> updateProduct(
    		@PathVariable Long id,
    		@RequestBody ProductDTO dto) {
    	
        return ResponseEntity.ok(service.updateProduct(id, dto));
    }

}
    
    
    
    //=== Old findProducts version without filter ===
//    @GetMapping
//    @Operation(
//            summary = "Get products",
//            description = "Paginated product list with optional name search; sorted by createdAt (desc)."
//    )
//    public ResponseEntity<Page<ProductDTO>> list(
//            @Parameter(description = "Optional search by product name", example = "ring")
//            @RequestParam(required = false) String search,
//
//            @Parameter(description = "Zero-based page index", example = "0")
//            @RequestParam(defaultValue = "0") int page,
//
//            @Parameter(description = "Page size", example = "10")
//            @RequestParam(defaultValue = "10") int size
//    ) {
    	
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//        Page<ProductDTO> result = service.findProducts(search, pageable);
//        return ResponseEntity.ok(result);
//    }


