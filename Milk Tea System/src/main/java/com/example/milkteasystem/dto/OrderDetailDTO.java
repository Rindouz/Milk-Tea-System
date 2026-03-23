package com.example.milkteasystem.dto;

import com.example.milkteasystem.entity.OrderItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class OrderDetailDTO {
    private Long orderId;
    private String orderNo;
    private Long userId;
    private Long storeId;
    private BigDecimal totalAmount;
    private Byte orderStatus;
    private String remark;
    private String takeName;
    private String takePhone;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private List<OrderItem> orderItems;
}