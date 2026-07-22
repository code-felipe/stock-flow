package com.stockflow.backend.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(
    name = "LowStockProductDTO",
    description = "DTO that checks the low stock. Which products need restocking?"
)
public class LowStockProductDTO {

    private String productName;

    private Integer quantity;

    private Integer minStock;

    private Double stockPercentage;


    public LowStockProductDTO(
            String productName,
            Integer quantity,
            Integer minStock
    ) {
        this.productName = productName;
        this.quantity = quantity;
        this.minStock = minStock;

        this.stockPercentage =
                (minStock != null && minStock > 0)
                ? (quantity.doubleValue() / minStock.doubleValue()) * 100
                : 0.0;
    }
}
