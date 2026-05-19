package com.example.milkteasystem.mapper;

import com.example.milkteasystem.entity.Inventory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 库存表 Mapper 接口
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-02-27
 */
@Mapper
public interface InventoryMapper extends BaseMapper<Inventory> {

    /**
     * 原子扣减库存（使用 SQL 行级锁防止超卖）
     * @return 影响行数（0 表示库存不足）
     */
    @Update("UPDATE inventory SET stock = stock - #{quantity}, sold = sold + #{quantity} WHERE product_id = #{productId} AND stock >= #{quantity}")
    int deductStockAtomic(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * 原子回滚库存
     * @return 影响行数
     */
    @Update("UPDATE inventory SET stock = stock + #{quantity}, sold = sold - #{quantity} WHERE product_id = #{productId} AND sold >= #{quantity}")
    int rollbackStockAtomic(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
