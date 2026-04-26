package com.stockflow.backend.order.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
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
@Schema(name = "Cart Item", description = "Session item")
public class CartItemRequest {
	
	@Schema(description = "Product id", example = "2")
	private Long productId;
	
	@Schema(description = "The product quantity", example = "3")
	@Min(value = 1, message = "Quantity must be atleats 1")
	private Integer quantity;
	
}
