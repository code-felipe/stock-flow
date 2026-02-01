package com.stockflow.backend.product.mapper;

import com.stockflow.backend.product.domain.Product;
import com.stockflow.backend.product.dto.ProductDTO;

public class Mapper {
	
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
				.build();
	}

}
