package com.stockflow.backend.cart.test;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.stockflow.backend.order.service.IOrderService;

import com.stockflow.backend.store.service.IStoreService;



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
