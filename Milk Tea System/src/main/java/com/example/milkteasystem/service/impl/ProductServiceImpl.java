package com.example.milkteasystem.service.impl;

import com.example.milkteasystem.entity.Product;
import com.example.milkteasystem.mapper.ProductMapper;
import com.example.milkteasystem.service.IProductService;
import com.example.milkteasystem.service.IInventoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-02-27
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    @Autowired
    private IInventoryService inventoryService;

    @Override
    public List<Product> getProductList(Long categoryId, Long storeId) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        if (storeId != null) {
            queryWrapper.eq("store_id", storeId);
        }
        // 只查询上架状态的商品
        queryWrapper.eq("status", 1);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public Page<Product> getProductPage(Integer page, Integer size, Long categoryId, Long storeId) {
        Page<Product> productPage = new Page<>(page, size);
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        if (storeId != null) {
            queryWrapper.eq("store_id", storeId);
        }
        // 只查询上架状态的商品
        queryWrapper.eq("status", 1);
        return baseMapper.selectPage(productPage, queryWrapper);
    }

    @Override
    public Product getProductDetail(Long productId) {
        return baseMapper.selectById(productId);
    }

    @Override
    public boolean updateProductStatus(Long productId, Byte status) {
        Product product = new Product();
        product.setProductId(productId);
        product.setStatus(status);
        return baseMapper.updateById(product) > 0;
    }

    @Override
    public Integer getProductStock(Long productId) {
        return inventoryService.getStockByProductId(productId);
    }
}

