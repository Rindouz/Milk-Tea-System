package com.example.milkteasystem.consumer;

import com.example.milkteasystem.message.OrderCreateMessage;
import com.example.milkteasystem.service.IOrdersService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单创建消费者
 */
@Component
@RocketMQMessageListener(topic = "order-create-topic", consumerGroup = "order-create-consumer-group")
public class OrderCreateConsumer implements RocketMQListener<OrderCreateMessage> {
    
    @Autowired
    private IOrdersService ordersService;
    
    @Override
    public void onMessage(OrderCreateMessage message) {
        try {
            // 异步处理订单创建
            ordersService.createOrder(message.getOrderCreateDTO());
            System.out.println("订单创建处理成功: " + message.getOrderCreateDTO());
        } catch (Exception e) {
            // 处理异常，可实现重试机制或死信队列
            System.err.println("订单创建处理失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
