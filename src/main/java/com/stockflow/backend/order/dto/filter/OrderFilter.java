package com.stockflow.backend.order.dto.filter;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderFilter {

	// por status — el más usado, "muéstrame solo las CANCELLED"
	@Schema(description = "Filter shows the order status", example = "CANCELLED")
    private String orderStatus;

    // por rango de fechas — "ventas de hoy", "ventas de esta semana"
	@Schema(description = "Range - Filter that star on from", example = "2026-06-26")
    private Instant dateFrom;
	@Schema(description = "Range - Filter that ends on to", example = "2026-06-26")
    private Instant dateTo;

    // por rango de total — "órdenes mayores a $100"
	@Schema(description = "Range - Filter that star on min", example = "8")
    private Double totalMin;
	@Schema(description = "Range - Filter that ends on max", example = "20")
    private Double totalMax;
    
    
}
