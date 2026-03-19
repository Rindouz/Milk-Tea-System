package com.example.milkteasystem.service.impl;

import com.example.milkteasystem.entity.Inventory;
import com.example.milkteasystem.mapper.InventoryMapper;
import com.example.milkteasystem.service.IInventoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

    @Override
    public Integer getStockByProductId(Long productId) {
        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId);
        Inventory inventory = baseMapper.selectOne(queryWrapper);
        return inventory != null ? inventory.getStock() : 0;
    }

    @Override
    public boolean deductStock(Long productId, Integer quantity) {
        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId);
        Inventory inventory = baseMapper.selectOne(queryWrapper);
        if (inventory == null || inventory.getStock() < quantity) {
            return false;
        }
        inventory.setStock(inventory.getStock() - quantity);
        inventory.setSold(inventory.getSold() + quantity);
        return baseMapper.updateById(inventory) > 0;
    }

    @Override
    public boolean rollbackStock(Long productId, Integer quantity) {
        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId);
        Inventory inventory = baseMapper.selectOne(queryWrapper);
        if (inventory == null) {
            return false;
        }
        inventory.setStock(inventory.getStock() + quantity);
        inventory.setSold(inventory.getSold() - quantity);
        return baseMapper.updateById(inventory) > 0;
    }

    @Override
    public Integer getSoldCount(Long productId) {
        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId);
        Inventory inventory = baseMapper.selectOne(queryWrapper);
        return inventory != null ? inventory.getSold() : 0;
    }

    @Override
    public boolean initInventory(Long productId, Integer initialStock) {
        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId);
        Inventory existingInventory = baseMapper.selectOne(queryWrapper);
        if (existingInventory != null) {
            // 已存在库存记录，更新库存
            existingInventory.setStock(initialStock);
            return baseMapper.updateById(existingInventory) > 0;
        } else {
            // 不存在库存记录，创建新记录
            Inventory inventory = new Inventory();
            inventory.setProductId(productId);
            inventory.setStock(initialStock);
            inventory.setSold(0);
            return baseMapper.insert(inventory) > 0;
        }
    }
}

