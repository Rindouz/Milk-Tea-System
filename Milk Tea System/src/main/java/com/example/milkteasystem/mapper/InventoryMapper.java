package com.example.milkteasystem.mapper;

import com.example.milkteasystem.entity.Inventory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

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

}
