package com.example.milkteasystem.service.impl;

import com.example.milkteasystem.dto.UserDTO;
import com.example.milkteasystem.dto.UserCreateDTO;
import com.example.milkteasystem.dto.UserUpdateDTO;
import com.example.milkteasystem.entity.User;
import com.example.milkteasystem.mapper.UserMapper;
import com.example.milkteasystem.service.IUserService;
import com.example.milkteasystem.util.AESUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-02-27
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    @Transactional
    public User createUser(UserCreateDTO userCreateDTO) {
        User user = new User();
        user.setNickname(userCreateDTO.getNickname());
        user.setAvatar(userCreateDTO.getAvatar());
        
        // 手机号加密
        if (userCreateDTO.getPhone() != null && !userCreateDTO.getPhone().isEmpty()) {
            user.setPhone(AESUtil.encrypt(userCreateDTO.getPhone()));
        }
        
        save(user);
        return user;
    }

    @Override
    @Transactional
    public boolean updateUser(Long userId, UserUpdateDTO userUpdateDTO) {
        User user = getById(userId);
        if (user == null) {
            return false;
        }
        
        if (userUpdateDTO.getNickname() != null) {
            user.setNickname(userUpdateDTO.getNickname());
        }
        if (userUpdateDTO.getAvatar() != null) {
            user.setAvatar(userUpdateDTO.getAvatar());
        }
        if (userUpdateDTO.getPhone() != null && !userUpdateDTO.getPhone().isEmpty()) {
            user.setPhone(AESUtil.encrypt(userUpdateDTO.getPhone()));
        }
        
        return updateById(user);
    }

    @Override
    public UserDTO getUserDTO(Long userId) {
        User user = getById(userId);
        if (user == null) {
            return null;
        }
        return convertToDTO(user);
    }

    @Override
    public List<UserDTO> getAllUserDTOs() {
        List<User> users = list();
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public User getUserByOpenid(String openid) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOpenid, openid);
        return getOne(queryWrapper);
    }

    @Override
    @Transactional
    public boolean updateWechatInfo(Long userId, String nickname, String avatar) {
        User user = getById(userId);
        if (user == null) {
            return false;
        }
        
        user.setNickname(nickname);
        user.setAvatar(avatar);
        
        return updateById(user);
    }

    // 转换User为UserDTO
    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setNickname(user.getNickname());
        userDTO.setAvatar(user.getAvatar());
        userDTO.setCreateTime(user.getCreateTime());
        
        // 手机号解密
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            try {
                userDTO.setPhone(AESUtil.decrypt(user.getPhone()));
            } catch (Exception e) {
                userDTO.setPhone("加密手机号");
            }
        }
        
        return userDTO;
    }

    @Override
    public User login(String username, String password) {
        // 硬编码管理员账号和密码
        // 实际项目中应该从数据库查询或使用加密存储
        if ("admin".equals(username) && "admin123".equals(password)) {
            // 创建一个管理员用户对象
            User adminUser = new User();
            adminUser.setUserId(10001L); // 固定管理员ID
            adminUser.setNickname("管理员");
            adminUser.setAvatar("https://example.com/admin-avatar.png");
            return adminUser;
        }
        return null;
    }

}
