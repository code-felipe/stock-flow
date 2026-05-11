package com.stockflow.backend.cart.service;

import java.util.List;

import com.stockflow.backend.cart.dto.CartItemRequest;
import com.stockflow.backend.cart.dto.CartItemResponse;
import com.stockflow.backend.cart.dto.CartResponse;




public interface ICartItemService {
	
	public List<CartItemRequest> addToCart(Long userId, List<CartItemRequest> cars);
	public List<CartItemRequest> getCart(Long userId);
	public void clearCart(Long userId);
	public CartItemRequest remove(Long userId, Long productId);
	public CartResponse cartDetail(Long userId);
}
