package com.example.milkteasystem.dto;

import lombok.Data;

@Data
public class UserCreateDTO {
    private String nickname;
    private String avatar;
    private String phone; // 明文手机号
}