package com.example.milkteasystem.consumer;

import com.example.milkteasystem.message.OrderCancelMessage;
import com.example.milkteasystem.service.IOrdersService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单取消消费者
 */
@Component
@RocketMQMessageListener(topic = "order-cancel-topic", consumerGroup = "order-cancel-consumer-group")
public class OrderCancelConsumer implements RocketMQListener<OrderCancelMessage> {
    
    @Autowired
    private IOrdersService ordersService;
    
    @Override
    public void onMessage(OrderCancelMessage message) {
        try {
            // 异步处理订单取消及库存回滚
            boolean success = ordersService.cancelOrder(message.getOrderNo());
            if (success) {
                System.out.println("订单取消处理成功: 订单号=" + message.getOrderNo() + ", 原因=" + message.getCancelReason());
            } else {
                System.out.println("订单取消处理失败: 订单号=" + message.getOrderNo());
            }
        } catch (Exception e) {
            // 处理异常，可实现重试机制或死信队列
            System.err.println("订单取消处理异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
