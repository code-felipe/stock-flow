package com.stockflow.backend.order.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.stockflow.backend.category.domain.Category;
import com.stockflow.backend.inventory.domain.Inventory;
import com.stockflow.backend.orderItem.domain.OrderItem;
import com.stockflow.backend.product.domain.Product;
import com.stockflow.backend.store.domain.Store;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "orders")
public class Order {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "order_date")
	private Instant orderDate;
	
	@Column(name = "order_status")
	private String orderStatus;
	
	private Double total;
	
	@ManyToOne
	private Store store;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "order_id")
	private List<OrderItem> orderItems = new ArrayList<>();
	
	@PrePersist
	void prePersist() {
		Instant now = Instant.now();
		orderDate = now;
	}
	
}
