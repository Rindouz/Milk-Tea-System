
package com.example.milkteasystem.task;

import com.example.milkteasystem.entity.Orders;
import com.example.milkteasystem.service.IOrdersService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
/*
 * 订单状态检查定时任务
 * 用于自动处理超时未支付的订单
 */
public class OrderStatusCheckTask {

    @Autowired
    private IOrdersService ordersService;

    /**
     * 定时检查订单状态
     * 每5分钟执行一次
     * cron表达式：0 0/5 * * * ?
     * - 0：秒
     * - 0/5：分钟，每5分钟执行一次
     * - *：小时，每天
     * - *：月份，每月
     * - *：星期，每天
     * - ?：年份，无指定
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void checkOrderStatus() {
        // 计算30分钟前的时间点
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minus(30, ChronoUnit.MINUTES);

        // 构建查询条件
        // 查询条件：订单状态为待支付(0)且创建时间早于30分钟前
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_status", 0)  // 订单状态为待支付
                .lt("create_time", thirtyMinutesAgo);  // 创建时间早于30分钟前

        // 执行查询，获取符合条件的订单列表
        List<Orders> orders = ordersService.list(queryWrapper);

        // 遍历处理每个超时订单
        for (Orders order : orders) {
            // 取消订单并回滚库存
            ordersService.cancelOrder(order.getOrderNo());
        }
    }
}
