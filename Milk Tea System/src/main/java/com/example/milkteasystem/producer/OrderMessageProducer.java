package com.example.milkteasystem.producer;

import com.example.milkteasystem.message.InventoryDeductMessage;
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

}
