package com.stockflow.backend.product.dto.update;

import java.util.Date;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "ProductUpdateResponseDTO", description = "DTO that represents a product and its objects related on update")
public class ProductUpdateResponseDTO {

    @Schema(description = "Product id", example = "1")
    private Long id;

    @Schema(description = "Product name", example = "Gold Minimalist Ring")
    @NotBlank(message = "Name is required")
    private String name;
    
    @Schema(description = "Product description", example = "18k gold plated minimalist ring with smooth finish")
    @NotBlank(message = "Description is required")
    private String description;
    
    @Schema(description = "Product price", example = "4.99")
    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be >= 0")
    private Double price;
    
    @Schema(description = "Product SKU - Unique", example = "Unique product identifier JWL-DUAL-020")
	private String sku;

    @Schema(description = "Product image representation url", example = "https://example.com/images/gold-ring.jpg")
    @NotBlank(message = "Image is required")
    private String imageUrl;
    
    @Schema(description = "Enable - Disable for catalog", example = "false")
    private Boolean active;
    @Schema(description = "The time when the product was discontinued", example = "2026-01-31T14:16:36Z")
    private Date discontinuedAt;
    
    @NotEmpty(message = "At least one category is required")
    private Set<Long> categoryIds;

}

