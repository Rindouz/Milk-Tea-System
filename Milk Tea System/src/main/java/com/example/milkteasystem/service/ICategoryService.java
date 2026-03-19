package com.example.milkteasystem.service;

import com.example.milkteasystem.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 * 商品分类表 服务类
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-02-27
 */
public interface ICategoryService extends IService<Category> {

    /**
     * 获取分类列表（按sort排序）
     * @return 分类列表
     */
    List<Category> getCategoryList();
}

