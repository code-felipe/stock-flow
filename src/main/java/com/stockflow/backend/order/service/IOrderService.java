package com.stockflow.backend.order.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.stockflow.backend.order.cart.CartItemRequest;
import com.stockflow.backend.order.dto.create.OrderCreateResponsetDTO;
import com.stockflow.backend.order.dto.filter.OrderFilter;
import com.stockflow.backend.order.dto.summary.OrderDetailedResponseDTO;
import com.stockflow.backend.order.dto.summary.OrderSummaryResponseDTO;
import com.stockflow.backend.store.domain.Store;

public interface IOrderService {
	
	public OrderCreateResponsetDTO checkout(List<CartItemRequest> cart, Store store);
	public Page<OrderSummaryResponseDTO> findAllOrdersByStoreId(Long storeId, OrderFilter filter,  Pageable pageable);
	public OrderDetailedResponseDTO orderDetail(Long orderId, Long storeId);
	public OrderSummaryResponseDTO cancel(Long orderId, Long storeId);

}
