package com.example.milkteasystem.util;

import lombok.extern.slf4j.Slf4j; // 【关键修改1】引入 @Slf4j 注解
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis 通用工具类（基于 RedisTemplate<String, Object> 封装）
 * 适配 Spring Boot 3.2.10 + RedisConfig 配置，支持 JSON 序列化/反序列化
 * 封装高频操作：String/Hash/List/Set/ZSet/通用操作
 */
@Component
@Slf4j // 添加 @Slf4j 注解，自动生成 log 对象
public class RedisUtil {

    // 注入自定义配置的 RedisTemplate（String -> Object）
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ============================ String 类型操作 ============================

    /**
     * 设置缓存（无过期时间）
     * @param key 键
     * @param value 值（任意对象，自动 JSON 序列化）
     * @return 是否成功
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("Redis 设置缓存失败，key:{}", key, e);
            return false;
        }
    }

    /**
     * 设置缓存（带过期时间）
     * @param key 键
     * @param value 值
     * @param expireTime 过期时间（单位：秒）
     * @return 是否成功
     */
    public boolean set(String key, Object value, long expireTime) {
        try {
            redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error("Redis 设置缓存（带过期时间）失败，key:{}, expireTime:{}", key, expireTime, e);
            return false;
        }
    }

    /**
     * 获取缓存（返回 Object 类型）
     * @param key 键
     * @return 值（null 表示无数据）
     */
    public Object get(String key) {
        try {
            return key == null ? null : redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis 获取缓存失败，key:{}", key, e);
            return null;
        }
    }

