package com.stockflow.backend.order.dto.create;

import java.time.Instant;
import java.util.Set;

import com.stockflow.backend.orderItem.dto.create.OrderItemResponseDTO;

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
@Schema(name = "OrderCreateRequestDTO", description = "DTO Response that represents the receipt")
public class OrderCreateResponsetDTO {
	
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
	
	@Schema(description = "OrderItems id's where can also reference Iventory - Product", example = "1 - 2 - 3")
	private Set<OrderItemResponseDTO> items;

}
