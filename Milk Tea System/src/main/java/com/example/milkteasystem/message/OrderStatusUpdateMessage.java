package com.example.milkteasystem.message;

import lombok.Data;

/**
 * 订单状态更新消息
 */
@Data
public class OrderStatusUpdateMessage {
    /**
     * 订单编号
     */
    private String orderNo;
    
    /**
     * 订单状态
     */
    private byte orderStatus;
    
    /**
     * 状态更新时间
     */
    private long timestamp;
}
