package com.example.milkteasystem.consumer;

import com.example.milkteasystem.message.OrderStatusUpdateMessage;
import com.example.milkteasystem.service.IOrdersService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单状态更新消费者
 */
@Component
@RocketMQMessageListener(topic = "order-status-update-topic", consumerGroup = "order-status-update-consumer-group")
public class OrderStatusUpdateConsumer implements RocketMQListener<OrderStatusUpdateMessage> {
    
    @Autowired
    private IOrdersService ordersService;
    
    @Override
    public void onMessage(OrderStatusUpdateMessage message) {
        try {
            // 根据订单状态执行不同的处理逻辑
            switch (message.getOrderStatus()) {
                case 1: // 支付成功
                    ordersService.payOrder(message.getOrderNo());
                    break;
                case 2: // 订单完成
                    // 可以添加订单完成后的处理逻辑
                    ordersService.confirmOrder(message.getOrderNo());
                    break;
                case 4: // 订单取消
                    ordersService.cancelOrder(message.getOrderNo());
                    break;
                case 3: // 制作中
                    ordersService.makeOrder(message.getOrderNo());
                    break;
                default:
                    break;
            }
            System.out.println("订单状态更新处理成功: 订单号=" + message.getOrderNo() + ", 状态=" + message.getOrderStatus());
        } catch (Exception e) {
            // 处理异常，可实现重试机制或死信队列
            System.err.println("订单状态更新处理失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
