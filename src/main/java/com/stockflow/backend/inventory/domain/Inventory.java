package com.stockflow.backend.inventory.domain;

import java.time.Instant;
import java.util.Date;

import com.stockflow.backend.product.domain.Product;
import com.stockflow.backend.store.domain.Store;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "inventories")
public class Inventory {
	
	@EmbeddedId
    private InventoryId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("storeId") // conecta id.storeId con store.id
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("productId") // conecta id.productId con product.id
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "on_hand", nullable = false)
    private Integer onHand = 0;

    @Column(name = "reserved", nullable = false)
    private Integer reserved = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        var now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
