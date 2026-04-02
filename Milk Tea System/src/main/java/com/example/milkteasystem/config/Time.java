package com.example.milkteasystem.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 字段自动填充配置
 * 用于自动填充创建时间、更新时间等字段
 */
@Component
public class Time implements MetaObjectHandler {

    // 插入操作时自动填充


    @Override
    public void insertFill(MetaObject metaObject){
        this.strictInsertFill(
                metaObject,
                "createTime",
                LocalDateTime.class,
                LocalDateTime.now()
        );
        this.strictInsertFill(
                metaObject,
                "updateTime",
                LocalDateTime.class,
                LocalDateTime.now()
        );
    }

    // 更新操作时自动填充（如果有updateTime字段可以用）
    @Override
    public void updateFill(MetaObject metaObject) {
         this.strictUpdateFill(
             metaObject,
             "updateTime",
             LocalDateTime.class,
             LocalDateTime.now()
         );
    }
}