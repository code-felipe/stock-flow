package com.stockflow.backend.inventory.dto.delete;

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
public class InventoryDeleteResponseDTO {
	
	@Schema(description = "Product id", example = "1")
	private Long productId;
	
	@Schema(description = "Store id", example = "1")
	private Long storeId;

	@Schema(description = "Inventory stock on hand for the specific product", example = "35")	
	private Integer onHand;
	
	@Schema(description = "Inventory stock on reserved for the specific product", example = "3")
	private Integer reserved;
}
