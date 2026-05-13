package com.stockflow.backend.auditlog.dto.summary;

import java.time.Instant;
import java.util.Set;

import com.stockflow.backend.auditlog.enumerate.AuditAction;
import com.stockflow.backend.order.dto.create.OrderCreateResponsetDTO;
import com.stockflow.backend.orderItem.dto.create.OrderItemResponseDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "AuditLogResponseDTO", description = "DTO Response that represents AuditLog entity")
public class AuditLogResponseDTO {
	
	
	//What user made the HTTP request
	@Schema(description = "What user made the HTTP request", example = "admin")
	private String performBy;		//username's user
	@Schema(description = "The user id from db", example = "1")
	private Long userId;			//FK from the user db fetched by user.name
	
	//What module - entity was affected
	@Schema(description = "What module - entity was affected", example = "Order - Inventory - Product etc")
	private String entityName;		//e.g Order, Product, Inventory etc.
	@Schema(description = "The entity id from db", example = "1")
	private Long entityId;			//Register entity affected
	
	@Enumerated(EnumType.STRING)
	private AuditAction action;		// CREATE - UPDATE - DISABLE etc.
	
	//How (which HTTP protocol was used)
	@Schema(description = "How (which HTTP protocol was used)", example = "CREATE - UPDATE - DISABLE etc.")
	private String httMethod;		// POST - UPDATE - PATCH - PUT
	@Schema(description = "Literal endpoint", example = "api/admin/store/1...")
	private String endpoint;		// "api/admin/store/1...
	
	@Schema(description = "HTTP previus status", example = "JSON state before(to UPDATE)")
	private String previousValue;	// JSON state before(to UPDATE)
	
	@Schema(description = "HTTP new status", example = "JSON new state.")
	private String newValue;		// JSON new state.
	
	// When this happend
	@Schema(description = "When this changed happend", example = "2026-08-05")
	private Instant performedAt;
	
	// Result
	@Schema(description = "Http status result", example = "200 - 201 - 400")
	private Integer httpStatus;		// 200, 201, 400, etc.
	@Schema(description = "Boolean Http status", example = "true - false")
	private Boolean succes;			// true - false.
	
	@Schema(description = "Any error", example = "some erros")
	private String errorMessage;	
}
