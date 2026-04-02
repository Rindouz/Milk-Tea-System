package com.example.milkteasystem.controller;

import com.example.milkteasystem.common.Result;
import com.example.milkteasystem.entity.Product;
import com.example.milkteasystem.service.IProductService;
import com.example.milkteasystem.service.IInventoryService;
import com.example.milkteasystem.util.RedisUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    @Autowired
    private RedisUtil redisUtil;

    private static final String PRODUCT_LIST_KEY_PREFIX = "product:list:";
    private static final String PRODUCT_DETAIL_KEY_PREFIX = "product:detail:";
    private static final String PRODUCT_STOCK_KEY_PREFIX = "product:stock:";

    /**
     * 获取所有商品
     * @return 所有商品列表
     */
    @GetMapping("/all")
    public Result getAllProducts() {
        List<Product> productList = productService.list();
        return Result.success(productList);
    }
    /**
     * 获取商品列表（支持按分类、门店筛选）
     * @param categoryId 分类ID
     * @param storeId 门店ID
     * @return 商品列表
     */
    @GetMapping("/list")
    public Result getProductList(@RequestParam(required = false) Long categoryId, @RequestParam(required = false) Long storeId) {
        String key = PRODUCT_LIST_KEY_PREFIX + (categoryId != null ? categoryId : "all") + ":" + (storeId != null ? storeId : "all");
        // 尝试从缓存获取
        List<Product> productList = (List<Product>) redisUtil.get(key);
        if (productList != null) {
            return Result.success(productList);
        }
        // 缓存未命中，从数据库获取
        productList = productService.getProductList(categoryId, storeId);
        // 存入缓存，设置过期时间为10分钟
        redisUtil.set(key, productList, 600);
        return Result.success(productList);
    }
    /**
     * 分页获取商品列表（支持按分类、门店筛选）
     * @param page 当前页码
     * @param size 每页大小
     * @param categoryId 分类ID
     * @param storeId 门店ID
     * @return 分页商品列表
     */
    @GetMapping("/page")
    public Result getProductPage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size, @RequestParam(required = false) Long categoryId, @RequestParam(required = false) Long storeId) {
        Page<Product> productPage = productService.getProductPage(page, size, categoryId, storeId);
        return Result.success(productPage);
    }

    /**
     * 获取商品详情
     * @param productId 商品ID
     * @return 商品详情
     */
    @GetMapping("/detail/{productId}")
    public Result getProductDetail(@PathVariable Long productId) {
        String key = PRODUCT_DETAIL_KEY_PREFIX + productId;
        // 尝试从缓存获取
        Product product = (Product) redisUtil.get(key);
        if (product != null) {
            return Result.success(product);
        }
        // 缓存未命中，从数据库获取
        product = productService.getProductDetail(productId);
        if (product != null) {
            // 存入缓存，设置过期时间为30分钟
            redisUtil.set(key, product, 1800);
        }
        return product != null ? Result.success(product) : Result.error("商品不存在");
    }

    /**
     * 添加商品
     * @param product 商品信息
     * @return 添加结果
     */
//    @PostMapping("/add")
//    public Result addProduct(@RequestBody Product product) {
//        boolean result = productService.save(product);
//        if (result) {
//            // 初始化商品库存
//            inventoryService.initInventory(product.getProductId(), 0);
//        }
//        return result ? Result.success() : Result.error("添加商品失败");
//    }
    @PostMapping("/add")
    public Result addProduct(@RequestBody Product product) {
        // 确保前端没有传递productId
        product.setProductId(null);

        boolean result = productService.save(product);
        System.out.println("保存后商品ID: " + product.getProductId()); // 添加日志

        if (result && product.getProductId() != null) {
            // 初始化商品库存
            boolean inventoryResult = inventoryService.initInventory(product.getProductId(), 0);
            System.out.println("库存初始化结果: " + inventoryResult); // 添加日志
            // 清除商品列表缓存
            clearProductListCache();
        }
        return result && product.getProductId() != null ? Result.success() : Result.error("添加商品失败");
    }
    /**
     * 修改商品
     * @param product 商品信息
     * @return 修改结果
     */
    @PutMapping("/update")
    public Result updateProduct(@RequestBody Product product) {
        boolean result = productService.updateById(product);
        if (result) {
            // 清除商品详情缓存
            redisUtil.delete(PRODUCT_DETAIL_KEY_PREFIX + product.getProductId());
            // 清除商品列表缓存
            clearProductListCache();
        }
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
        if (result) {
            // 清除商品详情缓存
            redisUtil.delete(PRODUCT_DETAIL_KEY_PREFIX + productId);
            // 清除商品库存缓存
            redisUtil.delete(PRODUCT_STOCK_KEY_PREFIX + productId);
            // 清除商品列表缓存
            clearProductListCache();
        }
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
        if (result) {
            // 清除商品详情缓存
            redisUtil.delete(PRODUCT_DETAIL_KEY_PREFIX + productId);
            // 清除商品列表缓存
            clearProductListCache();
        }
        return result ? Result.success() : Result.error("操作失败");
    }

    /**
     * 查询商品库存
     * @param productId 商品ID
     * @return 库存数量
     */
    @GetMapping("/stock/{productId}")
    public Result getProductStock(@PathVariable Long productId) {
        String key = PRODUCT_STOCK_KEY_PREFIX + productId;
        // 尝试从缓存获取
        Integer stock = (Integer) redisUtil.get(key);
        if (stock != null) {
            return Result.success(stock);
        }
        // 缓存未命中，从数据库获取
        stock = productService.getProductStock(productId);
        // 存入缓存，设置过期时间为5分钟
        redisUtil.set(key, stock, 300);
        return Result.success(stock);
    }

    /**
     * 清除商品列表缓存
     */
    private void clearProductListCache() {
        // 这里简化处理，实际项目中可能需要更精确的缓存清理策略
        redisUtil.delete("product:list:*");
    }
}

