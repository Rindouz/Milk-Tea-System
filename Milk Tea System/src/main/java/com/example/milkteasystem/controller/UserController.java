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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/milkteasystem/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private IOrdersService ordersService;

    @PostMapping
    public Result save(@RequestBody UserCreateDTO userCreateDTO) {
        try {
            userService.createUser(userCreateDTO);
            log.info("管理员创建用户: nickname={}", userCreateDTO.getNickname());
            return Result.success();
        } catch (Exception e) {
            log.error("保存用户信息失败", e);
            return Result.error("保存用户信息失败");
        }
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            boolean result = userService.updateUser(id, userUpdateDTO);
            if (result) {
                log.info("管理员更新用户: userId={}", id);
                return Result.success();
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return Result.error("更新用户信息失败");
        }
    }

    @GetMapping
    public Result getAll() {
        return Result.success(userService.getAllUserDTOs());
    }

    @GetMapping("/{id}")
    public Result getoneByid(@PathVariable Long id) {
        UserDTO userDTO = userService.getUserDTO(id);
        if (userDTO != null) {
            return Result.success(userDTO);
        } else {
            return Result.error("用户不存在");
        }
    }

    @DeleteMapping("/{id}")
    public Result deleteByid(@PathVariable Long id) {
        log.warn("管理员删除用户: userId={}", id);
        return Result.success(userService.removeById(id));
    }

    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "5") Integer pageSize,
                       @RequestParam(defaultValue = "") String phone) {
        return Result.success(
                userService.getUserDTOPage(pageNum, pageSize, phone)
        );
    }

    @GetMapping("/doLogin")
    public Result doLogin(@RequestParam String username, @RequestParam String password) {
        try {
            User user = userService.login(username, password);
            if (user == null) {
                log.warn("管理员登录失败: username={}", username);
                return Result.error("用户名或密码错误");
            }

            StpUtil.login(user.getUserId());
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            log.info("管理员登录成功: userId={}", user.getUserId());
            return Result.success(tokenInfo);
        } catch (Exception e) {
            log.error("登录失败", e);
            return Result.error("登录失败：" + e.getMessage());
        }
    }

    @PostMapping("/wechatLogin")
    public Result wechatLogin(@RequestParam String code, @RequestParam(required = false) String clientId) {
        try {
            log.info("微信登录请求: code={} clientId={}",
                    code.substring(0, Math.min(10, code.length())) + "...",
                    clientId != null ? clientId : "null");

            User user;
            // 如果提供了clientId，使用clientId作为openid（开发调试/降级）
            if (clientId != null && !clientId.isEmpty()) {
                user = userService.loginByClientId(clientId);
            } else {
                user = userService.loginByWechatCode(code);
            }

            if (user.getStatus() != null && user.getStatus() == 0) {
                return Result.error("账户已被禁用");
            }

            StpUtil.login(user.getUserId());
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

            UserDTO userDTO = userService.getUserDTO(user.getUserId());
            userDTO.setPhone(null);

            log.info("微信登录成功: userId={}, openid={}", user.getUserId(), user.getOpenid());
            User finalUser = user;
            return Result.success(new Object() {
                public final String tokenValue = tokenInfo.getTokenValue();
                public final String tokenName = tokenInfo.getTokenName();
                public final Long loginId = finalUser.getUserId();
                public final String nickname = userDTO.getNickname();
                public final String avatar = userDTO.getAvatar();
            });
        } catch (Exception e) {
            log.error("微信登录失败", e);
            return Result.error("微信登录失败: " + e.getMessage());
        }
    }

    @PostMapping("/updateWechatInfo")
    public Result updateWechatInfo(@RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            boolean result = userService.updateWechatInfo(userId, userUpdateDTO.getNickname(), userUpdateDTO.getAvatar());
            if (result) {
                log.info("用户更新微信信息: userId={}", userId);
                return Result.success();
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            log.error("更新微信用户信息失败", e);
            return Result.error("更新微信用户信息失败");
        }
    }

    @GetMapping("/isLogin")
    public String isLogin() {
        return "当前会话是否登录：" + StpUtil.isLogin();
    }

    @PostMapping("/updatePersonalInfo")
    public Result updatePersonalInfo(@RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            boolean result = userService.updateUser(userId, userUpdateDTO);
            if (result) {
                log.info("用户更新个人信息: userId={}", userId);
                return Result.success();
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            log.error("更新个人信息失败", e);
            return Result.error("更新个人信息失败");
        }
    }

    @GetMapping("/orders")
    public Result getPersonalOrders(@RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "5") Integer pageSize) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Orders::getUserId, userId);
            queryWrapper.orderByDesc(Orders::getCreateTime);
            Page<Orders> page = ordersService.page(new Page<>(pageNum, pageSize), queryWrapper);
            return Result.success(page);
        } catch (Exception e) {
            log.error("查询订单列表失败", e);
            return Result.error("查询订单列表失败");
        }
    }

    @PutMapping("/{id}/status")
    public Result updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            boolean result = userService.setUserStatus(id, status);
            if (result) {
                log.info("管理员{}用户: userId={}", status == 1 ? "启用" : "禁用", id);
                return Result.success();
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            log.error("更新用户状态失败", e);
            return Result.error("更新用户状态失败");
        }
    }

    @GetMapping("/current")
    public Result getCurrentUser() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            UserDTO userDTO = userService.getUserDTO(userId);
            return Result.success(userDTO);
        } catch (Exception e) {
            return Result.error("获取当前用户信息失败");
        }
    }
}