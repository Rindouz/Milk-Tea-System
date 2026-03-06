package com.example.milkteasystem.service.impl;

import com.example.milkteasystem.entity.Category;
import com.example.milkteasystem.mapper.CategoryMapper;
import com.example.milkteasystem.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品分类表 服务实现类
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-02-27
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

}
