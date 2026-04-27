package com.stockflow.backend.utils.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.stockflow.backend.category.domain.Category;
import com.stockflow.backend.category.dto.CategoryDTO;
import com.stockflow.backend.inventory.domain.Inventory;
import com.stockflow.backend.inventory.dto.create.InventoryCreateRequestDTO;
import com.stockflow.backend.inventory.dto.create.InventoryCreateResponseDTO;
import com.stockflow.backend.inventory.dto.delete.InventoryDeleteResponseDTO;
import com.stockflow.backend.inventory.dto.summary.InventorySummaryDTO;
import com.stockflow.backend.inventory.dto.update.InventoryUpdateResponseDTO;
import com.stockflow.backend.order.domain.Order;
import com.stockflow.backend.order.dto.create.OrderCreateResponsetDTO;
import com.stockflow.backend.order.dto.summary.OrderDetailedResponseDTO;
import com.stockflow.backend.order.dto.summary.OrderSummaryResponseDTO;
import com.stockflow.backend.orderItem.domain.OrderItem;
import com.stockflow.backend.orderItem.dto.create.OrderItemResponseDTO;
import com.stockflow.backend.product.domain.Product;
import com.stockflow.backend.product.dto.create.ProductCreateRequestDTO;
import com.stockflow.backend.product.dto.create.ProductCreateResponseDTO;
import com.stockflow.backend.product.dto.detail.ProductDetailDTO;
import com.stockflow.backend.product.dto.summary.ProductStockDTO;
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
				.createdAt(product.getCreatedAt())
				.build();
	}
	
	public static ProductStockDTO toSummaryDTO(Product product, Inventory inv) {
		
		return ProductStockDTO.builder()
				.productId(product.getId())
				.productName(product.getName())
				.productDescription(product.getDescription())
				.productPrice(product.getPrice())
				.productSku(product.getSku())
				.productImageUrl(product.getImageUrl())
				.productIsActive(product.getActive())
				.ProductDiscontinuedAt(product.getDiscontinuedAt())
				.onHand(inv != null ? inv.getOnHand() : 0)
				.reserved(inv != null ? inv.getReserved() : 0)
	            .productCreatedAt(product.getCreatedAt())
	            .updatedAt(inv != null ? inv.getUpdatedAt() : null)
				.createdAt(inv != null ? inv.getCreatedAt() : null)
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
				.createdAt(product.getCreatedAt())
				.categoryIds(
						product.getCategories() == null ? null : 
							product.getCategories().stream()
							.map(Category::getId)
							.collect(Collectors.toSet())
						)
				.build();
	}
	
	public static Product toDetail(ProductDetailDTO dto) {
		return Product.builder()
				.id(dto.getId())
				.name(dto.getName())
				.description(dto.getDescription())
				.price(dto.getPrice())
				.sku(dto.getSku())
				.imageUrl(dto.getImageUrl())
				.active(dto.getActive())
				.discontinuedAt(dto.getDiscontinuedAt())
				.createdAt(dto.getCreatedAt())
				.categories(null) // services looks for the categories.
				.build();
	}
	
	public static ProductCreateResponseDTO createProductResponse(Product product) {
		
		return ProductCreateResponseDTO.builder()
				.id(product.getId())
				.name(product.getName())
				.description(product.getDescription())
				.price(product.getPrice())
				.sku(product.getSku())
				.imageUrl(product.getImageUrl())
				.active(product.getActive())
				.discontinuedAt(product.getDiscontinuedAt())
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
	
	public static Store summaryEntity(StoreSummaryDTO dto) {
		return Store.builder()
				.id(dto.getId())
				.name(dto.getName())
				.description(dto.getDescription())
				.address(dto.getAddress())
				.createdAt(dto.getCreatedAt())
				.build();
	}
	
	//==== INVENTORY ====
	
	public static InventorySummaryDTO toSummaryDTO(ProductStockView v) {
	    return InventorySummaryDTO.builder()
	        .productId(v.getProductId())
	        .productName(v.getProductName())
	        .productPrice(v.getProductPrice())
	        .productImageUrl(v.getProductImageUrl())
	        .productDescription(v.getProductDescription())
	        .productSku(v.getProductSku())
	        .onHand(v.getOnHand())
	        .reserved(v.getReserved())
	        .createdAt(v.getCreatedAt())
	        .updatedAt(v.getUpdatedAt())
	        .build();
	}
	
	
	public static InventoryCreateResponseDTO createInventoryResponse(Inventory inventory) {
		return InventoryCreateResponseDTO.builder()
				.productId(inventory.getProduct().getId())
				.storeId(inventory.getStore().getId())
				.onHand(inventory.getOnHand())
				.reserved(inventory.getReserved())
				.createdAt(inventory.getCreatedAt())
				.build();
	}
	
	public static InventoryUpdateResponseDTO updateInventoryResponse(Inventory inventory) {
		return InventoryUpdateResponseDTO.builder()
				.productId(inventory.getProduct().getId())
				.storeId(inventory.getStore().getId())
				.onHand(inventory.getOnHand())
				.reserved(inventory.getReserved())
				.build();
	}
	
	public static InventoryDeleteResponseDTO deleteInventoryResponse(Inventory inventory) {
		return InventoryDeleteResponseDTO.builder()
				.productId(inventory.getProduct().getId())
				.storeId(inventory.getStore().getId())
				.onHand(inventory.getOnHand())
				.reserved(inventory.getReserved())
				.build();
	}
	
	//==== ORDER ====
	
	public static OrderCreateResponsetDTO createOrderResponse(Order order) {
		return OrderCreateResponsetDTO.builder()
				.orderStatus(order.getOrderStatus())
				.total(order.getTotal())
				.storeName(order.getStore().getName())
				.storeAddress(order.getStore().getAddress())
				.orderDate(order.getOrderDate())
				.items(mapOrderItems(order.getOrderItems()))
				.build();
	}
	
	public static OrderSummaryResponseDTO toSummaryDTO(Order order) {
		return OrderSummaryResponseDTO.builder()
				.id(order.getId())
				.orderStatus(order.getOrderStatus())
				.orderDate(order.getOrderDate())
				.total(order.getTotal())
				.storeName(order.getStore().getName())
				.storeAddress(order.getStore().getAddress())
				.build();
				
	}
	
	public static OrderDetailedResponseDTO toDetail(Order order) {
		return OrderDetailedResponseDTO.builder()
				.id(order.getId())
				.orderStatus(order.getOrderStatus())
				.orderDate(order.getOrderDate())
				.total(order.getTotal())
				.storeName(order.getStore().getName())
				.storeAddress(order.getStore().getAddress())
				.items(mapOrderItems(order.getOrderItems()))
				.build();
	}
	
	private static Set<OrderItemResponseDTO> mapOrderItems(List<OrderItem> items){
		if(items == null || items.isEmpty()) return null;
		
		return items.stream()
				.map(item -> OrderItemResponseDTO.builder()
						.productName(item.getInventory().getProduct().getName())
						.quantity(item.getQuantity())
						.unitPrice(item.getUnitPrice())
						.subtotal(item.getUnitPrice() * item.getQuantity())
						.build())
				.collect(Collectors.toSet());
		
	}
}
