package com.stockflow.backend.dashboard.service;

import java.util.List;

import com.stockflow.backend.dashboard.dto.LowStockProductDTO;
import com.stockflow.backend.dashboard.dto.OrdersByMonthDTO;
import com.stockflow.backend.order.enumerate.OrderStatus;

public interface IDashboardService {
	
	public List<OrdersByMonthDTO> getOrdersByMonth(int year);
	
	public Double getAvgOrder(int year, OrderStatus status);
	
	public Long getProductsSold(int year, OrderStatus status);
	
	public List<LowStockProductDTO> getLowStockProducts();
	
	public Double totalRevenueByYear(int year);
	
	public Long healthyProducts();
	
	public List<Integer> findAvailableSalesYears();

}