    /**
     * 获取缓存（泛型重载，直接返回指定类型）
     * @param key 键
     * @param clazz 返回类型
     * @return 强类型结果
     */
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = get(key);
            return value == null ? null : clazz.cast(value);
        } catch (Exception e) {
            log.error("Redis 获取缓存（泛型）失败，key:{}, clazz:{}", key, clazz.getName(), e);
            return null;
        }
    }

    /**
     * 递增（数值类型专用）
     * @param key 键
     * @param delta 递增步长（>0）
     * @return 递增后的值
     */
    public Long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增步长必须大于0");
        }
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("Redis 递增操作失败，key:{}, delta:{}", key, delta, e);
            return 0L; // 【关键修改4】添加异常兜底返回值
        }
    }

    /**
     * 递减（数值类型专用）
     * @param key 键
     * @param delta 递减步长（>0）
     * @return 递减后的值
     */
    public Long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减步长必须大于0");
        }
        try {
            return redisTemplate.opsForValue().increment(key, -delta);
        } catch (Exception e) {
            log.error("Redis 递减操作失败，key:{}, delta:{}", key, delta, e);
            return 0L;
        }
    }

    // ============================ Hash 类型操作 ============================

    /**
     * Hash 设置值
     * @param key 键
     * @param hashKey 哈希键
     * @param value 值
     * @return 是否成功
     */
    public boolean hSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            return true;
        } catch (Exception e) {
            log.error("Redis Hash 设置值失败，key:{}, hashKey:{}", key, hashKey, e);
            return false;
        }
    }

    /**
     * Hash 获取值
     * @param key 键
     * @param hashKey 哈希键
     * @return 值
     */
    public Object hGet(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().get(key, hashKey);
        } catch (Exception e) {
            log.error("Redis Hash 获取值失败，key:{}, hashKey:{}", key, hashKey, e);
            return null;
        }
    }

    /**
     * Hash 获取值（泛型重载）
     * @param key 键
     * @param hashKey 哈希键
     * @param clazz 返回类型
     * @return 强类型结果
     */
    public <T> T hGet(String key, String hashKey, Class<T> clazz) {
        try {
            Object value = hGet(key, hashKey);
            return value == null ? null : clazz.cast(value);
        } catch (Exception e) {
            log.error("Redis Hash 获取值（泛型）失败，key:{}, hashKey:{}, clazz:{}", key, hashKey, clazz.getName(), e);
            return null;
        }
    }

    /**
     * Hash 获取所有键值对
     * @param key 键
     * @return 哈希表
     */
    public Map<Object, Object> hGetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error("Redis Hash 获取所有键值对失败，key:{}", key, e);
            return null;
        }
    }

    /**
     * Hash 删除指定键
     * @param key 键
     * @param hashKeys 哈希键（可传多个）
     * @return 删除成功的数量
     */
    public Long hDelete(String key, Object... hashKeys) {
        try {
            return redisTemplate.opsForHash().delete(key, hashKeys);
        } catch (Exception e) {
            log.error("Redis Hash 删除指定键失败，key:{}, hashKeys:{}", key, hashKeys, e);
            return 0L;
        }
    }

    /**
     * Hash 判断是否存在指定键
     * @param key 键
     * @param hashKey 哈希键
     * @return 是否存在
     */
    public boolean hHasKey(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().hasKey(key, hashKey);
        } catch (Exception e) {
            log.error("Redis Hash 判断是否存在指定键失败，key:{}, hashKey:{}", key, hashKey, e);
            return false;
        }
    }

    // ============================ List 类型操作 ============================

    /**
     * List 左压入（从头部添加）
     * @param key 键
     * @param value 值
     * @return 列表长度
     */
    public Long lPush(String key, Object value) {
        try {
            return redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            log.error("Redis List 左压入失败，key:{}, value:{}", key, value, e);
            return 0L;
        }
    }

    /**
     * List 右压入（从尾部添加）
     * @param key 键
     * @param value 值
     * @return 列表长度
     */
    public Long rPush(String key, Object value) {
        try {
            return redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            log.error("Redis List 右压入失败，key:{}, value:{}", key, value, e);
            return 0L;
        }
    }

    /**
     * List 获取指定范围的元素
     * @param key 键
     * @param start 起始索引（0 开始）
     * @param end 结束索引（-1 表示最后一个）
     * @return 元素列表
     */
    public List<Object> lRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("Redis List 获取指定范围元素失败，key:{}, start:{}, end:{}", key, start, end, e);
            return null;
        }
    }

    /**
     * List 弹出头部元素
     * @param key 键
     * @return 头部元素
     */
    public Object lPop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            log.error("Redis List 弹出头部元素失败，key:{}", key, e);
            return null;
        }
    }

    /**
     * List 弹出尾部元素
     * @param key 键
     * @return 尾部元素
     */
    public Object rPop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            log.error("Redis List 弹出尾部元素失败，key:{}", key, e);
            return null;
        }
    }

    // ============================ 通用操作 ============================

    /**
     * 判断键是否存在
     * @param key 键
     * @return 是否存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Redis 判断键是否存在失败，key:{}", key, e);
            return false;
        }
    }

    /**
     * 删除指定键
     * @param keys 键（可传多个）
     * @return 是否成功
     */
    public boolean delete(String... keys) {
        if (keys == null || keys.length == 0) {
            return false;
        }
        try {
            if (keys.length == 1) {
                return Boolean.TRUE.equals(redisTemplate.delete(keys[0]));
            }
            redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(keys));
            return true;
        } catch (Exception e) {
            log.error("Redis 删除指定键失败，keys:{}", keys, e);
            return false;
        }
    }

    /**
     * 设置键的过期时间
     * @param key 键
     * @param expireTime 过期时间（单位：秒）
     * @return 是否成功
     */
    public boolean expire(String key, long expireTime) {
        try {
            if (expireTime > 0) {
                redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("Redis 设置键过期时间失败，key:{}, expireTime:{}", key, expireTime, e);
            return false;
        }
    }

    /**
     * 获取键的过期时间
     * @param key 键
     * @return 过期时间（秒），-1 表示永久有效，-2 表示键不存在
     */
    public Long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Redis 获取键过期时间失败，key:{}", key, e);
            return -2L;
        }
    }
}