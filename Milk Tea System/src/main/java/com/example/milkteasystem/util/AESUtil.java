package com.example.milkteasystem.util;

import cn.dev33.satoken.secure.SaSecureUtil;

/**
 * 基于 Sa-Token 封装的 AES 加密工具类
 * 替代原有手动实现的 AESUtil
 */
public class AESUtil {
    /**
     * 加密密钥（无需关注长度，Sa-Token 自动适配）
     */
    private static final String KEY = "milk_tea_system_key";

    /**
     * AES 加密（替代原有 encrypt 方法）
     * @param data 待加密明文
     * @return 加密后字符串（Base64 编码）
     */
    public static String encrypt(String data) {
        try {
            // Sa-Token 一行完成 AES 加密，自动处理密钥长度、Base64 编码
            return SaSecureUtil.aesEncrypt(KEY, data);
        } catch (Exception e) {
            // 自定义异常处理（如打印日志、抛业务异常）
            throw new RuntimeException("AES 加密失败：" + e.getMessage(), e);
        }
    }

    /**
     * AES 解密（替代原有 decrypt 方法）
     * @param encryptedData 加密后的字符串
     * @return 解密后明文
     */
    public static String decrypt(String encryptedData) {
        try {
            // Sa-Token 一行完成 AES 解密
            return SaSecureUtil.aesDecrypt(KEY, encryptedData);
        } catch (Exception e) {
            throw new RuntimeException("AES 解密失败：" + e.getMessage(), e);
        }
    }

    // 测试方法（验证加解密一致性）
    public static void main(String[] args) {
        String text = "13800138000"; // 测试敏感数据（手机号）
        String encrypt = AESUtil.encrypt(text);
        String decrypt = AESUtil.decrypt(encrypt);
        System.out.println("加密后：" + encrypt);
        System.out.println("解密后：" + decrypt);
        System.out.println("是否一致：" + text.equals(decrypt)); // 输出 true
    }
}