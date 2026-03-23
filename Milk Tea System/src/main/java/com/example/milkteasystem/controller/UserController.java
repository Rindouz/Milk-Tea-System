package com.example.milkteasystem.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.milkteasystem.common.Result;
import com.example.milkteasystem.dto.UserDTO;
import com.example.milkteasystem.dto.UserCreateDTO;
import com.example.milkteasystem.dto.UserUpdateDTO;
import com.example.milkteasystem.entity.Orders;
import com.example.milkteasystem.entity.User;
import com.example.milkteasystem.service.IOrdersService;
import com.example.milkteasystem.service.IUserService;
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
    private IUserService userService ;
    
    @Autowired
    private IOrdersService ordersService;
    /**
     * 添加用户
     * @param userCreateDTO 用户创建DTO
     * @return 操作结果
     */
    @PostMapping
    public Result save(@RequestBody UserCreateDTO userCreateDTO){
        try {
            userService.createUser(userCreateDTO);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("保存用户信息失败");
        }
    }
    /**
     * 修改用户
     * @param id 用户ID
     * @param userUpdateDTO 用户更新DTO
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO){
        try {
            boolean result = userService.updateUser(id, userUpdateDTO);
            if (result) {
                return Result.success();
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新用户信息失败");
        }
    }
    /**
     * 查询所有用户
     * @return 用户列表
     */
    @GetMapping
    public Result getAll(){
        return Result.success(userService.getAllUserDTOs());
    }
    /**
     * 根据id查询用户
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Result getoneByid(@PathVariable Long id){
        UserDTO userDTO = userService.getUserDTO(id);
        if (userDTO != null) {
            return Result.success(userDTO);
        } else {
            return Result.error("用户不存在");
        }
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
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */

    @RequestMapping("doLogin")
    public Result doLogin(@RequestParam String username, @RequestParam String password) {
        try {
            // 1. 验证用户名和密码
            User user = userService.login(username, password);
            if (user == null) {
                return Result.error("用户名或密码错误");
            }

            // 2. 登录并生成Token
            StpUtil.login(user.getUserId());
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

            // 3. 返回登录结果
            return Result.success(tokenInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("登录失败：" + e.getMessage());
        }
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
            User user = userService.getUserByOpenid(openid);
            
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
     * @param userUpdateDTO 用户更新DTO
     * @return 更新结果
     */
    @PostMapping("/updateWechatInfo")
    public Result updateWechatInfo(@RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            // 获取当前登录用户ID
            Long userId = StpUtil.getLoginIdAsLong();
            
            boolean result = userService.updateWechatInfo(userId, userUpdateDTO.getNickname(), userUpdateDTO.getAvatar());
            if (result) {
                return Result.success();
            } else {
                return Result.error("用户不存在");
            }
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
     * @param userUpdateDTO 用户更新DTO
     * @return 更新结果
     */
    @PostMapping("/updatePersonalInfo")
    public Result updatePersonalInfo(@RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            // 获取当前登录用户ID
            Long userId = StpUtil.getLoginIdAsLong();
            
            boolean result = userService.updateUser(userId, userUpdateDTO);
            if (result) {
                return Result.success();
            } else {
                return Result.error("用户不存在");
            }
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