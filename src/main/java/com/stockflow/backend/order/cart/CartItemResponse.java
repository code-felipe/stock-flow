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
public class CartItemResponse {
	
	@Schema(description = "Product id", example = "2")
	private Long productId;
	
	@Schema(description = "Store id", example = "1")
	private Long storeId;
	
	@Schema(description = "Product name", example = "Churro de queso")
	private String productName;
	
	@Schema(description = "The product price fecth by inventory", example = "$10.00")
    @Positive(message = "Unit price must be greater than 0")
	private Double unitPrice;
	
	@Schema(description = "The product quantity", example = "3")
	@Min(value = 1, message = "Quantity must be atleats 1")
	private Integer quantity;
	
	// Present data
	public Double subTotal() {
		return this.unitPrice * this.quantity;
	}
}
