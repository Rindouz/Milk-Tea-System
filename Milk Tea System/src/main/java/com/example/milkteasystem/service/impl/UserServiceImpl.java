package com.example.milkteasystem.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.example.milkteasystem.dto.UserDTO;
import com.example.milkteasystem.dto.UserCreateDTO;
import com.example.milkteasystem.dto.UserUpdateDTO;
import com.example.milkteasystem.entity.User;
import com.example.milkteasystem.mapper.UserMapper;
import com.example.milkteasystem.service.IUserService;
import com.example.milkteasystem.util.AESUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired(required = false)
    private WxMaService wxMaService;

    @Override
    @Transactional
    public User createUser(UserCreateDTO userCreateDTO) {
        User user = new User();
        user.setNickname(userCreateDTO.getNickname());
        user.setAvatar(userCreateDTO.getAvatar());
        user.setStatus(1);

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
        if (userUpdateDTO.getStatus() != null) {
            user.setStatus(userUpdateDTO.getStatus());
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
    public Page<UserDTO> getUserDTOPage(Integer pageNum, Integer pageSize, String phone) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (phone != null && !phone.isEmpty()) {
            queryWrapper.eq(User::getPhone, phone);
        }
        queryWrapper.orderByDesc(User::getCreateTime);
        Page<User> userPage = page(new Page<>(pageNum, pageSize), queryWrapper);
        List<UserDTO> userDTOList = userPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        Page<UserDTO> userDTOPage = new Page<>(pageNum, pageSize, userPage.getTotal());
        userDTOPage.setRecords(userDTOList);
        return userDTOPage;
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

    @Override
    @Transactional
    public boolean setUserStatus(Long userId, Integer status) {
        User user = getById(userId);
        if (user == null) {
            return false;
        }
        user.setStatus(status);
        return updateById(user);
    }

    @Override
    @Transactional
    public User loginByWechatCode(String code) {
        // 优先尝试真实微信API
        if (wxMaService != null) {
            try {
                WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
                String openid = session.getOpenid();

                User user = getUserByOpenid(openid);
                if (user == null) {
                    user = new User();
                    user.setOpenid(openid);
                    user.setNickname("微信用户");
                    user.setStatus(1);
                    save(user);
                }

                if (user.getStatus() != null && user.getStatus() == 0) {
                    throw new RuntimeException("账户已被禁用");
                }

                return user;
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                log.warn("微信API调用失败，使用降级方案: {}", e.getMessage());
            }
        }

        // 降级方案：使用稳定的clientId确定openid
        // 当微信SDK不可用时，用code前缀+长度生成可复现的openid
        String fallbackOpenid = "wx_fallback_" + (code != null ? code.hashCode() : System.currentTimeMillis());
        User user = getUserByOpenid(fallbackOpenid);
        if (user == null) {
            user = new User();
            user.setOpenid(fallbackOpenid);
            user.setNickname("微信用户");
            user.setStatus(1);
            save(user);
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new RuntimeException("账户已被禁用");
        }

        return user;
    }

    @Override
    @Transactional
    public User loginByClientId(String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            throw new IllegalArgumentException("clientId不能为空");
        }

        String openid = "wx_client_" + clientId;
        User user = getUserByOpenid(openid);
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setNickname("微信用户");
            user.setStatus(1);
            save(user);
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new RuntimeException("账户已被禁用");
        }

        return user;
    }

    @Override
    public User login(String username, String password) {
        if ("admin".equals(username) && "admin123".equals(password)) {
            User adminUser = new User();
            adminUser.setUserId(10001L);
            adminUser.setNickname("管理员");
            adminUser.setAvatar("https://example.com/admin-avatar.png");
            adminUser.setStatus(1);
            return adminUser;
        }
        return null;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setNickname(user.getNickname());
        userDTO.setAvatar(user.getAvatar());
        userDTO.setCreateTime(user.getCreateTime());
        userDTO.setStatus(user.getStatus());

        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            try {
                userDTO.setPhone(AESUtil.decrypt(user.getPhone()));
            } catch (Exception e) {
                userDTO.setPhone("加密手机号");
            }
        }

        return userDTO;
    }
}