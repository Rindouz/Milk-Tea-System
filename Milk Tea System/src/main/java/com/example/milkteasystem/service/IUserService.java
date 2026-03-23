package com.example.milkteasystem.service;

import com.example.milkteasystem.dto.UserDTO;
import com.example.milkteasystem.dto.UserCreateDTO;
import com.example.milkteasystem.dto.UserUpdateDTO;
import com.example.milkteasystem.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-02-27
 */
public interface IUserService extends IService<User> {
    // 创建用户
    User createUser(UserCreateDTO userCreateDTO);
    
    // 更新用户
    boolean updateUser(Long userId, UserUpdateDTO userUpdateDTO);
    
    // 获取用户DTO
    UserDTO getUserDTO(Long userId);
    
    // 获取所有用户DTO
    List<UserDTO> getAllUserDTOs();
    
    // 根据openid查询用户
    User getUserByOpenid(String openid);
    
    // 更新用户微信信息
    boolean updateWechatInfo(Long userId, String nickname, String avatar);
    
    // 用户登录
    User login(String username, String password);
}
