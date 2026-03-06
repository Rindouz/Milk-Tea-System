package com.example.milkteasystem.service.impl;

import com.example.milkteasystem.entity.OrderItem;
import com.example.milkteasystem.mapper.OrderItemMapper;
import com.example.milkteasystem.service.IOrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单详情表 服务实现类
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-02-27
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements IOrderItemService {

}
