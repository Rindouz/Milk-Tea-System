package com.example.milkteasystem.dto;

import java.util.List;
import lombok.Data;

@Data
public class OrderCreateDTO {
    private Long userId;
    private Long storeId;
    private String remark;
    private String takeName;
    private String takePhone;
    private List<OrderItemDTO> orderItems;
}