package com.stockflow.backend.order.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.stockflow.backend.cart.dto.CartItemRequest;
import com.stockflow.backend.cart.dto.CartItemResponse;
import com.stockflow.backend.exception.OutOfStockException;
import com.stockflow.backend.exception.ProductNotAvailableException;
import com.stockflow.backend.exception.ResourceNotFoundException;
import com.stockflow.backend.inventory.domain.Inventory;
import com.stockflow.backend.inventory.repository.IInventoryRepository;
import com.stockflow.backend.inventory.service.IInventoryService;
import com.stockflow.backend.order.domain.Order;
import com.stockflow.backend.order.dto.create.OrderCreateResponsetDTO;
import com.stockflow.backend.order.dto.filter.OrderFilter;
import com.stockflow.backend.order.dto.summary.OrderDetailedResponseDTO;
import com.stockflow.backend.order.dto.summary.OrderSummaryResponseDTO;
import com.stockflow.backend.order.enumerate.OrderStatus;
import com.stockflow.backend.order.repository.IOrderRespository;
import com.stockflow.backend.order.spec.OrderSpecifications;
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
	public OrderCreateResponsetDTO checkout(List<CartItemRequest> cart, Store store) {
		// TODO Auto-generated method stub
		
		List<CartItemResponse> cartItems = new ArrayList<>();
	
		Order order = new Order();
		order.setOrderDate(Instant.now());
		//Example to test the dateFrom dateTo: needs to deactivate prePersists()
		//order.setOrderDate(Instant.parse("2026-02-10T14:00:00Z"));
		order.setOrderStatus(OrderStatus.CONFIRMED);
		order.setStore(store);
		order.setTotal(0.0);
		
		// persists the order: total is 0.0
		orderRepo.save(order);
		
		cart.stream().forEach(c -> {
			Inventory inv = inventoryService.findById(store.getId(), c.getProductId());
			
			if(inv.getOnHand() < c.getQuantity()) {
				throw new OutOfStockException("Stock is insufficient " + inv.getProduct().getName());
				}
			if (!inv.getProduct().getActive()) {
			        throw new ProductNotAvailableException("Product is not available: " + inv.getProduct().getName() + " id: " + inv.getProduct().getId() );
			   	}
			//discount on stock - onHand
			inv.setOnHand(inv.getOnHand() - c.getQuantity());
			
			OrderItem item = new OrderItem();
			item.setInventory(inv);
			item.setQuantity(c.getQuantity());
			item.setUnitPrice(inv.getProduct().getPrice());
			
			order.getOrderItems().add(item);
			
			CartItemResponse ca = new CartItemResponse();
			ca.setProductId(c.getProductId());
			ca.setProductName(inv.getProduct().getName());
			ca.setQuantity(c.getQuantity());
			ca.setUnitPrice(inv.getProduct().getPrice());
			
			cartItems.add(ca);
			
		});
		
		
		Double total = order.getOrderItems().stream()
				.mapToDouble(c -> c.subTotal())
				.sum();
		order.setTotal(total);

		// Once the order is persists the save method updates that order with the OrderItem feed it by the cartItem
		//second save — UPDATE total + INSERT order_items on cascade and includes the real product price
	    return Mapper.createOrderResponse(orderRepo.save(order));
	}

	@Override
	public Page<OrderSummaryResponseDTO> findAllOrdersByStoreId(Long storeId, OrderFilter filter, Pageable pageable) {
		// TODO Auto-generated method stub
		 Specification<Order> spec = OrderSpecifications.withFilters(
		            filter.getOrderStatus(),
		            filter.getDateFrom(),
		            filter.getDateTo(),
		            filter.getTotalMin(),
		            filter.getTotalMax()
		    );
	
		 return orderRepo.findAll(spec, pageable).map(Mapper::toSummaryDTO);
		
	}

	@Override
	public OrderDetailedResponseDTO orderDetail(Long orderId, Long storeId) {
		// TODO Auto-generated method stub
		Order order = orderRepo.findByIdAndStoreId(orderId, storeId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found!")) ;
		
		
		return Mapper.toDetail(order);
	}

	@Transactional
	@Override
	public OrderSummaryResponseDTO cancel(Long orderId, Long storeId) {
		// TODO Auto-generated method stub
		Order order = orderRepo.findByIdAndStoreId(orderId, storeId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found!"));
		
		order.setOrderStatus(OrderStatus.CANCELLED);
		
		return Mapper.toSummaryDTO(orderRepo.save(order));
	}
		
//		cart.stream().forEach(c -> {
//			c.setStoreId(store.getId());
//			Inventory inv = inventoryService.findById(c.getStoreId(), c.getProductId());
//			
//			if(inv.getOnHand() < c.getQuantity()) {
//				throw new OutOfStockException("Stock is insufficient " + c.getProductName());
//			}
//
//			OrderItem item = new OrderItem();
//			item.setInventory(inv);
//			item.setQuantity(c.getQuantity());
//			item.setUnitPrice(inv.getProduct().getPrice());
//			
//			order.getOrderItems().add(item);
//			
//			//discount
//			inv.setOnHand(inv.getOnHand() - c.getQuantity());
//			
//			
//		});
//		
//		Double total = cart.stream()
//				.mapToDouble(c -> c.subTotal())
//				.sum();
//		order.setTotal(total);
//		
//		
//		return Mapper.createOrderResponse(orderRepo.save(order));
//		
//	}
	

/*
 * Resumen

400 Bad Request — Estabas mandando {} en el body cuando el endpoint esperaba []
Separación de responsabilidades — Dividiste CartItem en:

CartItemRequest → solo productId y quantity (lo que manda el cliente)
CartItemResponse → enriquecido con storeId, productName, unitPrice y subTotal() desde el servidor

Persistencia del Order en cascada OrderItem mediante el controller con el post

Precios y nombres desde el servidor — Dejaste de confiar en datos del cliente, ahora se obtienen desde inv.getProduct()
Total calculado correctamente — Desde cartItems (enriquecido) no desde cart (request)
Validaciones — Agregaste List<@Valid CartItemRequest> en el controller y el handler HandlerMethodValidationException en el GlobalExceptionHandler
 */

}
