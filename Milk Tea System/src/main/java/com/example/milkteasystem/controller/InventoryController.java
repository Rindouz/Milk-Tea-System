package com.example.milkteasystem.controller;

import com.example.milkteasystem.common.Result;
import com.example.milkteasystem.service.IInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 查询商品库存
     * @param productId 商品ID
     * @return 库存数量
     */
    @GetMapping("/stock/{productId}")
    public Result getStockByProductId(@PathVariable Long productId) {
        Integer stock = inventoryService.getStockByProductId(productId);
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
        return result ? Result.success() : Result.error("操作失败");
    }

    /**
     * 已售数量统计
     * @param productId 商品ID
     * @return 已售数量
     */
    @GetMapping("/sold/{productId}")
    public Result getSoldCount(@PathVariable Long productId) {
        Integer soldCount = inventoryService.getSoldCount(productId);
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
        return result ? Result.success() : Result.error("初始化库存失败");
    }
}

