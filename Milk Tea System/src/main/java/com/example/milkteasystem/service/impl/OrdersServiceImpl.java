package com.example.milkteasystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.milkteasystem.dto.OrderCreateDTO;
import com.example.milkteasystem.dto.OrderDetailDTO;
import com.example.milkteasystem.dto.OrderItemDTO;
import com.example.milkteasystem.entity.OrderItem;
import com.example.milkteasystem.entity.Orders;
import com.example.milkteasystem.mapper.OrdersMapper;
import com.example.milkteasystem.message.InventoryDeductMessage;
import com.example.milkteasystem.producer.OrderMessageProducer;
import com.example.milkteasystem.service.IInventoryService;
import com.example.milkteasystem.service.IOrderItemService;
import com.example.milkteasystem.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.milkteasystem.service.IProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private IOrderItemService orderItemService;

    @Autowired
    private IInventoryService inventoryService;

    @Autowired
    private IProductService productService;
    @Autowired
    private OrderMessageProducer orderMessageProducer;

    // 生成唯一订单号（雪花算法）
    private String generateOrderNo() {
        return String.valueOf(IdWorker.getId());
    }
    // 根据订单号查询订单
    private Orders getByOrderNo(String orderNo) {
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        return getOne(queryWrapper);
    }


    @Transactional
    @Override
    public Orders createOrder(OrderCreateDTO orderCreateDTO) {
        // 生成唯一订单号
        String orderNo = generateOrderNo();
        
        // 创建订单对象
        Orders order = new Orders();
        order.setOrderNo(orderNo);
        order.setUserId(orderCreateDTO.getUserId());
        order.setStoreId(orderCreateDTO.getStoreId());
        order.setRemark(orderCreateDTO.getRemark());
        order.setTakeName(orderCreateDTO.getTakeName());
        order.setTakePhone(orderCreateDTO.getTakePhone());
        order.setOrderStatus((byte) 0); // 待支付
        
        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (OrderItemDTO itemDTO : orderCreateDTO.getOrderItems()) {
            OrderItem item = new OrderItem();
            item.setProductId(itemDTO.getProductId());
            item.setProductName(itemDTO.getProductName());
            item.setProductImage(itemDTO.getProductImage());
            item.setPrice(itemDTO.getPrice());
            item.setQuantity(itemDTO.getQuantity());
            item.setSubtotal(itemDTO.getPrice().multiply(new BigDecimal(itemDTO.getQuantity())));
            totalAmount = totalAmount.add(item.getSubtotal());
            orderItems.add(item);
        }
        
        order.setTotalAmount(totalAmount);
        
        // 保存订单
        save(order);

        // 批量保存订单项
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getOrderId());
            // 发送库存扣减消息
            InventoryDeductMessage message = new InventoryDeductMessage();
            message.setOrderId(order.getOrderId());
            message.setProductId(item.getProductId());
            message.setQuantity(item.getQuantity());
            orderMessageProducer.sendInventoryDeductMessage(message);
        }
        orderItemService.saveBatch(orderItems);
        
        return order;
    }

    @Override
    @Transactional
    public boolean payOrder(String orderNo) {
        Orders order = getByOrderNo(orderNo);
        if (order == null || order.getOrderStatus() != 0) {
            return false;
        }
        order.setOrderStatus((byte) 1);
        order.setPayTime(LocalDateTime.now());
        return updateById(order);
    }

    @Override
    @Transactional
    public boolean cancelOrder(String orderNo) {
        Orders order = getByOrderNo(orderNo);
        if (order == null) {
            return false;
        }
        byte status = order.getOrderStatus();
        if (status != 0 && status != 1 && status != 3) {
            return false;
        }

        // 查询订单商品
        List<OrderItem> orderItems = orderItemService.getByOrderId(order.getOrderId());
        // 恢复库存
        for (OrderItem item : orderItems) {
            inventoryService.rollbackStock(item.getProductId(), item.getQuantity());
        }

        order.setOrderStatus((byte) 4);
        return updateById(order);
    }

    @Override
    @Transactional
    public boolean confirmOrder(String orderNo) {
        Orders order = getByOrderNo(orderNo);
        if (order == null) {
            return false;
        }
        byte status = order.getOrderStatus();
        if (status != 1 && status != 3) {
            return false;
        }
        order.setOrderStatus((byte) 2);
        return updateById(order);
    }


    @Override
    @Transactional
    //制作订单
    public boolean makeOrder(String orderNo) {
        Orders order = getByOrderNo(orderNo);
        if (order == null || order.getOrderStatus() != 1) {
            return false;
        }
        order.setOrderStatus((byte) 3);
        return updateById(order);
    }

    @Override
    public List<Orders> getUserOrders(Long userId, Byte status) {
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        if (status != null) {
            queryWrapper.eq("order_status", status);
        }
        queryWrapper.orderByDesc("create_time");
        return list(queryWrapper);
    }

    @Override
    public Page<Orders> getUserOrdersPage(Integer page, Integer size, Long userId, Byte status) {
        Page<Orders> ordersPage = new Page<>(page, size);
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        if (status != null) {
            queryWrapper.eq("order_status", status);
        }
        queryWrapper.orderByDesc("create_time");
        return baseMapper.selectPage(ordersPage, queryWrapper);
    }

    @Override
    public OrderDetailDTO getOrderDetail(String orderNo) {
        Orders order = getByOrderNo(orderNo);
        if (order == null) {
            return null;
        }
        
        // 构建订单详情DTO
        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
        BeanUtils.copyProperties(order, orderDetailDTO);
        
        // 加载订单项
        List<OrderItem> orderItems = orderItemService.getByOrderId(order.getOrderId());
        orderDetailDTO.setOrderItems(orderItems);
        
        return orderDetailDTO;
    }

    @Override
    public int updateOrderStatus(String orderNo, Byte status) {
        Orders order = getByOrderNo(orderNo);
        if (order == null) {
            return 0;
        }
        boolean success = false;
        switch (status) {
            case 1: // 待取餐（支付成功）
                success = payOrder(orderNo);
                break;
            case 2: // 已完成（确认取餐）
                success = confirmOrder(orderNo);
                break;
            case 4: // 已取消
                success = cancelOrder(orderNo);
                break;
            case 3: // 正在制作制作
                success = makeOrder(orderNo);
                break;
            default:
                throw new IllegalArgumentException("不支持的订单状态: " + status);
        }
        return success ? 1 : 0;
    }
}
