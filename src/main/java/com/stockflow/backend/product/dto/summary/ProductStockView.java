package com.stockflow.backend.product.dto.summary;

import java.time.Instant;

public interface ProductStockView {
	
	Long getProductId();
    String getProductName();
    String getProductDescription();
    Double getProductPrice();
    String getProductImageUrl();
    String getProductSku();
    Integer getOnHand();
    Integer getReserved();
    Instant getCreatedAt();  // opcional
    Instant getUpdatedAt();  // opcional

}
