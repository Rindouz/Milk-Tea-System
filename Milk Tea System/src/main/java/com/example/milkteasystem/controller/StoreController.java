package com.example.milkteasystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.milkteasystem.common.Result;
import com.example.milkteasystem.entity.Orders;
import com.example.milkteasystem.entity.Product;
import com.example.milkteasystem.entity.Store;
import com.example.milkteasystem.service.IStoreService;
import com.example.milkteasystem.service.impl.OrdersServiceImpl;
import com.example.milkteasystem.service.impl.ProductServiceImpl;
import com.example.milkteasystem.service.impl.StoreServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 门店表 前端控制器
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-03-01
 */
@RestController
@RequestMapping("/milkteasystem/store")
public class StoreController {
    @Autowired
    private StoreServiceImpl storeService;
    @Autowired
    private OrdersServiceImpl ordersService;
    @Autowired
    private ProductServiceImpl productService;

    //- 门店列表查询
//- 门店详情查询
//- 门店CRUD（管理员）
//- 门店状态管理（启用/停用）
//- 查询门店下的商品列表

    /**
     * 检查门店是否存在
     * @param id 门店ID
     * @return Result 错误结果（门店不存在），或null（门店存在）
     */
    private Result checkStoreExist(Long id) {
        if (id == null || storeService.getById(id) == null) {
            return Result.error("门店不存在");
        }
        return null;
    }


    /*
     * 查询所有门店
     * @return 门店列表
     */
    @GetMapping
    public Result getStoreAll() {
        return Result.success(storeService.list());
    }

    //门店详情查询
    @GetMapping("/{id}")
    public Result getStoreById(@PathVariable Long id) {
        Result checkResult = checkStoreExist(id);
        if (checkResult != null) {
            return checkResult;
        }
        return Result.success(storeService.getById(id));
    }

    //门店订单查询
    @GetMapping("/{id}/orders")
    public Result getOrdersByStoreId(@PathVariable Long id) {
        Result checkResult = checkStoreExist(id);
        if (checkResult != null) {
            return checkResult;
        }
        return Result.success(ordersService.list(new LambdaQueryWrapper<Orders>().eq(Orders::getStoreId, id)));
    }

    //门店在制订单查询（小程序上显示当前正在准备的订单量后多少，用于估计等待时间）
    //调用orders-status=3的字段
    @GetMapping("/{id}/orders/Waiting")
    public Result getWaiting(@PathVariable Long id) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getStoreId, id);
        queryWrapper.eq(Orders::getOrderStatus, 3);
        long count = ordersService.count(queryWrapper);
        return Result.success(count);
    }

    //- 门店CRUD（管理员）
    @PostMapping
    public Result save(@RequestBody Store store) {
        return Result.success(storeService.save(store));
    }

    @PutMapping
    public Result update(@RequestBody Store store) {
        return Result.success(storeService.updateById(store));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        Result checkResult = checkStoreExist(id);
        if (checkResult != null) {
            return checkResult;
        }
        return Result.success(storeService.removeById(id));
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize, @RequestParam(defaultValue = "") String phone) {
        LambdaQueryWrapper<Store> queryWrapper = new LambdaQueryWrapper<>();
        if (!"".equals(phone)) {
            queryWrapper.eq(Store::getPhone, phone);
        }
        return Result.success(
                storeService.page(new Page<>(pageNum, pageSize), queryWrapper)
        );
    }

    //门店状态管理（启用/停用）
    @PutMapping("/{id}/status")
    public Result saveOrUpdateStatus(@PathVariable Long id, @RequestParam byte status) {
        Result checkResult = checkStoreExist(id);
        if (checkResult != null) {
            return checkResult;
        }
        Store store = storeService.getById(id);
        store.setStatus(status);
        storeService.saveOrUpdate(store);
        return Result.success();
    }

    //查询门店下的商品列表
    /**
     * 查询门店下的商品列表
     * @param id 门店ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 商品列表
     */
    @GetMapping("/{id}/products")
    public Result getProductsById(@PathVariable Long id,
                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Result checkResult = checkStoreExist(id);
            if (checkResult != null) {
                return checkResult;
            }

            // 构建查询条件
            LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Product::getStoreId, id);

            // 执行分页查询
            Page<Product> page = productService.page(new Page<>(pageNum, pageSize), queryWrapper);
            return Result.success(page);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询商品列表失败");
        }
    }


}