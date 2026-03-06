package com.example.milkteasystem.service.impl;

import com.example.milkteasystem.entity.Product;
import com.example.milkteasystem.mapper.ProductMapper;
import com.example.milkteasystem.service.IProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
