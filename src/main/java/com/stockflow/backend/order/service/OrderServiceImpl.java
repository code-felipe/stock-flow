package com.stockflow.backend.order.service;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stockflow.backend.exception.OutOfStockException;
import com.stockflow.backend.inventory.domain.Inventory;
import com.stockflow.backend.inventory.repository.IInventoryRepository;
import com.stockflow.backend.inventory.service.IInventoryService;
import com.stockflow.backend.order.cart.CartItem;
import com.stockflow.backend.order.domain.Order;
import com.stockflow.backend.order.dto.create.OrderCreateResponsetDTO;
import com.stockflow.backend.order.repository.IOrderRespository;
import com.stockflow.backend.orderItem.domain.OrderItem;
import com.stockflow.backend.store.domain.Store;
import com.stockflow.backend.utils.mapper.Mapper;

import jakarta.transaction.Transactional;

@Service
public class OrderServiceImpl implements IOrderService{
	
	@Autowired
	private IOrderRespository orderRepo;
	
	@Autowired
	private IInventoryService inventoryService;
	
	@Transactional
	@Override
	public OrderCreateResponsetDTO checkout(List<CartItem> cart, Store store) {
		// TODO Auto-generated method stub
			
		Order order = new Order();
		order.setOrderDate(Instant.now());
		order.setOrderStatus("PENDING");
		order.setStore(store);
		
		cart.stream().forEach(c -> {
			Inventory inv = inventoryService.findById(c.getStoreId(), c.getProductId());
			
			if(inv.getOnHand() < c.getQuantity()) {
				throw new OutOfStockException("Stock is insufficient " + c.getProductName());
			}
				
			
			OrderItem item = new OrderItem();
			item.setInventory(inv);
			item.setQuantity(c.getQuantity());
			item.setUnitPrice(c.getUnitPrice());
			
			order.getOrderItems().add(item);
			
			//discount
			inv.setOnHand(inv.getOnHand() - c.getQuantity());
			
		});
		
		Double total = cart.stream()
				.mapToDouble(c -> c.getQuantity() * c.getUnitPrice())
				.sum();
		order.setTotal(total);
		
		
		return Mapper.createOrderResponse(orderRepo.save(order));
		
		
	}



}
