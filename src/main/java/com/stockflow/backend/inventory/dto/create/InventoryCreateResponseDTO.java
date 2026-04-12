package com.stockflow.backend.inventory.dto.create;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "InventoryCreateResponseDTO", description = "DTO Response that with a more detail information on create")
public class InventoryCreateResponseDTO {
	
	@Schema(description = "Product id", example = "1")
	private Long productId;
	
	@Schema(description = "Category id", example = "1")
	private Long storeId;

	@Schema(description = "Inventory stock on hand for the specific product", example = "35")	
	private Integer onHand;
	
	@Schema(description = "Inventory stock on reserved for the specific product", example = "3")
	private Integer reserved;

	@Schema(description = "Inventory creation date", example = "2026-01-31T14:16:36Z")
    private Instant createdAt;
	
	@Schema(description = "Inventory update date", example = "2026-01-31T14:16:36Z")
    private Instant updatedAt;
	
}
