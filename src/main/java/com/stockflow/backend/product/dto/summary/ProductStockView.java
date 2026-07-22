package com.stockflow.backend.product.dto.summary;

import java.time.Instant;

// Use on native query
public interface ProductStockView {
	
	Long getProductId();
    String getProductName();
    String getProductDescription();
    Double getProductPrice();
    String getProductImageUrl();
    String getProductSku();
    Integer getOnHand();
    Integer getReserved();
    Integer getMinStock();
    Instant getCreatedAt();  // opcional
    Instant getUpdatedAt();  // opcional

}
