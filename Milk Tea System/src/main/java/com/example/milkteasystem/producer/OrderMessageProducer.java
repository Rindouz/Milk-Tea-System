package com.example.milkteasystem.producer;

import com.example.milkteasystem.message.InventoryDeductMessage;
import com.example.milkteasystem.message.OrderCreateMessage;
import com.example.milkteasystem.message.OrderStatusUpdateMessage;
import com.example.milkteasystem.message.OrderCancelMessage;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class OrderMessageProducer {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    // 发送库存扣减消息
    public void sendInventoryDeductMessage(InventoryDeductMessage message) {
        rocketMQTemplate.convertAndSend("inventory-deduct-topic", message);
    }

    // 发送订单创建消息
    public void sendOrderCreateMessage(OrderCreateMessage message) {
        rocketMQTemplate.convertAndSend("order-create-topic", message);
    }

    // 发送订单状态更新消息
    public void sendOrderStatusUpdateMessage(OrderStatusUpdateMessage message) {
        rocketMQTemplate.convertAndSend("order-status-update-topic", message);
    }

    // 发送订单取消消息
    public void sendOrderCancelMessage(OrderCancelMessage message) {
        rocketMQTemplate.convertAndSend("order-cancel-topic", message);
    }

}
