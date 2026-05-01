package com.stockflow.backend.cart.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.stockflow.backend.cart.dto.CartItemRequest;
import com.stockflow.backend.exception.CartEmptyException;

@Service
public class CartItemServiceImpl implements ICartItemService {
	
	// 			user id - product id - cart
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
	    Map<Long, CartItemRequest> innerMap = this.getOrCreateCart(userId);
	    return new ArrayList<>(innerMap.values()); // retorna [] si está vacío - the front end does not need error message here.
	}

	@Override
	public void clearCart(Long userId) {
		// TODO Auto-generated method stub
		Map<Long, CartItemRequest> innerMap = cart.get(userId);
		innerMap.values().clear();
		cart.put(userId, innerMap);		
	}
	
	@Override
	public CartItemRequest remove(Long userId, Long productId) {
	    Map<Long, CartItemRequest> innerMap = cart.get(userId);

	    if (innerMap == null || innerMap.isEmpty()) {
	        return null; // cart does no exists or is empty
	    }

	    return innerMap.remove(productId); //item or null if did not existed
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
