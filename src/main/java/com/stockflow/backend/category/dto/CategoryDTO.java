package com.stockflow.backend.category.dto;

import java.util.Date;

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
@Schema(name = "CategoryDTO", description = "DTO that represents a category")
public class CategoryDTO {
	

    @Schema(description = "Category ID", example = "1")
	private Long id;

    @Schema(description = "Category name", example = "Ring")
    @NotBlank(message = "Ring represents the product's Gold Emerald Ring category")
	private String name;

    @Schema(description = "Category description", example = "Minimalist and statement rings designed for everyday wear or to elevate any look")
    @NotBlank(message = "Description is required")
	private String description;
    
    @Schema(description = "Category Image representation", example = "Image in JPG or SVG that represents de category")
    @NotBlank(message = "Attacht an image is required")
	private String image;
    
    @Schema(description = "Category creation date", example = "2026-01-31T14:16:36Z")
	private Date createdAt;
}
