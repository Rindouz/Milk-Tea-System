package com.example.milkteasystem.service.impl;

import com.example.milkteasystem.entity.Inventory;
import com.example.milkteasystem.mapper.InventoryMapper;
import com.example.milkteasystem.service.IInventoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    /**
     * 查询商品库存
     * @param productId 商品ID
     * @return 库存数量
     */
    @Override
    public Integer getStockByProductId(Long productId) {
        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId);
        Inventory inventory = baseMapper.selectOne(queryWrapper);
        return inventory != null ? inventory.getStock() : 0;
    }

    /**
     * 扣库存（原子操作，防止并发超卖）
     * @param productId 商品ID
     * @param quantity  扣库存数量
     * @return 是否成功
     */
    @Override
    public boolean deductStock(Long productId, Integer quantity) {
        return baseMapper.deductStockAtomic(productId, quantity) > 0;
    }
    /**
     * 回滚库存（原子操作）
     * @param productId 商品ID
     * @param quantity  回滚库存数量
     * @return 是否成功
     */
    @Override
    public boolean rollbackStock(Long productId, Integer quantity) {
        return baseMapper.rollbackStockAtomic(productId, quantity) > 0;
    }
    /**
     * 查询商品已售数量
     * @param productId 商品ID
     * @return 已售数量
     */
    @Override
    public Integer getSoldCount(Long productId) {
        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId);
        Inventory inventory = baseMapper.selectOne(queryWrapper);
        return inventory != null ? inventory.getSold() : 0;
    }
    /**
     * 初始化商品库存
     * @param productId 商品ID
     * @param initialStock  初始库存数量
     * @return 是否成功
     */
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
    /**
     * 增加或修改库存
     * @param inventory 库存对象
     * @return 新增的库存对象
     */
    @Override
    public Inventory saveOrUpdateInventory(Inventory inventory) {
        // 1. 根据商品ID productId 查询是否已有库存记录
        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", inventory.getProductId());

        Inventory existInventory = baseMapper.selectOne(queryWrapper);

        if (existInventory != null) {
            // 2. 已存在 → 更新：把新的库存、销量设置进去，保留主键ID
            existInventory.setStock(inventory.getStock());
            existInventory.setSold(inventory.getSold());
            // 时间会自动填充，不用手动 set
            baseMapper.updateById(existInventory);

            return existInventory; // 返回更新后的对象
        } else {
            // 3. 不存在 → 插入新增
            baseMapper.insert(inventory);
            return inventory; // 返回新增后的对象
        }
    }

    @Override
    public Page<Inventory> getInventoryPage(Integer page, Integer size) {
        Page<Inventory> inventoryPage = new Page<>(page, size);
        return baseMapper.selectPage(inventoryPage, null);
    }



}

