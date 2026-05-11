package com.stockflow.backend.cart.dto;

import java.util.List;

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
@Schema(name = "Cart", description = "A descriptive cart with items - subtotal and total")
public class CartResponse {
	
	private List<CartItemResponse> items;
	
	private Double total;

}
