package com.stockflow.backend.order.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusRequestDTO {
	private OrderStatus status;
}
