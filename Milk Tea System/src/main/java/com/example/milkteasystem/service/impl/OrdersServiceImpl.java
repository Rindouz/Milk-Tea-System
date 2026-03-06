package com.example.milkteasystem.service.impl;

import com.example.milkteasystem.entity.Orders;
import com.example.milkteasystem.mapper.OrdersMapper;
import com.example.milkteasystem.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-02-27
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

}
