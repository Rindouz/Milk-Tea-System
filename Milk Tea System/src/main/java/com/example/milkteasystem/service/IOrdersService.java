package com.example.milkteasystem.service;

import com.example.milkteasystem.dto.OrderCreateDTO;
import com.example.milkteasystem.dto.OrderDetailDTO;
import com.example.milkteasystem.entity.OrderItem;
import com.example.milkteasystem.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-02-27
 */
public interface IOrdersService extends IService<Orders> {
    @Transactional


    //创建订单
    Orders createOrder(OrderCreateDTO orderCreateDTO);
    //订单支付
    boolean payOrder(String orderNo);
    //取消订单
    boolean cancelOrder(String orderNo);
    //确认取餐
    boolean confirmOrder(String orderNo);
    //查询用户订单列表
    List<Orders> getUserOrders(Long userId, Byte status);
    //分页查询用户订单列表
    Page<Orders> getUserOrdersPage(Integer page, Integer size, Long userId, Byte status);
    //查询订单详情
    OrderDetailDTO getOrderDetail(String orderNo);
}

