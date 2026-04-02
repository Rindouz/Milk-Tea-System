package com.example.milkteasystem.service;

import com.example.milkteasystem.entity.Inventory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 库存表 服务类
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-02-27
 */
public interface IInventoryService extends IService<Inventory> {

    /**
     * 查询商品库存
     * @param productId 商品ID
     * @return 库存数量
     */
    Integer getStockByProductId(Long productId);

    /**
     * 库存扣减（下单时）
     * @param productId 商品ID
     * @param quantity 扣减数量
     * @return 操作结果
     */
    boolean deductStock(Long productId, Integer quantity);

    /**
     * 库存回滚（订单取消时）
     * @param productId 商品ID
     * @param quantity 回滚数量
     * @return 操作结果
     */
    boolean rollbackStock(Long productId, Integer quantity);

    /**
     * 已售数量统计
     * @param productId 商品ID
     * @return 已售数量
     */
    Integer getSoldCount(Long productId);

    /**
     * 库存初始化（新增商品时）
     * @param productId 商品ID
     * @param initialStock 初始库存
     * @return 操作结果
     */
    boolean initInventory(Long productId, Integer initialStock);

    /**
     * 增加或修改库存
     * @param inventory 库存对象
     * @return 新增的库存对象
     */
    Inventory saveOrUpdateInventory(Inventory inventory);

    /**
     * 分页获取库存列表
     * @param page 当前页码
     * @param size 每页大小
     * @return 分页库存列表
     */
    Page<Inventory> getInventoryPage(Integer page, Integer size);


}

