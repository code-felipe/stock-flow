package com.stockflow.backend.utils.mapper;

import java.util.stream.Collectors;

import com.stockflow.backend.category.domain.Category;
import com.stockflow.backend.category.dto.CategoryDTO;
import com.stockflow.backend.inventory.domain.Inventory;
import com.stockflow.backend.inventory.dto.InventorySummaryDTO;
import com.stockflow.backend.product.domain.Product;
import com.stockflow.backend.product.dto.ProductDTO;
import com.stockflow.backend.product.dto.create.ProductCreateResponseDTO;
import com.stockflow.backend.product.dto.detail.ProductDetailDTO;
import com.stockflow.backend.product.dto.summary.ProductStockView;
import com.stockflow.backend.product.dto.summary.ProductSummaryDTO;
import com.stockflow.backend.product.dto.update.ProductUpdateResponseDTO;
import com.stockflow.backend.store.domain.Store;
import com.stockflow.backend.store.dto.StoreSummaryDTO;

public class Mapper {
	
	public static ProductSummaryDTO toSummaryDTO(Product product) {
		
		return ProductSummaryDTO.builder()
				.id(product.getId())
				.name(product.getName())
				.description(product.getDescription())
				.price(product.getPrice())
				.sku(product.getSku())
				.imageUrl(product.getImageUrl())
				.active(product.getActive())
				.discontinuedAt(product.getDiscontinuedAt())
				.stock(product.getStock())
				.createdAt(product.getCreatedAt())
				.build();
	}
	
	public static ProductDetailDTO toDetailDTO(Product product) {
		
		return ProductDetailDTO.builder()
				.id(product.getId())
				.name(product.getName())
				.description(product.getDescription())
				.price(product.getPrice())
				.sku(product.getSku())
				.imageUrl(product.getImageUrl())
				.active(product.getActive())
				.discontinuedAt(product.getDiscontinuedAt())
				.stock(product.getStock())
				.createdAt(product.getCreatedAt())
				.categoryIds(
						product.getCategories() == null ? null : 
							product.getCategories().stream()
							.map(Category::getId)
							.collect(Collectors.toSet())
						)
				.build();
	}
	
	public static ProductCreateResponseDTO toCreateDTO(Product product) {
		
		return ProductCreateResponseDTO.builder()
				.id(product.getId())
				.name(product.getName())
				.description(product.getDescription())
				.price(product.getPrice())
				.sku(product.getSku())
				.imageUrl(product.getImageUrl())
				.active(product.getActive())
				.discontinuedAt(product.getDiscontinuedAt())
				.stock(product.getStock())
				.createdAt(product.getCreatedAt())
				.build();
	}
	
	public static ProductUpdateResponseDTO toUpdateDTO(Product product) {
		
		return ProductUpdateResponseDTO.builder()
				.id(product.getId())
				.name(product.getName())
				.description(product.getDescription())
				.price(product.getPrice())
				.sku(product.getSku())
				.imageUrl(product.getImageUrl())
				.active(product.getActive())
				.discontinuedAt(product.getDiscontinuedAt())
				.stock(product.getStock())
				.createdAt(product.getCreatedAt())
				.build();
	}
	
	public static ProductDTO toDTO(Product product) {
		
		return ProductDTO.builder()
				.id(product.getId())
				.name(product.getName())
				.description(product.getDescription())
				.price(product.getPrice())
				.sku(product.getSku())
				.imageUrl(product.getImageUrl())
				.active(product.getActive())
				.discontinuedAt(product.getDiscontinuedAt())
				.stock(product.getStock())
				.createdAt(product.getCreatedAt())
//				.categoryIds(
//						product.getCategories() == null ? null : 
//							product.getCategories().stream()
//							.map(Category::getId)
//							.collect(Collectors.toSet())
//						)
				.build();
	}
	
	public static CategoryDTO toDTO(Category category) {
		
		return CategoryDTO.builder()
				.id(category.getId())
				.name(category.getName())
				.description(category.getDescription())
				.image(category.getImage())
				.createdAt(category.getCreatedAt())
				.build();
	}
	
	public static StoreSummaryDTO toSummaryDTO(Store store) {
		
		return StoreSummaryDTO.builder()
				.id(store.getId())
				.name(store.getName())
				.description(store.getDescription())
				.address(store.getAddress())
				.createdAt(store.getCreatedAt())
				.build();
	}
	
	public static InventorySummaryDTO toSummaryDTO(ProductStockView v) {
	    return InventorySummaryDTO.builder()
	        .productId(v.getProductId())
	        .productName(v.getProductName())
	        .productSku(v.getProductSku())
	        .onHand(v.getOnHand())
	        .reserved(v.getReserved())
	        .createdAt(v.getCreatedAt())
	        .updatedAt(v.getUpdatedAt())
	        .build();
	}
}
