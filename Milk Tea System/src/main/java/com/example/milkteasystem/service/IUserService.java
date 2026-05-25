package com.example.milkteasystem.service;

import com.example.milkteasystem.dto.UserDTO;
import com.example.milkteasystem.dto.UserCreateDTO;
import com.example.milkteasystem.dto.UserUpdateDTO;
import com.example.milkteasystem.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

public interface IUserService extends IService<User> {
    User createUser(UserCreateDTO userCreateDTO);
    boolean updateUser(Long userId, UserUpdateDTO userUpdateDTO);
    UserDTO getUserDTO(Long userId);
    List<UserDTO> getAllUserDTOs();
    Page<UserDTO> getUserDTOPage(Integer pageNum, Integer pageSize, String phone);
    User getUserByOpenid(String openid);
    boolean updateWechatInfo(Long userId, String nickname, String avatar);
    User login(String username, String password);
    User loginByWechatCode(String code);
    User loginByClientId(String clientId);
    boolean setUserStatus(Long userId, Integer status);
}