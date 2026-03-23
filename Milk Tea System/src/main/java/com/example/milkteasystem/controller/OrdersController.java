package com.example.milkteasystem.controller;

import com.example.milkteasystem.dto.OrderCreateDTO;
import com.example.milkteasystem.dto.OrderDetailDTO;
import com.example.milkteasystem.entity.Orders;
import com.example.milkteasystem.service.IOrdersService;
import com.example.milkteasystem.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-03-01
 */
@RestController
@RequestMapping("/milkteasystem/orders")
public class OrdersController {

    @Autowired
    private IOrdersService ordersService;

    // 创建订单
    @PostMapping("/create")
    public Result createOrder(@RequestBody OrderCreateDTO orderCreateDTO) {
        Orders order = ordersService.createOrder(orderCreateDTO);
        return Result.success(order);
    }

    // 订单支付
    @PostMapping("/pay/{orderNo}")
    public Result payOrder(@PathVariable String orderNo) {
        boolean result = ordersService.payOrder(orderNo);
        if (result) {
            return Result.success("支付成功");
        } else {
            return Result.error("支付失败");
        }
    }

    // 取消订单
    @PostMapping("/cancel/{orderNo}")
    public Result cancelOrder(@PathVariable String orderNo) {
        boolean result = ordersService.cancelOrder(orderNo);
        if (result) {
            return Result.success("取消成功");
        } else {
            return Result.error("取消失败");
        }
    }

    // 确认取餐
    @PostMapping("/confirm/{orderNo}")
    public Result confirmOrder(@PathVariable String orderNo) {
        boolean result = ordersService.confirmOrder(orderNo);
        if (result) {
            return Result.success("取餐成功");
        } else {
            return Result.error("取餐失败");
        }
    }

    // 查询用户订单列表
    @GetMapping("/user/{userId}")
    public Result getUserOrders(@PathVariable Long userId, @RequestParam(required = false) Byte status) {
        List<Orders> orders = ordersService.getUserOrders(userId, status);
        return Result.success(orders);
    }

    // 查询订单详情
    @GetMapping("/detail/{orderNo}")
    public Result getOrderDetail(@PathVariable String orderNo) {
        OrderDetailDTO orderDetail = ordersService.getOrderDetail(orderNo);
        return Result.success(orderDetail);
    }

}
