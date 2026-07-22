package com.stockflow.backend.dashboard.dto;

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
@Schema(name = "OrderDashByMonth", description = "DTO that fetchs all orders by month")
public class OrdersByMonthDTO {
	
	int year;
	int month;
	long total;
	double totalSales;
}
