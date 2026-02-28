package com.stockflow.backend.inventory.domain;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class InventoryId implements Serializable{
	
	private Long storeId;
    private Long productId;

    public InventoryId() {}

    public InventoryId(Long storeId, Long productId) {
        this.storeId = storeId;
        this.productId = productId;
    }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InventoryId that)) return false;
        return Objects.equals(storeId, that.storeId) &&
               Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeId, productId);
    }
}
