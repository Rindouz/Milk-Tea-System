package com.example.milkteasystem.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrderItemDTO {
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
}