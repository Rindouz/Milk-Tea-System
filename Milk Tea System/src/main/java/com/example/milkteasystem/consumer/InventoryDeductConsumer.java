package com.example.milkteasystem.consumer;

import com.example.milkteasystem.message.InventoryDeductMessage;
import com.example.milkteasystem.service.IInventoryService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "inventory-deduct-topic", consumerGroup = "inventory-deduct-consumer-group")
public class InventoryDeductConsumer implements RocketMQListener<InventoryDeductMessage> {
@Autowired
private IInventoryService inventoryService;
    @Override
    public void onMessage(InventoryDeductMessage message) {
        // 扣库存
        boolean success = inventoryService.deductStock(message.getProductId(), message.getQuantity());
        if (!success) {
            // 可以实现重试机制或死信队列处理
            System.out.println("库存扣减失败: 商品ID=" + message.getProductId() + ", 数量=" + message.getQuantity());
        }
    }
}
