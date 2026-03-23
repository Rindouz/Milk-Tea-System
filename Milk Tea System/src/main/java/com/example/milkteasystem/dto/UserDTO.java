package com.example.milkteasystem.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserDTO {
    private Long userId;
    private String nickname;
    private String avatar;
    private String phone; // 解密后的手机号
    private LocalDateTime createTime;
}