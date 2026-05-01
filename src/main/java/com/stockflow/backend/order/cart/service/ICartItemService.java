package com.stockflow.backend.order.cart.service;

import java.util.List;

import com.stockflow.backend.order.cart.CartItemRequest;


public interface ICartItemService {
	
	public List<CartItemRequest> addToCart(Long userId, List<CartItemRequest> cars);
	
	public List<CartItemRequest> getCart(Long userId);
	
	public void clearCart(Long userId);
}
