package com.example.milkteasystem.controller;

import com.example.milkteasystem.common.Result;
import com.example.milkteasystem.entity.Inventory;
import com.example.milkteasystem.service.IInventoryService;
import com.example.milkteasystem.util.RedisUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 库存表 前端控制器
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-03-01
 */
@RestController
@RequestMapping("/milkteasystem/inventory")
public class InventoryController {

    @Autowired
    private IInventoryService inventoryService;

    @Autowired
    private RedisUtil redisUtil;

    private static final String INVENTORY_STOCK_KEY_PREFIX = "inventory:stock:";
    private static final String INVENTORY_SOLD_KEY_PREFIX = "inventory:sold:";



    /**
     * 查询商品库存
     * @param productId 商品ID
     * @return 库存数量
     */
    @GetMapping("/stock/{productId}")
    public Result getStockByProductId(@PathVariable Long productId) {
        String key = INVENTORY_STOCK_KEY_PREFIX + productId;
        // 尝试从缓存获取
        Integer stock = (Integer) redisUtil.get(key);
        if (stock != null) {
            return Result.success(stock);
        }
        // 缓存未命中，从数据库获取
        stock = inventoryService.getStockByProductId(productId);
        // 存入缓存，设置过期时间为5分钟
        redisUtil.set(key, stock, 300);
        return Result.success(stock);
    }

    /**
     * 库存扣减（下单时）
     * @param productId 商品ID
     * @param quantity 扣减数量
     * @return 操作结果
     */
    @PutMapping("/deduct")
    public Result deductStock(@RequestParam Long productId, @RequestParam Integer quantity) {
        boolean result = inventoryService.deductStock(productId, quantity);
        if (result) {
            // 清除库存缓存
            redisUtil.delete(INVENTORY_STOCK_KEY_PREFIX + productId);
            // 清除已售数量缓存
            redisUtil.delete(INVENTORY_SOLD_KEY_PREFIX + productId);
        }
        return result ? Result.success() : Result.error("库存不足或操作失败");
    }

    /**
     * 库存回滚（订单取消时）
     * @param productId 商品ID
     * @param quantity 回滚数量
     * @return 操作结果
     */
    @PutMapping("/rollback")
    public Result rollbackStock(@RequestParam Long productId, @RequestParam Integer quantity) {
        boolean result = inventoryService.rollbackStock(productId, quantity);
        if (result) {
            // 清除库存缓存
            redisUtil.delete(INVENTORY_STOCK_KEY_PREFIX + productId);
            // 清除已售数量缓存
            redisUtil.delete(INVENTORY_SOLD_KEY_PREFIX + productId);
        }
        return result ? Result.success() : Result.error("操作失败");
    }

    /**
     * 已售数量统计
     * @param productId 商品ID
     * @return 已售数量
     */
    @GetMapping("/sold/{productId}")
    public Result getSoldCount(@PathVariable Long productId) {
        String key = INVENTORY_SOLD_KEY_PREFIX + productId;
        // 尝试从缓存获取
        Integer soldCount = (Integer) redisUtil.get(key);
        if (soldCount != null) {
            return Result.success(soldCount);
        }
        // 缓存未命中，从数据库获取
        soldCount = inventoryService.getSoldCount(productId);
        // 存入缓存，设置过期时间为5分钟
        redisUtil.set(key, soldCount, 300);
        return Result.success(soldCount);
    }

    /**
     * 库存初始化（新增商品时）
     * @param productId 商品ID
     * @param initialStock 初始库存
     * @return 操作结果
     */
    @PostMapping("/init")
    public Result initInventory(@RequestParam Long productId, @RequestParam Integer initialStock) {
        boolean result = inventoryService.initInventory(productId, initialStock);
        if (result) {
            // 清除库存缓存
            redisUtil.delete(INVENTORY_STOCK_KEY_PREFIX + productId);
            // 清除已售数量缓存
            redisUtil.delete(INVENTORY_SOLD_KEY_PREFIX + productId);
        }
        return result ? Result.success() : Result.error("初始化库存失败");
    }

    /**
     * 增加或修改库存
     * @param inventory 库存对象
     * @return 新增的库存对象
     */
    @PostMapping("/saveOrUpdate")
    public Result saveOrUpdateInventory(@RequestBody Inventory inventory) {
        Inventory savedInventory = inventoryService.saveOrUpdateInventory(inventory);
        if (savedInventory != null) {
            // 清除库存缓存
            redisUtil.delete(INVENTORY_STOCK_KEY_PREFIX + inventory.getProductId());
            // 清除已售数量缓存
            redisUtil.delete(INVENTORY_SOLD_KEY_PREFIX + inventory.getProductId());
        }
        return savedInventory != null ? Result.success(savedInventory) : Result.error("保存或更新库存失败");
    }

    /**
     * 查看所有库存
     * @return 所有库存列表
     */
    @GetMapping("/all")
    public Result getAllInventories() {
        List<Inventory> inventoryList = inventoryService.list();
        return Result.success(inventoryList);
    }

    /**
     * 分页查看库存
     * @param page 当前页码
     * @param size 每页大小
     * @return 分页库存列表
     */
    @GetMapping("/page")
    public Result getInventoryPage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        Page<Inventory> inventoryPage = inventoryService.getInventoryPage(page, size);
        return Result.success(inventoryPage);
    }

}

