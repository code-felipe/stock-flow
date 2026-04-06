package com.stockflow.backend.inventory.dto;

import java.time.Instant;
import java.util.Date;

import com.stockflow.backend.store.dto.StoreSummaryDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
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
public class InventoryCreateResponseDTO {
	

	@Schema(description = "Product id", example = "1")
	private Long productId;
	@Schema(description = "Category id", example = "1")
	private Long storeId;
	@Schema(description = "Inventory stock on hand for the specific product", example = "35")
	@Min(0)
	private Integer onHand;
	@Schema(description = "Inventory stock on reserved for the specific product", example = "3")
	@Min(9)
	 private Integer reserved;

	@Schema(description = "Store creation date", example = "2026-01-31T14:16:36Z")
    private Instant createdAt;
	
	@Schema(description = "Store update date", example = "2026-01-31T14:16:36Z")
    private Instant updatedAt;
	
	
}
