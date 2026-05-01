package com.stockflow.backend.exception;

public class CartEmptyException extends RuntimeException {
	
	public CartEmptyException(String message) { super(message); }
}
