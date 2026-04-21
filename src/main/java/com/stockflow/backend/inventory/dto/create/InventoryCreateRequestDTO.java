package com.stockflow.backend.inventory.dto.create;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Min;

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
@Schema(name = "InventoryCreateRequestDTO", description = "DTO Request that represents only the necesary attributes for a inventoryo on create")
public class InventoryCreateRequestDTO {
	
	@Schema(description = "Inventory stock on hand for the specific product", example = "35")
	@Min(0)
	private Integer onHand;
	
	@Schema(description = "Inventory stock on reserved for the specific product", example = "3")
	@Min(0)
	private Integer reserved;
	
}
