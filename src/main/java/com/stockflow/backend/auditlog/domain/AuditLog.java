package com.stockflow.backend.auditlog.domain;

import java.time.Instant;

import com.stockflow.backend.auditlog.enumerate.AuditAction;
import com.stockflow.backend.inventory.domain.Inventory;
import com.stockflow.backend.inventory.domain.InventoryId;
import com.stockflow.backend.product.domain.Product;
import com.stockflow.backend.store.domain.Store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class AuditLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//What user made the HTTP request
	private String performBy;		//username's user
	private Long userId;			//FK from the user db fetched by user.name
	
	
	//What module - entity was affected
	private String entityName;		//e.g Order, Product, Inventory etc.
	private Long entityId;			//Register entity affected
	
	@Enumerated(EnumType.STRING)
	private AuditAction action;		// CREATE - UPDATE - DISABLE etc.
	
	//How (which HTTP protocol was used)
	private String httMethod;		// POST - UPDATE - PATCH - PUT
	private String endpoint;		// "api/admin/store/1...
	
	@Column(columnDefinition = "TEXT")
	private String previousValue;	// JSON state before(to UPDATE)
	
	@Column(columnDefinition = "TEXT")
	private String newValue;		// JSON new state.
	
	// When this happend
	private Instant performedAt;
	
	
	// Result
	private Integer httpStatus;		// 200, 201, 400, etc.
	private Boolean succes;			// true - false.
	
	@Column(columnDefinition = "TEXT")
	private String errorMessage;	
}
