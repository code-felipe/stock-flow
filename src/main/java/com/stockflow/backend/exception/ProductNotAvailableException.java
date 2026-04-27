package com.stockflow.backend.exception;

public class ProductNotAvailableException extends RuntimeException {
	public ProductNotAvailableException(String message) {super(message);};
}
