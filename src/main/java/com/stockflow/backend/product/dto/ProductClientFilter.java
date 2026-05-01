package com.stockflow.backend.product.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductClientFilter {

    @Schema(description = "Filter by name (contains, ignore case)", example = "ring")
    private String name;

    @Schema(description = "Minimum price", example = "5.00")
    private Double minPrice;

    @Schema(description = "Maximum price", example = "50.00")
    private Double maxPrice;

    @Schema(description = "Minimum stock", example = "1")
    private BigDecimal minStock;

    @Schema(description = "Maximum stock", example = "100")
    private BigDecimal maxStock;
    
    @Schema(description = "Filter by category name (exact match, ignore case)", example = "Rings")
    private String category;
    
    
}
