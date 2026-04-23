package com.stockflow.backend.order.service;

import java.util.List;

import com.stockflow.backend.order.cart.CartItem;
import com.stockflow.backend.order.dto.create.OrderCreateResponsetDTO;
import com.stockflow.backend.store.domain.Store;

public interface IOrderService {
	
	public OrderCreateResponsetDTO checkout(List<CartItem> cart, Store store);

}
