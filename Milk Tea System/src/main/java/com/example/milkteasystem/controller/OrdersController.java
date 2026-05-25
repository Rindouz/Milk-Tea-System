package com.example.milkteasystem.controller;

import com.example.milkteasystem.dto.OrderCreateDTO;
import com.example.milkteasystem.dto.OrderDetailDTO;
import com.example.milkteasystem.entity.Orders;
import com.example.milkteasystem.service.IOrdersService;
import com.example.milkteasystem.common.Result;
import com.example.milkteasystem.message.OrderCreateMessage;
import com.example.milkteasystem.message.OrderStatusUpdateMessage;
import com.example.milkteasystem.message.OrderCancelMessage;
import com.example.milkteasystem.producer.OrderMessageProducer;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    @Autowired
    private OrderMessageProducer orderMessageProducer;

    // 创建订单
    @PostMapping("/create")
    public Result createOrder(@RequestBody OrderCreateDTO orderCreateDTO) {
        // 发送订单创建消息，异步处理
        OrderCreateMessage message = new OrderCreateMessage();
        message.setOrderCreateDTO(orderCreateDTO);
        message.setTimestamp(System.currentTimeMillis());
        orderMessageProducer.sendOrderCreateMessage(message);
        return Result.success("订单创建请求已提交，正在处理");
    }

    // 订单支付
    @PostMapping("/pay/{orderNo}")
    public Result payOrder(@PathVariable String orderNo) {
        // 发送订单状态更新消息，异步处理
        OrderStatusUpdateMessage message = new OrderStatusUpdateMessage();
        message.setOrderNo(orderNo);
        message.setOrderStatus((byte) 1); // 支付成功
        message.setTimestamp(System.currentTimeMillis());
        orderMessageProducer.sendOrderStatusUpdateMessage(message);
        return Result.success("支付请求已提交，正在处理");
    }

    // 取消订单
    @PostMapping("/cancel/{orderNo}")
    public Result cancelOrder(@PathVariable String orderNo) {
        // 发送订单取消消息，异步处理
        OrderCancelMessage message = new OrderCancelMessage();
        message.setOrderNo(orderNo);
        message.setCancelReason("用户主动取消");
        message.setTimestamp(System.currentTimeMillis());
        orderMessageProducer.sendOrderCancelMessage(message);
        return Result.success("取消请求已提交，正在处理");
    }

    // 确认取餐
    @PostMapping("/confirm/{orderNo}")
    public Result confirmOrder(@PathVariable String orderNo) {
        OrderStatusUpdateMessage message = new OrderStatusUpdateMessage();
        message.setOrderNo(orderNo);
        message.setOrderStatus((byte) 2);
        message.setTimestamp(System.currentTimeMillis());
        orderMessageProducer.sendOrderStatusUpdateMessage(message);
        return Result.success("取餐请求已提交，正在处理");
    }

    // 开始制作
    @PostMapping("/make/{orderNo}")
    public Result makeOrder(@PathVariable String orderNo) {
        OrderStatusUpdateMessage message = new OrderStatusUpdateMessage();
        message.setOrderNo(orderNo);
        message.setOrderStatus((byte) 3);
        message.setTimestamp(System.currentTimeMillis());
        orderMessageProducer.sendOrderStatusUpdateMessage(message);
        return Result.success("制作请求已提交，正在处理");
    }

    // 查询订单列表
    @GetMapping("/user/{userId}")
    public Result getUserOrders(@PathVariable Long userId, @RequestParam(required = false) Byte status) {
        List<Orders> orders = ordersService.getUserOrders(userId, status);
        return Result.success(orders);
    }
    // 分页查询订单列表
    @GetMapping("/user/page/{userId}")
    public Result getUserOrdersPage(@PathVariable Long userId, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size, @RequestParam(required = false) Byte status) {
        Page<Orders> ordersPage = ordersService.getUserOrdersPage(page, size, userId, status);
        return Result.success(ordersPage);
    }

    // 查询订单详情
    @GetMapping("/detail/{orderNo}")
    public Result getOrderDetail(@PathVariable String orderNo) {
        OrderDetailDTO orderDetail = ordersService.getOrderDetail(orderNo);
        return Result.success(orderDetail);
    }

    // 更新订单状态
    @PostMapping("/updateStatus/{orderNo}")
    public Result updateOrderStatus(@PathVariable String orderNo, @RequestParam Byte status) {
        OrderStatusUpdateMessage message = new OrderStatusUpdateMessage();
        message.setOrderNo(orderNo);
        message.setOrderStatus(status);
        message.setTimestamp(System.currentTimeMillis());
        orderMessageProducer.sendOrderStatusUpdateMessage(message);
        return Result.success("状态更新请求已提交，正在处理");
    }

    // 管理员分页查询所有订单
    @GetMapping("/admin/page")
    public Result getAllOrdersPage(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size,
                                   @RequestParam(required = false) Byte status,
                                   @RequestParam(required = false) Long storeId) {
        Page<Orders> ordersPage = ordersService.getAllOrdersPage(page, size, status, storeId);
        return Result.success(ordersPage);
    }

}
