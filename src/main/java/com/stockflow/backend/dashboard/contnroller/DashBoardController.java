package com.stockflow.backend.dashboard.contnroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockflow.backend.dashboard.dto.LowStockProductDTO;
import com.stockflow.backend.dashboard.dto.OrdersByMonthDTO;
import com.stockflow.backend.dashboard.service.IDashboardService;
import com.stockflow.backend.order.enumerate.OrderStatus;

import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Endpoints for dashboard")
public class DashBoardController {
	
	@Autowired
	private IDashboardService dashboardService;
	
	@GetMapping("/orders-by-month")
	public ResponseEntity<Map<String, Object>> countOrdersByMonth(
			@RequestParam(defaultValue = "2026") int year
			) {

	    List<OrdersByMonthDTO> data = dashboardService.getOrdersByMonth(year);

	    Map<String, Object> body = new HashMap<>();

	    body.put("message", "Orders by month fetched successfully");
	    body.put("ordersByMonth", data);

	    return ResponseEntity.ok(
	            Map.of(
	                "message", "Orders by month fetched successfully",
	                "ordersByMonth", data
	            )
	        );
	}
	
	@GetMapping("/average-order-value")
	public ResponseEntity<Map<String, Object>> getAverageOrderValue(
	        @RequestParam(defaultValue = "2026") int year){

	    Double value = dashboardService.getAvgOrder(year, OrderStatus.CONFIRMED);

	    return ResponseEntity.ok(
	        Map.of(
	            "year", year,
	            "averageOrderValue", value
	        )
	    );
	}
	
	@GetMapping("/products-sold")
	public ResponseEntity<Map<String, Object>> getProductsSold(
	        @RequestParam(defaultValue = "2026") int year){

	    Long value = dashboardService.getProductsSold(year, OrderStatus.CONFIRMED);

	    return ResponseEntity.ok(
	        Map.of(
	            "year", year,
	            "productsSold", value
	        )
	    );
	}
	
	@GetMapping("/low-stock-products")
	public ResponseEntity<Map<String, Object>> getLowStockProducts(
	        @RequestParam(defaultValue = "2026") int year){

	    List<LowStockProductDTO> lowStockProducts = dashboardService.getLowStockProducts();

	    return ResponseEntity.ok(
	        Map.of(
	            "year", year,
	            "lowStockProducts", lowStockProducts
	        )
	    );
	}
	
	@GetMapping("/total-revenue")
	public ResponseEntity<Map<String, Object>> getTotalRevenueByYear(
	        @RequestParam(defaultValue = "2026") int year){

		Double revenue = dashboardService.totalRevenueByYear(year);

	    return ResponseEntity.ok(
	        Map.of(
	            "year", year,
	            "revenue", revenue
	        )
	    );
	}
	
	@GetMapping("/inventory-health")
	public ResponseEntity<Map<String, Object>> getInventoryHealth(){

		Long inventoryHealth = dashboardService.healthyProducts();

	    return ResponseEntity.ok(
	        Map.of(
	            "inventoryHealth", inventoryHealth
	        )
	    );
	}
	
	
	@GetMapping("/available-sale-years")
	public ResponseEntity<Map<String, Object>> getAvailableSaleYears(){

		List<Integer> availableSaleYears = dashboardService.findAvailableSalesYears();

	    return ResponseEntity.ok(
	        Map.of(
	            "availableSaleYears", availableSaleYears
	        )
	    );
	}
}
