package com.example.milkteasystem.controller;

import com.example.milkteasystem.entity.OrderItem;
import com.example.milkteasystem.service.IOrderItemService;
import com.example.milkteasystem.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * <p>
 * 订单详情表 前端控制器
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-03-01
 */
@RestController
@RequestMapping("/milkteasystem/order-item")
public class OrderItemController {

    @Autowired
    private IOrderItemService orderItemService;

    // 根据订单ID查询订单项
    @GetMapping("/order/{orderId}")
    public Result getOrderItems(@PathVariable Long orderId) {
        List<OrderItem> orderItems = orderItemService.getByOrderId(orderId);
        return Result.success(orderItems);
    }

}
