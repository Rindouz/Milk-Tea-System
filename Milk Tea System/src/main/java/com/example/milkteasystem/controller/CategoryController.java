package com.example.milkteasystem.controller;

import com.example.milkteasystem.common.Result;
import com.example.milkteasystem.entity.Category;
import com.example.milkteasystem.service.ICategoryService;
import com.example.milkteasystem.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * <p>
 * 商品分类表 前端控制器
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-03-01
 */
@RestController
@RequestMapping("/milkteasystem/category")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private RedisUtil redisUtil;

    private static final String CATEGORY_LIST_KEY = "category:list";

    /**
     * 获取分类列表（按sort排序）
     * @return 分类列表
     */
    @GetMapping("/list")
    public Result getCategoryList() {
        // 尝试从缓存获取
        List<Category> categoryList = (List<Category>) redisUtil.get(CATEGORY_LIST_KEY);
        if (categoryList != null) {
            return Result.success(categoryList);
        }
        // 缓存未命中，从数据库获取
        categoryList = categoryService.getCategoryList();
        // 存入缓存，设置过期时间为1小时
        redisUtil.set(CATEGORY_LIST_KEY, categoryList, 3600);
        return Result.success(categoryList);
    }

    /**
     * 添加分类
     * @param category 分类信息
     * @return 添加结果
     */
    @PostMapping("/add")
    public Result addCategory(@RequestBody Category category) {
        boolean result = categoryService.save(category);
        if (result) {
            // 清除分类列表缓存
            redisUtil.delete(CATEGORY_LIST_KEY);
        }
        return result ? Result.success() : Result.error("添加分类失败");
    }

    /**
     * 修改分类
     * @param category 分类信息
     * @return 修改结果
     */
    @PutMapping("/update")
    public Result updateCategory(@RequestBody Category category) {
        boolean result = categoryService.updateById(category);
        if (result) {
            // 清除分类列表缓存
            redisUtil.delete(CATEGORY_LIST_KEY);
        }
        return result ? Result.success() : Result.error("修改分类失败");
    }

    /**
     * 删除分类
     * @param categoryId 分类ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{categoryId}")
    public Result deleteCategory(@PathVariable Long categoryId) {
        boolean result = categoryService.removeById(categoryId);
        if (result) {
            // 清除分类列表缓存
            redisUtil.delete(CATEGORY_LIST_KEY);
        }
        return result ? Result.success() : Result.error("删除分类失败");
    }

    /**
     * 获取分类详情
     * @param categoryId 分类ID
     * @return 分类详情
     */
    @GetMapping("/detail/{categoryId}")
    public Result getCategoryDetail(@PathVariable Long categoryId) {
        String key = "category:detail:" + categoryId;
        // 尝试从缓存获取
        Category category = (Category) redisUtil.get(key);
        if (category != null) {
            return Result.success(category);
        }
        // 缓存未命中，从数据库获取
        category = categoryService.getById(categoryId);
        if (category != null) {
            // 存入缓存，设置过期时间为1小时
            redisUtil.set(key, category, 3600);
        }
        return category != null ? Result.success(category) : Result.error("分类不存在");
    }
}

