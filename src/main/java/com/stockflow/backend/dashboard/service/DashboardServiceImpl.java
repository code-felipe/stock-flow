package com.stockflow.backend.dashboard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.stockflow.backend.dashboard.dto.LowStockProductDTO;
import com.stockflow.backend.dashboard.dto.OrdersByMonthDTO;
import com.stockflow.backend.dashboard.repository.IDashboardRepository;
import com.stockflow.backend.order.enumerate.OrderStatus;

@Service
public class DashboardServiceImpl implements IDashboardService {
	
	@Autowired
	private IDashboardRepository dashRepo;
	
	@Override
	public List<OrdersByMonthDTO> getOrdersByMonth(int year) {
		// TODO Auto-generated method stub
		return dashRepo.countOrdersByMonth(year);
	}

	@Override
	public Double getAvgOrder(int year, OrderStatus status) {
		
		Double average = dashRepo.getAverageOrderValue(year, OrderStatus.CONFIRMED);
		
		return average != null ? average : 0.0;
	}

	@Override
	public Long getProductsSold(int year, OrderStatus status) {
		
		Long totalSold = dashRepo.getProductsSold(year, OrderStatus.CONFIRMED);
		
		return totalSold != null ? totalSold : 0;
	}

	@Override
	public List<LowStockProductDTO> getLowStockProducts() {
		 return dashRepo.findLowStockProducts(
			        PageRequest.of(0, 10)
			    );
	}

}
