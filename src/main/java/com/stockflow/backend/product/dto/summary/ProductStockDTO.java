package com.stockflow.backend.product.dto.summary;

import java.time.Instant;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(name = "ProductStockDTO", description = "DTO that represents product - inventory")
public class ProductStockDTO {
	
	 @Schema(description = "Product id", example = "1")
	    private Long productId;

	    @Schema(description = "Product name", example = "Gold Minimalist Ring")
	    @NotBlank(message = "Name is required")
	    private String productName;

	    @Schema(description = "Product description", example = "18k gold plated minimalist ring with smooth finish")
	    @NotBlank(message = "Description is required")
	    private String productDescription;

	    @Schema(description = "Product price", example = "4.99")
	    @NotNull(message = "Price is required")
	    @PositiveOrZero(message = "Price must be >= 0")
	    private Double productPrice;

	    @Schema(description = "Product unique sku", example = "JWL-RNG-001")
	    @NotBlank(message = "SKU is required")
	    private String productSku;

	    @Schema(description = "Product image representation url", example = "https://example.com/images/gold-ring.jpg")
	    @NotBlank(message = "Image is required")
	    private String productImageUrl;

	    @Schema(description = "Product on hand quantity", example = "25.00")
	    @PositiveOrZero(message = "Products in stock - available")
	    private Integer onHand;
	    
	    @Schema(description = "Product quantity on reserved", example = "25.00")	    
	    @PositiveOrZero(message = "Products in reserved")
	    private Integer reserved;
	    
	    @Schema(description = "Enable - Disable for catalog", example = "false")
	    private Boolean productIsActive;
	    @Schema(description = "The time when the product was discontinued", example = "2026-01-31T14:16:36Z")
	    private Date ProductDiscontinuedAt;
	    
	    @Schema(description = "Product creation date", example = "2026-01-31T14:16:36Z")
	    private Date productCreatedAt;
	    @Schema(description = "The inventory - product is created at: (date)", example = "2026-01-31T14:16:36Z")
	    private Instant createdAt;
	    @Schema(description = "The inventory - product is updated at: (date)", example = "2026-01-31T14:16:36Z")
	    private Instant updatedAt;
}
