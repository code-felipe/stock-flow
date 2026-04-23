package com.stockflow.backend.orderItem.dto.create;

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
@Schema(name = "OrderItemResponseDTO", description = "DTO Response that represent the lines of  the order - order items")
public class OrderItemResponseDTO {
	
	private String productName;
	
	private Integer quantity;
	
	private Double unitPrice;
	
	private Double subtotal;
}
