package com.stockflow.backend.product.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.stockflow.backend.category.domain.Category;
import com.stockflow.backend.inventory.domain.Inventory;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
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
@Table(name = "products")
public class Product {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private String description;
	@Column(nullable = false)
	private Double price;
	@Column(nullable = false, unique = true)
	private String sku;
	@Column(name = "image_url")
	private String imageUrl;
	// this attribute 'stock' will be replaced by the Inventory
	@Column(nullable = false)
	private BigDecimal stock;
	@Column(nullable = false)
	private Boolean active = true;
	@Column(name="discontinued_at")
	private Date discontinuedAt;
	@Column(name = "created_at", nullable = false, updatable = false)
	private Date createdAt;
	
	@ManyToMany
	@JoinTable(
	  name = "product_categories",
	  joinColumns = @JoinColumn(name = "product_id"),
	  inverseJoinColumns = @JoinColumn(name = "category_id")
	)
	private Set<Category> categories = new HashSet<>();
	
	// Helps the query star from product to bring all product attributes
	// En Product.java
	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
	private Set<Inventory> inventories = new HashSet<>();


	@PrePersist
	protected void onCreate() {
	    if (createdAt == null) {
	        createdAt = new Date();
	    }
	}

}
