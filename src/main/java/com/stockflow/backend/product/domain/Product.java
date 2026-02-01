package com.stockflow.backend.product.domain;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
@Table(name = "products")
public class Product {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String description;
	private Double price;
	private String sku;
	@Column(name = "image_url")
	private String imageUrl;
	private BigDecimal stock;
	@Column(nullable = false)
	private Boolean active = true;
	@Column(name="discontinued_at")
	private Date discontinuedAt;
	@Column(name = "created_at", nullable = false, updatable = false)
	private Date createdAt;

	@PrePersist
	protected void onCreate() {
	    if (createdAt == null) {
	        createdAt = new Date();
	    }
	}

}
