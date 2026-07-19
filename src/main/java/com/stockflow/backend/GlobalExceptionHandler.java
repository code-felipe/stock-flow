package com.stockflow.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.stockflow.backend.auditlog.service.IAuditLogService;
import com.stockflow.backend.error.ApiErrorResponse;
import com.stockflow.backend.exception.CartEmptyException;
import com.stockflow.backend.exception.DuplicateResourceException;
import com.stockflow.backend.exception.OutOfStockException;
import com.stockflow.backend.exception.ProductNotAvailableException;
import com.stockflow.backend.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@Autowired
	private IAuditLogService auditService;
	//=== Old implementation without ApiErrorResponse ===
	/*
	@ExceptionHandler(ProductNotAvailableException.class)
	public ResponseEntity<Map<String, Object>> handleProductNotAvailable(ProductNotAvailableException ex) {
	    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
	            "timestamp", Instant.now().toString(),
	            "status", 409,
	            "error", "Conflict",
	            "message", ex.getMessage()
	    ));
	}
	
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
//                "timestamp", Instant.now().toString(),
//                "status", 404,
//                "error", "Not Found",
//                "message", ex.getMessage()
//        ));
//    }

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

        if (detail != null && detail.toLowerCase().contains("duplicate entry")) {
            // extrae el valor duplicado entre comillas simples
            String value = detail.replaceAll(".*Duplicate entry '([^']+)'.*", "$1");

            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "timestamp", Instant.now().toString(),
                    "status", 409,
                    "error", "Conflict",
                    "message", "The value '" + value + "' already exists",
                    "detail", detail
            ));
        }

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
    
    // Audit catch the error

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleNotFound(
	        ResourceNotFoundException ex,
	        HttpServletRequest request,
	        Authentication auth) {
	
	    auditService.saveFailedAudit(auth, request, 404, ex.getMessage());
	
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
	            "timestamp", Instant.now().toString(),
	            "status", 404,
	            "error", "Not Found",
	            "message", ex.getMessage()
	    ));
	}
	*/
	// === New Implementation with ApiErrorResponse ==
	@ExceptionHandler(ProductNotAvailableException.class)
	public ResponseEntity<ApiErrorResponse> handleProductNotAvailable(ProductNotAvailableException ex) {
		ApiErrorResponse body = ApiErrorResponse.builder()
				.timestamp(Instant.now().toString())
				.status(409)
				.error("Conflict")
				.message(ex.getMessage())
				.build();

		return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
	}

//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex) {
//        ApiErrorResponse body = ApiErrorResponse.builder()
//                .timestamp(Instant.now().toString())
//                .status(404)
//                .error("Not Found")
//                .message(ex.getMessage())
//                .build();
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
//    }

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiErrorResponse> handleBadRequest(IllegalArgumentException ex) {
		ApiErrorResponse body = ApiErrorResponse.builder()
				.timestamp(Instant.now().toString())
				.status(400)
				.error("Bad Request")
				.message(ex.getMessage())
				.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<ApiErrorResponse> handleDataAccess(DataAccessException ex) {
		ApiErrorResponse body = ApiErrorResponse.builder()
				.timestamp(Instant.now().toString())
				.status(500)
				.error("Internal Server Error")
				.message("Database error")
				.detail(ex.getMostSpecificCause().getMessage())
				.build();

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

		Map<String, String> fieldErrors = new LinkedHashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(err ->
				fieldErrors.put(err.getField(), err.getDefaultMessage())
		);

		ApiErrorResponse body = ApiErrorResponse.builder()
				.timestamp(Instant.now().toString())
				.status(400)
				.error("Bad Request")
				.message("Validation failed")
				.errors(fieldErrors)
				.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {

		String detail = ex.getMostSpecificCause() != null
				? ex.getMostSpecificCause().getMessage()
				: ex.getMessage();

		// Heurística: si el mensaje trae "duplicate entry", es un conflicto de unicidad
		boolean duplicate = detail != null && detail.toLowerCase().contains("duplicate entry");

		if (duplicate) {
			String value = detail.replaceAll(".*Duplicate entry '([^']+)'.*", "$1");

			ApiErrorResponse body = ApiErrorResponse.builder()
					.timestamp(Instant.now().toString())
					.status(409)
					.error("Conflict")
					.message("The value '" + value + "' already exists")
					.detail(detail)
					.build();

			return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
		}

		ApiErrorResponse body = ApiErrorResponse.builder()
				.timestamp(Instant.now().toString())
				.status(400)
				.error("Bad Request")
				.message("Database constraint violation")
				.detail(detail)
				.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	// works on categori, when name is duplicate or store id and product id are equals "rare that happens".
	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ApiErrorResponse> handleDuplicate(DuplicateResourceException ex) {
		ApiErrorResponse body = ApiErrorResponse.builder()
				.timestamp(Instant.now().toString())
				.status(409)
				.error("Conflict")
				.message(ex.getMessage())
				.build();

		return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
	}

	//Handles 500 exception when the quantity is higher than onHand - stock
	@ExceptionHandler(OutOfStockException.class)
	public ResponseEntity<ApiErrorResponse> handleOutOfStock(OutOfStockException ex) {
		ApiErrorResponse body = ApiErrorResponse.builder()
				.timestamp(Instant.now().toString())
				.status(409)
				.error("Product quantity is higher than stock - onHand")
				.message(ex.getMessage())
				.build();

		return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
	}

	//Handles all errors on list. Special useful for the CartItemRequest
	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<ApiErrorResponse> handleMethodValidation(HandlerMethodValidationException ex) {

		// Antes era List<String>; se normaliza a Map<String,String> para
		// que 'errors' tenga siempre el mismo tipo en toda la API.
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		int i = 0;
		for (var err : ex.getAllErrors()) {
			fieldErrors.put("error_" + (i++), err.getDefaultMessage());
		}

		ApiErrorResponse body = ApiErrorResponse.builder()
				.timestamp(Instant.now().toString())
				.status(400)
				.error("Bad Request")
				.message("Validation failed")
				.errors(fieldErrors)
				.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@ExceptionHandler(CartEmptyException.class)
	public ResponseEntity<ApiErrorResponse> handleEmptyCart(CartEmptyException ex) {
		ApiErrorResponse body = ApiErrorResponse.builder()
				.timestamp(Instant.now().toString())
				.status(404)
				.error("Not Found")
				.message(ex.getMessage())
				.build();

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}

	// Audit catch the error
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(
			ResourceNotFoundException ex,
			HttpServletRequest request,
			Authentication auth) {

		auditService.saveFailedAudit(auth, request, 404, ex.getMessage());

		ApiErrorResponse body = ApiErrorResponse.builder()
				.timestamp(Instant.now().toString())
				.status(404)
				.error("Not Found")
				.message(ex.getMessage())
				.build();

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}
}
