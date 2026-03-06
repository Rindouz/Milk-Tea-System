package com.example.milkteasystem.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.milkteasystem.common.Result;
import com.example.milkteasystem.entity.User;
import com.example.milkteasystem.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-03-01
 */
@RestController
@RequestMapping("/milkteasystem/user")
public class UserController {
    @Autowired
    private UserServiceImpl userService ;
//    /**
//     * 添加用户
//     * @param user
//     * @return
//     */
//    @PostMapping
//    public Result save(@RequestBody User user){
//        userService.save(user);
//        return Result.success();
//    }
    /**
     * 修改用户或添加用户
     * @param user
     * @return
     */
    @PostMapping
    public  Result saveOrupdate(@RequestBody User user){
        userService.saveOrUpdate(user);
        return Result.success();
    }
    /**
     * 查询所有用户
     * @return
     */
    @GetMapping
    public  Result getAll(){
        return Result.success(userService.list());
    }
    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result getoneByid(@PathVariable Long id){
        return Result.success(userService.getById(id));
    }
    /**
     * 删除用户
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteByid(@PathVariable Long id){
        return Result.success(userService.removeById(id));
    }
    /**
     * 用户分页
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize,@RequestParam(defaultValue ="") String phone){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (!"".equals(phone)){
            queryWrapper.eq(User::getPhone,phone);
        }
        return Result.success(
          userService.page(new Page<>(pageNum,pageSize),queryWrapper)
        );
    }

    /**
     * 登录
     * @return
     */
    // 测试登录，浏览器访问： http://localhost:8080//milkteasystem/user/doLogin?username=zhang&password=123456
// 登录接口
    @RequestMapping("doLogin")
    public Result doLogin() {
        // 第1步，先登录上
        StpUtil.login(10001);
        // 第2步，获取 Token  相关参数
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        // 第3步，返回给前端
        return Result.success(tokenInfo);
    }


    // 查询登录状态，浏览器访问： http://localhost:8080/user/isLogin
    @RequestMapping("isLogin")
    public String isLogin() {
        return "当前会话是否登录：" + StpUtil.isLogin();
    }
}
