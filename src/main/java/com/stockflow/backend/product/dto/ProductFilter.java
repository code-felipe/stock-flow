package com.stockflow.backend.product.dto;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductFilter {

    @Schema(description = "Filter by product id (exact match)", example = "1")
    private Long id;

    @Schema(description = "Filter by name (contains, ignore case)", example = "ring")
    private String name;

    @Schema(description = "Filter by sku (exact match, ignore case)", example = "JWL-RNG-001")
    private String sku;

    @Schema(description = "Minimum price", example = "5.00")
    private Double minPrice;

    @Schema(description = "Maximum price", example = "50.00")
    private Double maxPrice;

    @Schema(description = "Minimum stock", example = "1")
    private BigDecimal minStock;

    @Schema(description = "Maximum stock", example = "100")
    private BigDecimal maxStock;
    
    @Schema(description = "Enable - Disable product activity", example = "true")
    private Boolean active;
    
    @Schema(description = "The product discontinuity is tracked automatically", example = "2026-01-31T14:16:36Z")
    private Date discontinuedAt;
    
    
}
