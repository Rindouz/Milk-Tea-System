package com.example.milkteasystem.mapper;

import com.example.milkteasystem.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-02-27
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

}
