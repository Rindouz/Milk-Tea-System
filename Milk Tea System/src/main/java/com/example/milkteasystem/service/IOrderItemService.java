package com.example.milkteasystem.service;

import com.example.milkteasystem.entity.OrderItem;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 * 订单详情表 服务类
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-02-27
 */
public interface IOrderItemService extends IService<OrderItem> {
    // 根据订单ID查询订单项
    List<OrderItem> getByOrderId(Long orderId);
}
