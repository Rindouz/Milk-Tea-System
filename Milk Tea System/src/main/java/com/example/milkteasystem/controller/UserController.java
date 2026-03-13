package com.example.milkteasystem.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.milkteasystem.common.Result;
import com.example.milkteasystem.entity.Orders;
import com.example.milkteasystem.entity.User;
import com.example.milkteasystem.service.impl.OrdersServiceImpl;
import com.example.milkteasystem.service.impl.UserServiceImpl;
import com.example.milkteasystem.util.AESUtil;
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
    
    @Autowired
    private OrdersServiceImpl ordersService;
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
     * 添加用户
     * @param user
     * @return
     */
    @PostMapping
    public Result save(@RequestBody User user){
        try {
            // 手机号加密
            if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                user.setPhone(AESUtil.encrypt(user.getPhone()));
            }
            userService.save(user);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("保存用户信息失败");
        }
    }
    /**
     * 修改用户
     * @param user
     * @return
     */
    @PutMapping
    public Result update(@RequestBody User user){
        try {
            // 手机号加密
            if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                user.setPhone(AESUtil.encrypt(user.getPhone()));
            }
            userService.updateById(user);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新用户信息失败");
        }
    }
    /**
     * 查询所有用户
     * @return
     */
    @GetMapping
    public Result getAll(){
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


    /**
     * 微信授权登录
     * @param code 微信登录code
     * @return 登录结果
     */
    @PostMapping("/wechatLogin")
    public Result wechatLogin(@RequestParam String code) {
        try {
            // 这里应该调用微信API获取openid，实际项目中需要实现
            // 模拟获取openid
            String openid = "mock_openid_" + System.currentTimeMillis();
            
            // 根据openid查询用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getOpenid, openid);
            User user = userService.getOne(queryWrapper);
            
            // 如果用户不存在，创建新用户
            if (user == null) {
                user = new User();
                user.setOpenid(openid);
                userService.save(user);
            }
            
            // 登录
            StpUtil.login(user.getUserId());
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            
            return Result.success(tokenInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("微信登录失败");
        }
    }

    /**
     * 更新微信用户信息
     * @param user 用户信息
     * @return 更新结果
     */
    @PostMapping("/updateWechatInfo")
    public Result updateWechatInfo(@RequestBody User user) {
        try {
            // 获取当前登录用户ID
            Long userId = StpUtil.getLoginIdAsLong();
            
            // 查询用户
            User existingUser = userService.getById(userId);
            if (existingUser == null) {
                return Result.error("用户不存在");
            }
            
            // 更新用户信息
            existingUser.setNickname(user.getNickname());
            existingUser.setAvatar(user.getAvatar());
            
            userService.updateById(existingUser);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新微信用户信息失败");
        }
    }




    // 查询登录状态，浏览器访问： http://localhost:8080/user/isLogin
    @RequestMapping("isLogin")
    public String isLogin() {
        return "当前会话是否登录：" + StpUtil.isLogin();
    }

    /**
     * 修改个人信息
     * @param user 用户信息
     * @return 更新结果
     */
    @PostMapping("/updatePersonalInfo")
    public Result updatePersonalInfo(@RequestBody User user) {
        try {
            // 获取当前登录用户ID
            Long userId = StpUtil.getLoginIdAsLong();
            
            // 查询用户
            User existingUser = userService.getById(userId);
            if (existingUser == null) {
                return Result.error("用户不存在");
            }
            
            // 更新用户信息
            if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                existingUser.setPhone(AESUtil.encrypt(user.getPhone()));
            }
            if (user.getNickname() != null) {
                existingUser.setNickname(user.getNickname());
            }
            if (user.getAvatar() != null) {
                existingUser.setAvatar(user.getAvatar());
            }
            
            userService.updateById(existingUser);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新个人信息失败");
        }
    }
    
    /**
     * 查询个人订单列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 订单列表
     */
    @GetMapping("/orders")
    public Result getPersonalOrders(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize) {
        try {
            // 获取当前登录用户ID
            Long userId = StpUtil.getLoginIdAsLong();
            
            // 查询订单列表
            LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Orders::getUserId, userId);
            queryWrapper.orderByDesc(Orders::getCreateTime);
            
            Page<Orders> page = ordersService.page(new Page<>(pageNum, pageSize), queryWrapper);
            return Result.success(page);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询订单列表失败");
        }
    }


}