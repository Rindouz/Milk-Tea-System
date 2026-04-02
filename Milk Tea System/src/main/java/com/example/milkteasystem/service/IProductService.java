package com.example.milkteasystem.service;

import com.example.milkteasystem.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-02-27
 */
public interface IProductService extends IService<Product> {

    /**
     * 获取商品列表（支持按分类、门店筛选）
     * @param categoryId 分类ID
     * @param storeId 门店ID
     * @return 商品列表
     */
    List<Product> getProductList(Long categoryId, Long storeId);

    /**
     * 分页获取商品列表（支持按分类、门店筛选）
     * @param page 当前页码
     * @param size 每页大小
     * @param categoryId 分类ID
     * @param storeId 门店ID
     * @return 分页商品列表
     */
    Page<Product> getProductPage(Integer page, Integer size, Long categoryId, Long storeId);

    /**
     * 获取商品详情
     * @param productId 商品ID
     * @return 商品详情
     */
    Product getProductDetail(Long productId);

    /**
     * 商品上下架
     * @param productId 商品ID
     * @param status 状态（0下架 1上架）
     * @return 操作结果
     */
    boolean updateProductStatus(Long productId, Byte status);

    /**
     * 查询商品库存
     * @param productId 商品ID
     * @return 库存数量
     */
    Integer getProductStock(Long productId);


}

