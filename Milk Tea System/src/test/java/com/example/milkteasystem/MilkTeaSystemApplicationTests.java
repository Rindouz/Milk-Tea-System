package com.example.milkteasystem;

import com.example.milkteasystem.entity.User;
import com.example.milkteasystem.service.impl.UserServiceImpl;
import com.example.milkteasystem.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MilkTeaSystemApplicationTests {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Test
    void contextLoads() {
    }
@Autowired
private RedisUtil redisUtil;
    @Test
    void testRedis() {
        List<User> userList = userServiceImpl.list();
        for (User user : userList) {
            redisUtil.set(user.getOpenid(), user);
        }

    }

}
