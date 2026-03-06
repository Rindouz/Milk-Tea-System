package com.example.milkteasystem.service.impl;

import com.example.milkteasystem.entity.Inventory;
import com.example.milkteasystem.mapper.InventoryMapper;
import com.example.milkteasystem.service.IInventoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 库存表 服务实现类
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-02-27
 */
@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements IInventoryService {

}
