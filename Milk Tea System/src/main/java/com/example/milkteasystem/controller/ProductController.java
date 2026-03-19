package com.example.milkteasystem.controller;

import com.example.milkteasystem.common.Result;
import com.example.milkteasystem.entity.Product;
import com.example.milkteasystem.service.IProductService;
import com.example.milkteasystem.service.IInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-03-01
 */
@RestController
@RequestMapping("/milkteasystem/product")
public class ProductController {

    @Autowired
    private IProductService productService;

    @Autowired
    private IInventoryService inventoryService;

    /**
     * 获取商品列表（支持按分类、门店筛选）
     * @param categoryId 分类ID
     * @param storeId 门店ID
     * @return 商品列表
     */
    @GetMapping("/list")
    public Result getProductList(@RequestParam(required = false) Long categoryId, @RequestParam(required = false) Long storeId) {
        List<Product> productList = productService.getProductList(categoryId, storeId);
        return Result.success(productList);
    }

    /**
     * 获取商品详情
     * @param productId 商品ID
     * @return 商品详情
     */
    @GetMapping("/detail/{productId}")
    public Result getProductDetail(@PathVariable Long productId) {
        Product product = productService.getProductDetail(productId);
        return product != null ? Result.success(product) : Result.error("商品不存在");
    }

    /**
     * 添加商品
     * @param product 商品信息
     * @return 添加结果
     */
    @PostMapping("/add")
    public Result addProduct(@RequestBody Product product) {
        boolean result = productService.save(product);
        if (result) {
            // 初始化商品库存
            inventoryService.initInventory(product.getProductId(), 0);
        }
        return result ? Result.success() : Result.error("添加商品失败");
    }

    /**
     * 修改商品
     * @param product 商品信息
     * @return 修改结果
     */
    @PutMapping("/update")
    public Result updateProduct(@RequestBody Product product) {
        boolean result = productService.updateById(product);
        return result ? Result.success() : Result.error("修改商品失败");
    }

    /**
     * 删除商品
     * @param productId 商品ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{productId}")
    public Result deleteProduct(@PathVariable Long productId) {
        boolean result = productService.removeById(productId);
        return result ? Result.success() : Result.error("删除商品失败");
    }

    /**
     * 商品上下架
     * @param productId 商品ID
     * @param status 状态（0下架 1上架）
     * @return 操作结果
     */
    @PutMapping("/status")
    public Result updateProductStatus(@RequestParam Long productId, @RequestParam Byte status) {
        boolean result = productService.updateProductStatus(productId, status);
        return result ? Result.success() : Result.error("操作失败");
    }

    /**
     * 查询商品库存
     * @param productId 商品ID
     * @return 库存数量
     */
    @GetMapping("/stock/{productId}")
    public Result getProductStock(@PathVariable Long productId) {
        Integer stock = productService.getProductStock(productId);
        return Result.success(stock);
    }
}

