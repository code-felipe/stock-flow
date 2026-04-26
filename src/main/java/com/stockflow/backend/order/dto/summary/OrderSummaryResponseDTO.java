package com.stockflow.backend.order.dto.summary;

import java.time.Instant;
import java.util.Set;

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
@Schema(name = "OrderSummaryResponseDTO", description = "DTO Response that sumarize the order")
public class OrderSummaryResponseDTO {
	
	@Schema(description = "Order id", example = "1")
	private Long id;
	
	@Schema(description = "The snapshot time when the order is persists", example = "2026-04-22")
	private Instant orderDate;
	
	@Schema(description = "Tracks the status of the order", example = "CANCELLED - APROVE")
	private String orderStatus;
	
	@Schema(description = "The total price of all items", example = "$20")
	private Double total;
	
	@Schema(description = "Store name", example = "Dedicioso")
	private String storeName;
	
	@Schema(description = "Store address description", example = "248 Cooper chase ct")
	private String storeAddress;
}
