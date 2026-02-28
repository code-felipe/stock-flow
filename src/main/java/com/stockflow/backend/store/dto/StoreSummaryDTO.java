package com.stockflow.backend.store.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.stockflow.backend.product.dto.summary.ProductSummaryDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
public class StoreSummaryDTO {
	@Schema(description = "Store id", example = "1")
	private Long id;

	@Schema(description = "Store name", example = "Jewerly Store")
	@NotBlank(message = "Name is required")
	private String name;

	@Schema(description = "Store description", example = "Store for jewerly products")
	@NotBlank(message = "Description is required")
	private String description;
	
	@Schema(description = "Store address location", example = "2417 swing ct, Louisville Ky")
	@NotBlank(message = "Description is required")
	private String address;
	
	@Schema(description = "Store creation date", example = "2026-01-31T14:16:36Z")
    private Date createdAt;
}
