package com.stockflow.backend.order.cart.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockflow.backend.exception.ResourceNotFoundException;
import com.stockflow.backend.order.cart.CartItemRequest;
import com.stockflow.backend.order.cart.service.ICartItemService;
import com.stockflow.backend.user.domain.User;
import com.stockflow.backend.user.repository.IUserRepository;


import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin/cart")
@Tag(name = "CartItemSessionController", description = "Endpoints for CartItem")
public class CartItemSessionController {
	
	@Autowired
	private IUserRepository userRepo;
	
	@Autowired
	private ICartItemService cartService;
	
	@PostMapping
	public ResponseEntity<Map<String, Object>> addToCart(
			@RequestBody List<CartItemRequest> carts,
			@AuthenticationPrincipal String username
			){
		
	
		User user = userRepo.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		
		List<CartItemRequest> items = cartService.addToCart(user.getId(), carts);
		
		Map<String, Object> body = new HashMap<>();
		body.put("carts", items);
		
		return ResponseEntity.ok(body);
	}
	
	@GetMapping
	public ResponseEntity<Map<String, Object>> getCart(
	        @AuthenticationPrincipal String username) {

	    User user = userRepo.findByUsername(username)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	    List<CartItemRequest> items = cartService.getCart(user.getId());

	    Map<String, Object> body = new HashMap<>();
	    body.put("cart", items);

	    return ResponseEntity.ok(body);
	}
	
}
