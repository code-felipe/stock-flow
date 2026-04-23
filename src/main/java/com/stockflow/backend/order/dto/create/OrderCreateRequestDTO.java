package com.stockflow.backend.order.dto.create;

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
@Schema(name = "OrderCreateRequestDTO", description = "DTO Request for create the Order")
public class OrderCreateRequestDTO {
	
	@Schema(description = "The snapshot time when the order is persists", example = "2026-04-22")
	private Instant orderDate;
	
	@Schema(description = "Tracks the status of the order", example = "CANCELLED - APROVE")
	private String orderStatus;
	
	@Schema(description = "The total price of all items", example = "$20")
	private Double total;
	
	@Schema(description = "The order belongs to that store id", example = "2")
	private Long storeId;
}
