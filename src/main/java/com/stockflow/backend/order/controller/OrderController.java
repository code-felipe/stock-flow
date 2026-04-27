package com.stockflow.backend.order.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stockflow.backend.inventory.domain.Inventory;
import com.stockflow.backend.inventory.domain.InventoryId;
import com.stockflow.backend.inventory.service.IInventoryService;
import com.stockflow.backend.order.cart.CartItemRequest;
import com.stockflow.backend.order.cart.CartItemResponse;
import com.stockflow.backend.order.dto.create.OrderCreateResponsetDTO;
import com.stockflow.backend.order.dto.filter.OrderFilter;
import com.stockflow.backend.order.dto.summary.OrderDetailedResponseDTO;
import com.stockflow.backend.order.dto.summary.OrderSummaryResponseDTO;
import com.stockflow.backend.order.service.IOrderService;
import com.stockflow.backend.product.dto.ProductFilter;
import com.stockflow.backend.product.dto.summary.ProductStockDTO;
import com.stockflow.backend.store.dto.StoreSummaryDTO;
import com.stockflow.backend.store.service.IStoreService;
import com.stockflow.backend.utils.mapper.Mapper;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/store")
@Tag(name = "Order", description = "Endpoints for Order")
public class OrderController {
	
	@Autowired
	private IStoreService storeService;
	
	@Autowired
	private IOrderService orderService;
	
	
	@GetMapping("/{storeId}/orders")
	public ResponseEntity<Map<String, Object>> findAllOrders(
	        @PathVariable Long storeId,
	        @ModelAttribute OrderFilter filter,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size
	) {
	    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));

	    Page<OrderSummaryResponseDTO> result = orderService.findAllOrdersByStoreId(storeId, filter, pageable);

	    Map<String, Object> body = new HashMap<>();
	    body.put("message", "All orders were successfully fetched");
	    body.put("orders", result);

	    return ResponseEntity.ok(body);
	}
	
	@PostMapping("/{storeId}/order")
	public ResponseEntity<Map<String, Object>> checkout(
			@Parameter(description = "Store id", example = "1")
	        @PathVariable Long storeId,
	        @Valid @RequestBody List<@Valid CartItemRequest> carts
			){
		 	
		StoreSummaryDTO store = storeService.findById(storeId);
		 		 
		OrderCreateResponsetDTO created = orderService.checkout(carts, Mapper.summaryEntity(store));
		Map<String, Object> body = new HashMap<>();
		body.put("message", "Order created successfully");
		body.put("order", created);
		 
		return ResponseEntity
				.created(URI.create("/api/admin/store/"+store.getId()+"/order"))
				.body(body);
		
		 
	 }
	
	@GetMapping("/{storeId}/order/{orderId}")
	public ResponseEntity<Map<String, Object>> detail(
			@Parameter(description = "store id", example = "1")
			@PathVariable Long storeId,
			@Parameter(description = "order id", example = "2")
			@PathVariable Long orderId
			){
		OrderDetailedResponseDTO order = orderService.orderDetail(orderId, storeId);
		Map<String, Object> body = new HashMap<>();
		body.put("message", "Order detail successfully fetched");
		body.put("order", order);
		
		return ResponseEntity.ok(body);
		
	}
	
}
