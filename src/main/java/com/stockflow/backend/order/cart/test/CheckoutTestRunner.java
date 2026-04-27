package com.stockflow.backend.order.cart.test;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.stockflow.backend.inventory.domain.Inventory;
import com.stockflow.backend.inventory.domain.InventoryId;
import com.stockflow.backend.inventory.repository.IInventoryRepository;
import com.stockflow.backend.inventory.service.IInventoryService;
import com.stockflow.backend.order.cart.CartItemRequest;
import com.stockflow.backend.order.cart.CartItemResponse;
import com.stockflow.backend.order.dto.create.OrderCreateResponsetDTO;
import com.stockflow.backend.order.service.IOrderService;
import com.stockflow.backend.store.dto.StoreSummaryDTO;
import com.stockflow.backend.store.service.IStoreService;
import com.stockflow.backend.utils.mapper.Mapper;


@Component
public class CheckoutTestRunner implements CommandLineRunner{
	
	@Autowired
	private IStoreService storeService;
	
	@Autowired
	private IOrderService orderService;

	
	@Override
	public void run(String... args) throws Exception {

		
//		StoreSummaryDTO store = storeService.findById(1L);
//		
//		List<CartItemRequest> cart = List.of(
////				CartItemRequest.builder()
//////				.storeId(store.getId())
//////				.productName("Churros de arequipe")
////				.productId(2L)
//////				.unitPrice(10.00)
////				.quantity(3)
////				.build(),
//				
//				CartItemRequest.builder()
////				.storeId(store.getId())
////				.productName("Churro de queso")
//				.productId(1L)
////				.unitPrice(12.40)
//				.quantity(30)
//				.build()
//				);
//				
//		OrderCreateResponsetDTO response = orderService.checkout(cart, Mapper.summaryEntity(store));
//		System.out.println(response);
				
	}

}
