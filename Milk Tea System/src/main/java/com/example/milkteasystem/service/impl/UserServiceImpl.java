package com.example.milkteasystem.service.impl;

import com.example.milkteasystem.entity.User;
import com.example.milkteasystem.mapper.UserMapper;
import com.example.milkteasystem.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
