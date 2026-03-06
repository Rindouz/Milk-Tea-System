package com.example.milkteasystem.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis 核心配置类（Spring Boot 3.2.10 适配版）
 * 特性：
 * 1. 基于 Lettuce 客户端 + 连接池（高性能、线程安全）
 * 2. JSON 序列化（替代默认 JDK 序列化，解决乱码/性能问题）
 * 3. 支持 Java 8 时间类型（LocalDateTime/LocalDate 等）
 * 4. 自定义缓存过期时间
 * 5. 无报错、无弃用 API、无冗余代码
 */
@Configuration
@EnableCaching // 启用缓存（按需开启，不需要可移除）
public class RedisConfig {

    // ========== 从配置文件读取 Redis 基础配置 ==========
    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    @Value("${spring.redis.database:0}")
    private int redisDatabase;

    @Value("${spring.redis.timeout:10000}")
    private Duration redisTimeout;

    // ========== 连接池配置 ==========
    @Value("${spring.redis.lettuce.pool.max-active:8}")
    private int maxActive;

    @Value("${spring.redis.lettuce.pool.max-idle:8}")
    private int maxIdle;

    @Value("${spring.redis.lettuce.pool.min-idle:0}")
    private int minIdle;

    @Value("${spring.redis.lettuce.pool.max-wait:-1}")
    private Duration maxWait;

    /**
     * 配置 Lettuce 连接工厂（带连接池）
     * 修复：GenericObjectPoolConfig 无 builder() 方法的问题
     */
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // 1. 配置 Redis 服务端信息
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        redisConfig.setDatabase(redisDatabase);
        if (StringUtils.hasText(redisPassword)) {
            redisConfig.setPassword(redisPassword);
        }

        // 2. 配置 Lettuce 连接池（核心修复：替换错误的 builder() 写法）
        GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(maxActive); // 最大连接数
        poolConfig.setMaxIdle(maxIdle);     // 最大空闲连接
        poolConfig.setMinIdle(minIdle);     // 最小空闲连接
        poolConfig.setMaxWait(maxWait);     // 获取连接的最大等待时间

        // 构建 Lettuce 客户端配置（链式调用，无冗余类型声明）
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(redisTimeout) // 命令超时时间
                .poolConfig(poolConfig)        // 绑定连接池配置
                .build();

        // 3. 构建 Lettuce 连接工厂
        return new LettuceConnectionFactory(redisConfig, clientConfig);
    }

    /**
     * 自定义 RedisTemplate（String -> Object）
     * 修复：setObjectMapper 弃用警告
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        // ========== 序列化配置 ==========
        // 1. String 序列化器（key/HashKey 通用）
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        // 2. JSON 序列化器（构造时传入 ObjectMapper，避免 setObjectMapper 弃用警告）
        ObjectMapper objectMapper = getObjectMapper();
        Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        // 设置 Key 序列化
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        // 设置 Value 序列化
        redisTemplate.setValueSerializer(jsonSerializer);
        redisTemplate.setHashValueSerializer(jsonSerializer);

        // 初始化模板
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 自定义 StringRedisTemplate（纯字符串场景）
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory connectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(connectionFactory);
        // 手动指定 String 序列化（默认已配置，显式声明更清晰）
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        stringRedisTemplate.setKeySerializer(stringSerializer);
        stringRedisTemplate.setValueSerializer(stringSerializer);
        stringRedisTemplate.setHashKeySerializer(stringSerializer);
        stringRedisTemplate.setHashValueSerializer(stringSerializer);
        stringRedisTemplate.afterPropertiesSet();
        return stringRedisTemplate;
    }

    /**
     * 配置 Redis 缓存管理器（按需启用）
     */
    @Bean
    public RedisCacheManager redisCacheManager(LettuceConnectionFactory connectionFactory) {
        // 1. 全局缓存配置
        ObjectMapper objectMapper = getObjectMapper();
        Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) // 默认过期时间 30 分钟
                .disableCachingNullValues()       // 禁止缓存 null 值
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));

        // 2. 自定义缓存过期时间
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheConfigs.put("userCache", defaultCacheConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigs.put("productCache", defaultCacheConfig.entryTtl(Duration.ofMinutes(10)));

        // 3. 构建缓存管理器
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }

    /**
     * 复用 ObjectMapper 配置（避免重复代码）
     */
    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 1. 配置可见性（序列化所有字段）
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 2. 支持多态类型（避免反序列化丢失类型信息）
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL
        );
        // 3. 支持 Java 8 时间类型
        objectMapper.registerModule(new JavaTimeModule());
        // 4. 关闭日期时间戳序列化
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 5. 忽略未知字段（提高兼容性）
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper;
    }
}