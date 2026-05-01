package com.stockflow.backend;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.stockflow.backend.exception.CartEmptyException;
import com.stockflow.backend.exception.DuplicateResourceException;
import com.stockflow.backend.exception.OutOfStockException;
import com.stockflow.backend.exception.ProductNotAvailableException;
import com.stockflow.backend.exception.ResourceNotFoundException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(ProductNotAvailableException.class)
	public ResponseEntity<Map<String, Object>> handleProductNotAvailable(ProductNotAvailableException ex) {
	    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
	            "timestamp", Instant.now().toString(),
	            "status", 409,
	            "error", "Conflict",
	            "message", ex.getMessage()
	    ));
	}
	
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 404,
                "error", "Not Found",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 400,
                "error", "Bad Request",
                "message", ex.getMessage()
        ));
    }
    
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccess(DataAccessException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 500,
                "error", "Internal Server Error",
                "message", "Database error",
                "detail", ex.getMostSpecificCause().getMessage()
        ));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                fieldErrors.put(err.getField(), err.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 400,
                "error", "Bad Request",
                "message", "Validation failed",
                "errors", fieldErrors
        ));
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {

        String detail = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage();

        // Heurística simple: si el mensaje menciona sku o unique constraint
        boolean skuConflict = detail != null && (
                detail.toLowerCase().contains("sku") ||
                detail.toLowerCase().contains("unique")
        );

        if (skuConflict) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "timestamp", Instant.now().toString(),
                    "status", 409,
                    "error", "Conflict",
                    "message", "SKU already exists",
                    "detail", detail
            ));
        }

        // Otros casos de integridad -> 400
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 400,
                "error", "Bad Request",
                "message", "Database constraint violation",
                "detail", detail
        ));
    }
    
    // works on categori, when name is duplicate or store id and product id are equals "rare that happens".
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 409,
                "error", "Conflict",
                "message", ex.getMessage()
        ));
    }
    
    //Handles 500 exception when the quantity is higher than onHand - stock
    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<Map<String, Object>> handleOutOfStock(OutOfStockException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
        		"timestamp", Instant.now().toString(),
        		"status", 409,
        		"error", "Product quantity is higher than stock - onHand",
        		"message", ex.getMessage()
        		));
    }
    
    //Handles all errors on list. Special useful for the CartItemRequest
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, Object>> handleMethodValidation(HandlerMethodValidationException ex) {
        // extrae los mensajes aquí
    	List<String> errors = ex.getAllErrors().stream()
                .map(err -> err.getDefaultMessage())
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 400,
                "error", "Bad Request",
                "message", "Validation failed",
                "errors", errors
        ));
    }
    
    @ExceptionHandler(CartEmptyException.class)
    public ResponseEntity<Map<String, Object>> handleEmptyCart(CartEmptyException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
            "status", 404,
            "message", ex.getMessage()
        ));
    }
}
