package com.example.milkteasystem.message;

import lombok.Data;

/**
 * 订单取消消息
 */
@Data
public class OrderCancelMessage {
    /**
     * 订单编号
     */
    private String orderNo;
    
    /**
     * 取消原因
     */
    private String cancelReason;
    
    /**
     * 消息发送时间
     */
    private long timestamp;
}
