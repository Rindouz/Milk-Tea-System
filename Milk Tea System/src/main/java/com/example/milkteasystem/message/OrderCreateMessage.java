package com.example.milkteasystem.message;

import com.example.milkteasystem.dto.OrderCreateDTO;
import lombok.Data;

/**
 * 订单创建消息
 */
@Data
public class OrderCreateMessage {
    /**
     * 订单创建DTO
     */
    private OrderCreateDTO orderCreateDTO;
    
    /**
     * 消息发送时间
     */
    private long timestamp;
}
