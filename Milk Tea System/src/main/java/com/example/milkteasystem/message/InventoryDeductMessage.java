package com.example.milkteasystem.message;
import lombok.Data;

@Data
public class InventoryDeductMessage {
    private Long orderId;
    private Long productId;
    private Integer quantity;
}