package com.example.milkteasystem;

import cn.dev33.satoken.SaManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.example.milkteasystem.mapper")
@EnableScheduling
public class MilkTeaSystemApplication {

    public static void main(String[] args) {


        SpringApplication.run(MilkTeaSystemApplication.class, args);
        System.out.println("启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
    }

}
