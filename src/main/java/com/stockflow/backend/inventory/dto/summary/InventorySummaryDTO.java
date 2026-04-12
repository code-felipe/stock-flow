package com.stockflow.backend.inventory.dto.summary;

import java.time.Instant;
import java.util.Date;

import com.stockflow.backend.store.dto.StoreSummaryDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class InventorySummaryDTO {
	
	@Schema(description = "Product id", example = "1")
	private Long productId;

	@Schema(description = "Product name", example = "Emeral Ring on Gold")
	@NotBlank(message = "Name is required")
	private String productName;
	
	@Schema(description = "Product description", example = "Ring gold for wedding")
	@NotBlank(message = "Description is required")
	private String productDescription;
	
	@Schema(description = "Product price", example = "$300")
	@NotBlank(message = "Price is required")
	private Double productPrice;
	
	@Schema(description = "Product image representation url", example = "https://example.com/images/gold-ring.jpg")
    @NotBlank(message = "Image is required")
    private String productImageUrl;
	
	@Schema(description = "Product sku", example = "HUJ-898-18")
	@NotBlank(message = "Sku is required")
	private String productSku;
	
	@Schema(description = "Inventory on hand", example = "300")
	@Min(0)
	private Integer onHand;
	
	@Schema(description = "Inventory reserved", example = "192")
	@Min(0)
	private Integer reserved;
	
	@Schema(description = "Store creation date", example = "2026-01-31T14:16:36Z")
    private Instant createdAt;
	
	@Schema(description = "Store update date", example = "2026-01-31T14:16:36Z")
    private Instant updatedAt;
	
	
}
