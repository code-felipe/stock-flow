package com.stockflow.backend.order.cart.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.stockflow.backend.exception.CartEmptyException;
import com.stockflow.backend.order.cart.CartItemRequest;


@Service
public class CartItemServiceImpl implements ICartItemService {
	
	
	private Map<Long, Map<Long, CartItemRequest>> cart = new HashMap<>();
	
	@Override
	public List<CartItemRequest> addToCart(Long userId, List<CartItemRequest> carts) {
		return this.mergeItems(userId, carts);

//	    Map<Long, CartItemRequest> innerMap = cart.get(userId);
//
//	    if(innerMap == null) {
//	        innerMap = new HashMap<>();
//	        cart.put(userId, innerMap);
//	    }
//
//	    Map<Long, CartItemRequest> finalInnerMap = innerMap;
//
//	    carts.forEach(newItem -> {
//	        if(finalInnerMap.containsKey(newItem.getProductId())) {
//	            CartItemRequest existing = finalInnerMap.get(newItem.getProductId());
//	            existing.setQuantity(existing.getQuantity() + newItem.getQuantity());
//	        } else {
//	            finalInnerMap.put(newItem.getProductId(), newItem);
//	        }
//	    });
//
//	    return new ArrayList<>(finalInnerMap.values());
	}

	@Override
	public List<CartItemRequest> getCart(Long userId) {
		// TODO Auto-generated method stub
//		Map<Long, CartItemRequest> innerMap = cart.get(userId);
		Map<Long, CartItemRequest> innerMap = this.getOrCreateCart(userId);
		
		if(innerMap == null || innerMap.isEmpty()) {
		    throw new CartEmptyException("Cart is empty");
		}
		
		return new ArrayList<>(innerMap.values());
	}

	@Override
	public void clearCart(Long userId) {
		// TODO Auto-generated method stub
		Map<Long, CartItemRequest> innerMap = cart.get(userId);
		innerMap.values().clear();
		cart.put(userId, innerMap);
		
		
	}
	
	//Helpers
	private  List<CartItemRequest> mergeItems(Long userId, List<CartItemRequest> carts){
		
		 
		Map<Long, CartItemRequest> finalInnerMap = this.getOrCreateCart(userId);
		
		carts.forEach(newItem -> {
	        if(finalInnerMap.containsKey(newItem.getProductId())) {
	            CartItemRequest existing = finalInnerMap.get(newItem.getProductId());
	            existing.setQuantity(existing.getQuantity() + newItem.getQuantity());
	        } else {
	            finalInnerMap.put(newItem.getProductId(), newItem);
	        }
	    });
		
		return new ArrayList<>(finalInnerMap.values());
	}
	
	private Map<Long, CartItemRequest> getOrCreateCart(Long userId){
		
		Map<Long, CartItemRequest> innerMap = cart.get(userId);

	    if(innerMap == null) {
	        innerMap = new HashMap<>();
	        cart.put(userId, innerMap);
	    }
	    
	    return innerMap;
	}

}
